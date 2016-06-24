package org.reactor.system.system;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import org.reactor.AbstractAnnotatedReactor;
import org.reactor.annotation.ReactOn;
import org.reactor.request.ReactorRequest;
import org.reactor.response.ReactorResponse;

@ReactOn(value = "cpu", description = "Prints out current system cpu usage")
public class CpuUsageReactor extends AbstractAnnotatedReactor<Void> {

    private static final String LINE_ID_LOAD = "cpu_load";
    private static final String LINE_ID_AVAIL = "cpu_avail";
    private static final OperatingSystemMXBean OS_BEAN = ManagementFactory
            .getPlatformMXBean(OperatingSystemMXBean.class);

    public CpuUsageReactor() {
        super(Void.class);
    }

    @Override
    protected ReactorResponse doReact(ReactorRequest<Void> reactorRequest) {
        return responseRenderer -> {
            double loadAverage =  OS_BEAN.getSystemLoadAverage();
            responseRenderer.renderDoubleLine(LINE_ID_LOAD, loadAverage);
            responseRenderer.renderDoubleLine(LINE_ID_AVAIL, 1 - loadAverage);
        };
    }
}