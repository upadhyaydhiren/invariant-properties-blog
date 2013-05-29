package com.invariantproperties.blog.tapestry.persistence.services;

import java.io.IOException;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.jpa.EntityManagerSource;
import org.apache.tapestry5.jpa.JpaEntityPackageManager;
import org.apache.tapestry5.jpa.JpaTransactionAdvisor;
import org.apache.tapestry5.jpa.PersistenceUnitConfigurer;
import org.apache.tapestry5.jpa.TapestryPersistenceUnitInfo;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

import com.invariantproperties.blog.tapestry.persistence.dao.ServerDao;
import com.invariantproperties.blog.tapestry.persistence.dao.jpa.ServerDaoJpa;

public class AppModule {

    public static void bind(ServiceBinder binder) {
        // binder.bind(MyServiceInterface.class, MyServiceImpl.class)
        binder.bind(ServerDao.class, ServerDaoJpa.class);
    }

    public static void contributeFactoryDefaults(final MappedConfiguration<String, Object> cfg) {
        cfg.add("tapestry.page-pool.hard-limit", "20");
        cfg.add("tapestry.page-pool.soft-limit", "5");

        cfg.override(SymbolConstants.APPLICATION_VERSION, "0.0.5-SNAPSHOT");
    }

    public static void contributeApplicationDefaults(final MappedConfiguration<String, Object> cfg) {
        cfg.add(SymbolConstants.SUPPORTED_LOCALES, "en");
        // cfg.add(MetaDataConstants.SECURE_PAGE, "true");
    }

    /**
     * Example of how to create a request filter.
     * 
     * @param log
     * @return
     */
    public RequestFilter buildTimingFilter(final Logger log) {
        return new RequestFilter() {
            @Override
            public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
                final long start = System.currentTimeMillis();

                try {
                    return handler.service(request, response);
                } finally {
                    final long elapsed = System.currentTimeMillis() - start;
                    // log.info(String.format("Request time: %d ms", elapsed);
                }
            }
        };
    }

    /**
     * Example of adding a request filter to the stack. Other common uses are
     * transaction management or security.
     * 
     * The @Local annotation selects the desired service by type, but only from
     * the same module. Otherwise there would be errors if other modules contain
     * services that implement RequestFilter.
     * 
     * @param configuration
     * @param filter
     */
    public void contributeRequestHandler(final OrderedConfiguration<RequestFilter> configuration,
            @Local RequestFilter filter) {
        configuration.add("Timing", filter);
    }

    // see https://tapestry.apache.org/integrating-with-jpa.html
    @Contribute(JpaEntityPackageManager.class)
    public static void providePackages(Configuration<String> configuration) {
        configuration.add("com.invariantproperties.blog.tapestry.persistence.domain");
    }

    // see https://tapestry.apache.org/integrating-with-jpa.html
    @Contribute(EntityManagerSource.class)
    public static void configurePersistenceUnitInfos(MappedConfiguration<String,
            PersistenceUnitConfigurer> cfg) {
        PersistenceUnitConfigurer configurer =
                new PersistenceUnitConfigurer() {

                    @Override
                    public void configure(TapestryPersistenceUnitInfo unitInfo) {
                        unitInfo
                                .nonJtaDataSource("jdbc/test").addProperty("hibernate.hbm2ddl.auto",
                                        "update");
                        // .addProperty("eclipselink.ddl-generation",
                        // "create-tables")
                        // .addProperty("eclipselink.logging.level", "fine");
                    }
                };
        cfg.add("test", configurer);
    }

    // see https://tapestry.apache.org/integrating-with-jpa.html
    @Match("*Dao")
    public static void adviseTransactionally(
            JpaTransactionAdvisor advisor,
            MethodAdviceReceiver receiver) {
        advisor.addTransactionCommitAdvice(receiver);
    }

    // advice for auditing?...

    // advice for timing performance?...
}
