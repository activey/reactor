package org.reactor.actuator;

import java.io.IOException;

import org.reactor.AbstractNestingReactor;
import org.reactor.actuator.response.HealthDiskSpaceReactorResponse;
import org.reactor.actuator.response.HealthDatabaseReactorResponse;
import org.reactor.annotation.ReactOn;
import org.reactor.request.ReactorRequest;
import org.reactor.response.ReactorResponse;
import org.reactor.response.StringReactorResponse;
import org.reactor.spring.actuator.client.service.HealthService;

@ReactOn(value = "health",
    description = "Provides access to Spring Actuator health information service")
public class HealthServiceReactor extends AbstractNestingReactor {

  private HealthService healthService;

  public HealthServiceReactor(HealthService healthService) {
    this.healthService = healthService;
  }

  @ReactOn(value = "status", description = "Prints out healtheck status information")
  public ReactorResponse status(ReactorRequest<Void> voidRequest) throws IOException {
    return new StringReactorResponse(
        healthService.getHealthInformation().execute().body().getStatus());
  }

  @ReactOn(value = "database", description = "Prints out any related database information")
  public ReactorResponse database(ReactorRequest<Void> voidRequest) throws IOException {
    return new HealthDatabaseReactorResponse(healthService.getHealthInformation().execute().body().getDb());
  }

  @ReactOn(value = "disk", description = "Prints out hard disk usage information")
  public ReactorResponse disk(ReactorRequest<Void> voidRequest) throws IOException {
    return new HealthDiskSpaceReactorResponse(healthService.getHealthInformation().execute().body().getDiskSpace());
  }

}
