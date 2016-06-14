package org.reactor.actuator.response;

import org.reactor.response.ReactorResponse;
import org.reactor.response.renderer.ReactorResponseRenderer;
import org.reactor.spring.actuator.client.model.MetricsInformation;

/**
 * @author grabslu
 */
public class MetricsMemoryUsageResponse implements ReactorResponse {

  private long memoryTotal;
  private long memoryFree;

  public MetricsMemoryUsageResponse(MetricsInformation metricsInformation) {
    memoryFree = metricsInformation.getMemoryFree();
    memoryTotal = metricsInformation.getMemoryTotal();
  }

  @Override
  public void renderResponse(ReactorResponseRenderer responseRenderer) throws Exception {
    responseRenderer.renderLongLine("total", memoryTotal);
    responseRenderer.renderLongLine("free", memoryFree);
  }
}
