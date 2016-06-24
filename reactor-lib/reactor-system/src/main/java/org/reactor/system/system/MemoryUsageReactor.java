package org.reactor.system.system;

import org.reactor.AbstractAnnotatedReactor;
import org.reactor.annotation.ReactOn;
import org.reactor.request.ReactorRequest;
import org.reactor.response.ReactorResponse;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@ReactOn(value = "mem", description = "Prints out current system memory usage")
public class MemoryUsageReactor extends AbstractAnnotatedReactor<Void> {

    private static final MemoryMXBean MEMORY_BEAN = ManagementFactory.getPlatformMXBean(MemoryMXBean.class);
    private static final String LINE_ID_HEAP_USED = "heapUsed";
    private static final String LINE_ID_HEAP_MAX = "heapMax";

    public MemoryUsageReactor() {
        super(Void.class);
    }

    @Override
    protected ReactorResponse doReact(ReactorRequest<Void> reactorRequest) {

        return responseRenderer -> {
            MemoryUsage heapMemory = MEMORY_BEAN.getHeapMemoryUsage();
            responseRenderer.renderLongLine(LINE_ID_HEAP_MAX, heapMemory.getMax());
            responseRenderer.renderLongLine(LINE_ID_HEAP_USED, heapMemory.getUsed());
        };
    }
}