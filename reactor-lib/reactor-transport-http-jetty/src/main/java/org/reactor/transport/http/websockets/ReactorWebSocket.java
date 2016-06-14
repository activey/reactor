package org.reactor.transport.http.websockets;

import static com.google.common.util.concurrent.Futures.addCallback;
import static java.lang.String.format;
import static org.reactor.transport.http.websockets.WebSocketResponseType.RESPONSE;

import java.io.IOException;
import java.io.Writer;

import com.google.common.util.concurrent.FutureCallback;
import org.eclipse.jetty.websocket.WebSocket;
import org.reactor.request.ReactorRequestInput;
import org.reactor.response.renderer.ReactorResponseRenderer;
import org.reactor.response.renderer.simple.SimpleReactorResponseRenderer;
import org.reactor.transport.ReactorRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactorWebSocket implements WebSocket.OnTextMessage {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReactorWebSocket.class);

    private static final String SENDER = "UNKNOWN";
    private static final String MESSAGE_INTERACTIVE_TOGGLE = "!interactive";
    private static final String MESSAGE_VERBOSE_TOGGLE = "!verbose";
    private static final String MESSAGE_TOGGLE_INTERACTIVE = "Turning %s interactive mode for session.";
    private static final String MESSAGE_TOGGLE_VERBOSE = "Turning %s verbose mode for session.";

    private final ReactorRequestHandler requestHandler;
    private final WebSocketsConnectionListener connectionListener;
    private boolean interactive;
    private boolean verbose;

    private Connection connection = null;

    public ReactorWebSocket(ReactorRequestHandler requestHandler, WebSocketsConnectionListener connectionListener) {
        this.requestHandler = requestHandler;
        this.connectionListener = connectionListener;
    }

    @Override
    public void onMessage(String message) {
        if (isInteractiveToggleMessage(message)) {
            toggleSessionInteractive();
            return;
        }
        if (isVerboseToggleMessage(message)) {
            toggleSessionVerbose();
            return;
        }

        ReactorRequestInput requestInput = new ReactorRequestInput(message);
        requestInput.setInteractive(interactive);
        ReactorResponseRenderer responseRenderer = new SimpleReactorResponseRenderer(verbose);
        addCallback(requestHandler.handleReactorRequest(requestInput, SENDER, responseRenderer), new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                responseRenderer.commit(new WebSocketResponseWriter(connection, RESPONSE));
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("An error occurred while processing request.", throwable);
            }
        });

    }

    private boolean isInteractiveToggleMessage(String textMessage) {
        return MESSAGE_INTERACTIVE_TOGGLE.equals(textMessage);
    }

    private void toggleSessionInteractive() {
        if (!interactive) {
            sendWebSocketText(MESSAGE_TOGGLE_INTERACTIVE, "ON");
        } else {
            sendWebSocketText(MESSAGE_TOGGLE_INTERACTIVE, "OFF");
        }
        interactive = !interactive;
    }

    private boolean isVerboseToggleMessage(String textMessage) {
        return MESSAGE_VERBOSE_TOGGLE.equals(textMessage);
    }

    private void toggleSessionVerbose() {
        if (!verbose) {
            sendWebSocketText(MESSAGE_TOGGLE_VERBOSE, "ON");
        } else {
            sendWebSocketText(MESSAGE_TOGGLE_VERBOSE, "OFF");
        }
        verbose = !verbose;
    }

    private void sendWebSocketText(String textTemplate, String... parameters) {
        Writer writer = new WebSocketResponseWriter(connection, RESPONSE);
        try {
            writer.write(format(textTemplate, parameters));
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("An error occurred while sending response.");
        }
    }

    @Override
    public void onOpen(Connection connection) {
        this.connection = connection;
        connectionListener.connectionOpened(connection);
    }

    @Override
    public void onClose(int closeCode, String message) {
        connectionListener.connectionClosed(connection);
        this.connection = null;
    }
}
