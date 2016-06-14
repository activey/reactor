package org.reactor.actuator;

import java.io.IOException;

import org.reactor.AbstractNestingReactor;
import org.reactor.actuator.response.MetricsMemoryUsageResponse;
import org.reactor.annotation.ReactOn;
import org.reactor.request.ReactorRequest;
import org.reactor.response.ReactorResponse;
import org.reactor.spring.actuator.client.service.MetricsService;

@ReactOn(value = "metrics",
    description = "Provides access to Spring Actuator metrics information service")
public class MetricsServiceReactor extends AbstractNestingReactor {

  private MetricsService metricsService;

  public MetricsServiceReactor(MetricsService metricsService) {
    this.metricsService = metricsService;
  }

  @ReactOn(value = "memory", description = "Prints out memory usage information")
  public ReactorResponse status(ReactorRequest<Void> voidRequest) throws IOException {
    return new MetricsMemoryUsageResponse(metricsService.getMetricsInformation().execute().body());
  }

}
