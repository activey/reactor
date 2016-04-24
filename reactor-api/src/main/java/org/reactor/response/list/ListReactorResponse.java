package org.reactor.response.list;

import org.reactor.response.ReactorResponse;
import org.reactor.response.renderer.ReactorResponseRenderer;

public abstract class ListReactorResponse<T> implements ReactorResponse {

    private static final String HEADER_TEMPLATE_EMPTY = "";
    private static final String LIST_LINE_ID_REACTORS_LIST = "reactors";

    private final String headerTemplate;
    private final Object[] variables;

    public ListReactorResponse(String headerTemplate, Object... variables) {
        this.headerTemplate = headerTemplate;
        this.variables = variables;
    }

    public ListReactorResponse() {
        this(HEADER_TEMPLATE_EMPTY);
    }

    @Override
    public final void renderResponse(ReactorResponseRenderer responseRenderer)  {
        responseRenderer.renderHeadLine(headerTemplate, variables);

        ListElementFormatter<T> formatter = getElementFormatter();
        Iterable<T> listElements = getElements();

        int index = 0;
        for (T listElement : listElements) {
            responseRenderer.renderListLine(LIST_LINE_ID_REACTORS_LIST, index, listElement, formatter);
            index++;
        }
    }

    protected abstract Iterable<T> getElements();

    protected abstract ListElementFormatter<T> getElementFormatter();
}
