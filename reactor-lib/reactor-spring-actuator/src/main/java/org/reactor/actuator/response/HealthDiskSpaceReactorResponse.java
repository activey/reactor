package org.reactor.actuator.response;

import org.reactor.response.ReactorResponse;
import org.reactor.response.renderer.ReactorResponseRenderer;
import org.reactor.spring.actuator.client.model.HealthDiskSpaceInformation;

/**
 * @author grabslu
 */
public class HealthDiskSpaceReactorResponse implements ReactorResponse {

  private HealthDiskSpaceInformation diskSpaceInformation;

  public HealthDiskSpaceReactorResponse(HealthDiskSpaceInformation diskSpaceInformation) {
    this.diskSpaceInformation = diskSpaceInformation;
  }

  @Override
  public void renderResponse(ReactorResponseRenderer responseRenderer) throws Exception {
    responseRenderer.renderTextLine("status", diskSpaceInformation.getStatus());
    responseRenderer.renderLongLine("free", diskSpaceInformation.getFree());
    responseRenderer.renderLongLine("total", diskSpaceInformation.getTotal());
    responseRenderer.renderLongLine("threshold", diskSpaceInformation.getThreshold());
  }
}
