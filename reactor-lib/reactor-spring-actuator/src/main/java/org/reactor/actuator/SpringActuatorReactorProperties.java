package org.reactor.actuator;

import org.reactor.ReactorProperties;

import java.util.Properties;

/**
 * @author grabslu
 */
public class SpringActuatorReactorProperties extends ReactorProperties {

  private static final String PROPERTY_SERVICE_URL = "reactor.spring.actuator.serviceUrl";

  public SpringActuatorReactorProperties(Properties properties) {
    super(properties);
  }

  public String getServiceUrl() {
    return getProperty(PROPERTY_SERVICE_URL);
  }
}
