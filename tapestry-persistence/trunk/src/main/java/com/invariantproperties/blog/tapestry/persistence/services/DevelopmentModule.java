package com.invariantproperties.blog.tapestry.persistence.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;

/**
 * This module is automatically included as part of the Tapestry IoC Registry if
 * <em>tapestry.execution.mode</em> includes <code>development</code>
 * 
 * @author bgiles
 */
public class DevelopmentModule {
    public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
        // The factory default is true but during the early stages of
        // an application overriding this value is a good idea. This value
        // can be overridden on the command line with
        // -Dtapestry.production-mode=false.
        configuration.add(SymbolConstants.PRODUCTION_MODE, false);

        // The application version number is incorporated into URLs for
        // some assets. Web browsers will cache assets because of
        // the HTTP header says they are long-lived. This can cause problems
        // during development unless we force a version change.
        configuration.add(SymbolConstants.APPLICATION_VERSION, "0.0.1-SNAPSHOT-DEV");
    }
}
