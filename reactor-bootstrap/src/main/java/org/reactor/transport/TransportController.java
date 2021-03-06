package org.reactor.transport;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.List;
import java.util.ServiceLoader;

import org.reactor.loader.LibrariesLoader;
import org.reactor.response.ReactorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ListeningExecutorService;

public class TransportController {

    private final static Logger LOGGER = LoggerFactory.getLogger(TransportController.class);

    private final static ListeningExecutorService executorService = listeningDecorator(newFixedThreadPool(10));

    private final List<ReactorMessageTransport> transports = newArrayList();

    public static TransportController createAndLoadTransports() {
        TransportController transportController = new TransportController();
        transportController.loadTransports();
        return transportController;
    }

    private TransportController() {}

    private void loadTransports() {
        ServiceLoader<ReactorMessageTransport> transportsLoader = LibrariesLoader.loadTransports();
        if (!transportsLoader.iterator().hasNext()) {
            LOGGER.warn("No message transport found!");
            return;
        }
        transportsLoader.forEach(transport -> {
            LOGGER.debug("Loading message transport: {}", transport.getClass().getName());
            addTransport(transport);
        });
    }

    public final void startTransports(TransportProperties transportProperties,
                                      ReactorRequestHandler messageTransportProcessor) {
        transports.forEach(transport -> startTransport(transport, transportProperties, messageTransportProcessor));
        executorService.shutdown();

        new TransportsShutdownHook(this).initHook();
    }

    private void startTransport(ReactorMessageTransport transport, TransportProperties transportProperties,
                                ReactorRequestHandler messageTransportProcessor) {
        LOGGER.debug("Starting up transport: {}", transport.getClass().getName());
        addCallback(executorService.submit(new TransportInitializationCallable(transport, transportProperties,
            messageTransportProcessor)), new TransportInitializationCallback(transport));
    }

    public final void stopTransports() {
        transports.forEach(this::stopTransport);
    }

    private void stopTransport(ReactorMessageTransport transport) {
        LOGGER.debug("Shutting down transport: {}", transport.getClass().getName());
        transport.stopTransport();
        LOGGER.debug("Transport stopped: {}", transport.getClass().getName());
    }

    public void broadcast(ReactorResponse reactorResponse) {
        transports.stream().filter(ReactorMessageTransport::isRunning)
            .forEach(transport -> transport.broadcast(reactorResponse));
    }

    @VisibleForTesting
    void addTransport(ReactorMessageTransport transport) {
        transports.add(transport);
    }
}
