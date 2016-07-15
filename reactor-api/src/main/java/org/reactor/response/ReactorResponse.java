package org.reactor.response;

import org.reactor.response.renderer.ReactorResponseRenderer;

public interface ReactorResponse {

    static ReactorResponse forString(String string) {
        return new StringReactorResponse(string);
    }

    void renderResponse(ReactorResponseRenderer responseRenderer) throws Exception;
}