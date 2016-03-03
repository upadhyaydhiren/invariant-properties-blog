#Creating an in-memory data source in Jetty

# Introduction #

Jetty is a popular light-weight webserver for integration tests - you can quickly stand up a server for testing as part of a maven build.

Container-managed data sources are a common solution to problem of how to manage resources in a deployed application - the admins can configure it via the standard appserver interface instead of dealing with a custom solution.

Can you use an in-memory database for a container-managed datasource in Jetty?

Yes!

# Details #

The solution is straightforward with a bit of research.

### jetty-env.xml ###

First, we must define the database in a jetty configuration file:

```
<?xml version="1.0"?>
<!DOCTYPE Configure PUBLIC "-//Jetty//Configure//EN" "http://www.eclipse.org/jetty/configure.dtd">

<Configure id="Server" class="org.eclipse.jetty.server.Server">
    <New id="DSTest" class="org.eclipse.jetty.plus.jndi.Resource">
        <Arg></Arg>
        <Arg>jdbc/test</Arg>
        <Arg>
            <New class="org.h2.jdbcx.JdbcDataSource">
                <Set name="URL">jdbc:h2:mem:;DB_CLOSE_DELAY=-1</Set>
                <Set name="User">sa</Set>
                <Set name="Password">sa</Set>
            </New>
        </Arg>
    </New>
</Configure>
```

### pom.xml ###

Second, we must tell the maven the necessary dependencies:

```
<!-- under <build><plugins>... -->
<!-- run as standalone application using "mvn jetty:run" -->
<plugin>
    <groupId>org.mortbay.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>8.1.10.v20130312</version>

    <configuration>
        <systemProperties>
            <systemProperty>
                <name>tapestry.execution-mode</name>
                <value>development</value>
            </systemProperty>
        </systemProperties>
        <jettyConfig>jetty/etc/jetty.xml</jettyConfig>
    </configuration>

    <dependencies>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>1.3.172</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate.javax.persistence</groupId>
            <artifactId>hibernate-jpa-2.0-api</artifactId>
            <version>1.0.1.Final</version>
        </dependency>
    </dependencies>
</plugin>
```

The dependency on the H2 database is obvious.

The dependency on the JPA2 API is more subtle and only comes up when using JPA2 as your ORM. Don't be misled by the fact it's labeled hibernate - all it contains is the standard javax.persistence API classes that would normally be provided by your app container.

### Data ###

Finally we must populate the database as necessary. Spring has good support for temporary databases, an example for Tapestry follows. (The code snippet should go into AppModule, QaModule or DevelopmentModule.)

```
// see https://tapestry.apache.org/integrating-with-jpa.html
@Contribute(EntityManagerSource.class)
public static void configurePersistenceUnitInfos(MappedConfiguration<String, PersistenceUnitConfigurer> cfg) {
    PersistenceUnitConfigurer configurer = new PersistenceUnitConfigurer() 
        final String DATASOURCE = "jdbc/test";

        /**
         * Set up database.
         */
        public void setupDatabase(Reader r) throws SQLException {
            try {
                InitialContext ctx = new InitialContext(null);
                Object o = ctx.lookup("jdbc/test");
                if (o instanceof DataSource) {
                    DataSource ds = (DataSource) o;
                    Connection conn = null;
                    try {
                        conn = ds.getConnection();
                        RunScript.execute(conn, r);
                    } finally {
                        if (conn != null) {
                            conn.close();
                        }
                    }
                }
            } catch (NamingException e) {
                System.out.println(e.getMessage());
            }
        }

        /**
         * Configure unit info.
         */
        @Override
        public void configure(TapestryPersistenceUnitInfo unitInfo) {
            InputStream is = null;
            try {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream("setup.sql");
                if (is != null) {
                    Reader r = new InputStreamReader(is);
                    setupDatabase(r);
                }
            } catch (SQLException e) {
                log.debug("setup: error setting up database: " +
                        e.getMessage());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        log.debug("setup: error loading resource 'setup.sql': " + e.getMessage());
                    }
                }
            }

            unitInfo.nonJtaDataSource(DATASOURCE);
            // we don't want to do this if we initialize DB with script.
            // unitInfo.addProperty("hibernate.hbm2ddl.auto", "create");
        }
    };
    cfg.add("defaultPU", configurer);
}
```

Note: RunScript is an H2 class.

The 'setup.sql' file can create the tables and perform initialization of 'constant' tables, e.g., enumerations used in foreign keys to ensure that the various tables only contain 'valid' data.

In practice you probably want to inject the 'configure' logic unless you always use the same database. (As mentioned elsewhere you might want to use different databases in maven integration testing and production.) Maven is smart enough to load main/test/resources/setup.sql instead of main/java/resources/setup.sql during tests.

### Testing ###

A transient in-memory database only makes sense in testing<sup>*</sup> and in my experience the cleanest approach to use custom annotations in your test code. The annotations give the name of a file, often in a zip archive, that contains the SQL to execute to set up the test. The implementation behind the annotation is straightforward. The implementation should be smart enough to not touch 'constant' tables.

TestNG is somewhat preferable to JUnit since the former allows you to bundle test methods and classes into larger collections that share resources. In this case you would only need to initialize the database once for a collection of tests, vs. initializing it for each test.

<sup>*</sup> There is an exception to this rule of thumb - transient in-memory databases are a good match for data that is intrinsically ephemeral. For instance session information - as a general rule sessions should not last forever or survive server reboots.

### Warning ###

It's important to remember that you will get a fresh database every time you start up jetty with an anonymous in-memory database BUT it's still just one database and you have to worry about concurrent tests stepping on each other.

### Using External Database ###

The in-memory database does not have to live within the Jetty server. For instance [h2-maven-plugin](https://github.com/ljnelson/h2-maven-plugin) lets you launch an H2 database before launching Jetty.

Of course it's also easy to use a standard database server. See
http://wiki.eclipse.org/Jetty/Howto/Configure_JNDI_Datasource