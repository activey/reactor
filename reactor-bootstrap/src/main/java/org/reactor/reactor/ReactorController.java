package org.reactor.reactor;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static org.reactor.request.ReactorRequestInput.TRIGGER_MATCHES;
import static org.reactor.request.ReactorRequestInput.TRIGGER_MATCHES_INPUT;
import static org.reactor.utils.ClassUtils.tryCall;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

import java.util.List;
import java.util.ServiceLoader;

import org.reactor.InitializingReactor;
import org.reactor.Reactor;
import org.reactor.ReactorProperties;
import org.reactor.event.EventProducingReactor;
import org.reactor.event.ReactorEventConsumerFactory;
import org.reactor.loader.LibrariesLoader;
import org.reactor.nesting.PrintReactorsInformationReactor;
import org.reactor.request.ReactorRequestInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactorController {

    private final static Logger LOG = LoggerFactory.getLogger(ReactorController.class);

    private final List<Reactor> reactors = newArrayList();

    public ReactorController() {
        collectReactors();
    }

    public Optional<Reactor> reactorMatchingInput(ReactorRequestInput requestInput) {
        return from(reactors).filter(TRIGGER_MATCHES_INPUT(requestInput)).first();
    }

    public Optional<Reactor> reactorMatchingTrigger(String reactorTrigger) {
        return from(reactors).filter(TRIGGER_MATCHES(reactorTrigger)).first();
    }

    private void collectReactors() {
        ServiceLoader<Reactor> reactorsLoader = LibrariesLoader.loadReactors();
        reactorsLoader.forEach(this::collectReactor);

        collectReactor(new PrintReactorsInformationReactor(reactors));
    }

    @VisibleForTesting
    void collectReactor(Reactor reactor) {
        LOG.debug("Reading reactor: {}", reactor.getClass().getName());
        reactors.add(reactor);
    }

    public void initReactors(ReactorProperties reactorProperties) {
        reactors.forEach(reactor -> tryInitReactor(reactor, reactorProperties));
    }

    private void tryInitReactor(Reactor reactor, ReactorProperties reactorProperties) {
        tryCall(reactor, InitializingReactor.class, subject -> {
            LOG.debug("Initializing reactor: {}", reactor.getClass().getName());
            subject.initReactor(reactorProperties);
            return null;
        });
    }

    public void initEventConsumers(ReactorEventConsumerFactory factory) {
        reactors.stream().forEach(reactor -> createEventConsumers(reactor, factory));
    }

    private void createEventConsumers(Reactor reactor, ReactorEventConsumerFactory factory) {
        tryCall(reactor, EventProducingReactor.class, subject -> {
            subject.initReactorEventConsumers(factory);
            return null;
        });
    }
}
