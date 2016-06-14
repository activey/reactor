package org.reactor.actuator;

import org.reactor.AbstractNestingReactor;
import org.reactor.ReactorProperties;
import org.reactor.annotation.ReactOn;
import org.reactor.spring.actuator.client.ActuatorClient;
import org.reactor.spring.actuator.client.ActuatorClientBuilder;

@ReactOn(value = "actuator", description = "Spring actuator reactor")
public class SpringActuatorReactor extends AbstractNestingReactor {

    private ActuatorClient actuatorClient;

    @Override
    protected void initNestingReactor(ReactorProperties properties) {
        initializeAcutatorClient(new SpringActuatorReactorProperties(properties));

        registerNestedReactor(new HealthServiceReactor(actuatorClient.getHealthService()));
        registerNestedReactor(new MetricsServiceReactor(actuatorClient.getMetricsService()));
    }

    private void initializeAcutatorClient(SpringActuatorReactorProperties reactorProperties) {
        this.actuatorClient = ActuatorClientBuilder
            .clientBuilder()
            .forServiceUrl(reactorProperties.getServiceUrl())
            .build();
    }
}
