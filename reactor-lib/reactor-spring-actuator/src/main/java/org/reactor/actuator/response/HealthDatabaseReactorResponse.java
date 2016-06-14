package org.reactor.actuator.response;

import org.reactor.response.ReactorResponse;
import org.reactor.response.renderer.ReactorResponseRenderer;
import org.reactor.spring.actuator.client.model.HealthDatabaseInformation;

/**
 * @author grabslu
 */
public class HealthDatabaseReactorResponse implements ReactorResponse {

  private HealthDatabaseInformation databaseInformation;

  public HealthDatabaseReactorResponse(HealthDatabaseInformation databaseInformation) {
    this.databaseInformation = databaseInformation;
  }

  @Override
  public void renderResponse(ReactorResponseRenderer responseRenderer) throws Exception {
    responseRenderer.renderTextLine("status", databaseInformation.getStatus());
    responseRenderer.renderTextLine("database", databaseInformation.getDatabase());
  }
}
