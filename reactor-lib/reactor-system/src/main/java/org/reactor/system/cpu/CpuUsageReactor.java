package org.reactor.system.cpu;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;

import org.reactor.AbstractAnnotatedReactor;
import org.reactor.annotation.ReactOn;
import org.reactor.request.ReactorRequest;
import org.reactor.response.ReactorResponse;

@ReactOn(value = "cpu", description = "Prints out current cpu usage")
public class CpuUsageReactor extends AbstractAnnotatedReactor<Void> {

    private final static OperatingSystemMXBean OS_BEAN = ManagementFactory
            .getPlatformMXBean(OperatingSystemMXBean.class);


    private final static MemoryMXBean MEMORY_BEAN = ManagementFactory.getPlatformMXBean(MemoryMXBean.class);

    public CpuUsageReactor() {
        super(Void.class);
    }

    @Override
    protected ReactorResponse doReact(ReactorRequest<Void> reactorRequest) {

        return responseRenderer -> {
            responseRenderer.renderTextLine("Memory usage");
            responseRenderer.renderLongLine(MEMORY_BEAN.getHeapMemoryUsage().getUsed());
            responseRenderer.renderTextLine("System load:");
            responseRenderer.renderDoubleLine(OS_BEAN.getSystemLoadAverage());
        };
    }
}