package org.reactor.tfs;

import org.reactor.ReactorProperties;

import java.util.Properties;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

public class TfsReactorProperties extends ReactorProperties {

    private static final String PROPERTY_SERVICE_URL = "reactor.tfs.serviceUrl";
    private static final String PROPERTY_USERNAME = "reactor.tfs.username";
    private static final String PROPERTY_PASSWORD = "reactor.tfs.password";
    private static final String PROPERTY_DOMAIN = "reactor.tfs.domain";
    private static final String PROPERTY_PROJECT = "reactor.tfs.project";

    public TfsReactorProperties(Properties properties) {
        super(properties);
    }

    public String getServiceUrl() {
        return getProperty(PROPERTY_SERVICE_URL);
    }

    public String getUsername() {
        return getProperty(PROPERTY_USERNAME);
    }

    public String getPassword() {
        return getProperty(PROPERTY_PASSWORD);
    }

    public String getDomain() {
        return getProperty(PROPERTY_DOMAIN);
    }

    public String getProject() {
        return getProperty(PROPERTY_PROJECT);
    }
}
