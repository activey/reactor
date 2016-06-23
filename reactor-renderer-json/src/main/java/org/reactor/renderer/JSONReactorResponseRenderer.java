package org.reactor.renderer;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.reactor.renderer.ReactorResponseLineType.*;

import java.io.Writer;

import com.google.common.annotations.VisibleForTesting;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reactor.response.list.ListElementFormatter;
import org.reactor.response.renderer.AbstractAutoFlushableResponseRenderer;

public class JSONReactorResponseRenderer extends AbstractAutoFlushableResponseRenderer {

    @VisibleForTesting
    static final String DEFAULT_PROPERTY_HEADER = "header";
    private static final String TEMPLATE_TEXT_LINE_ID = "text_%d";
    private static final String TEMPLATE_DOUBLE_LINE_ID = "double_%d";
    private static final String TEMPLATE_LONG_ID = "long_%d";

    @VisibleForTesting
    static final String KEY_ID = "id";
    @VisibleForTesting
    static final String KEY_TYPE = "type";
    @VisibleForTesting
    static final String KEY_VALUE = "value";
    @VisibleForTesting
    static final String KEY_RESPONSE = "response";

    private long generatedTextLinesCount = 0;
    private long generatedDoubleLinesCount = 0;
    private long generatedLongLinesCount = 0;

    private JSONArray responseLinesArray;

    public JSONReactorResponseRenderer() {
        prepareJSONObject();
    }

    private void prepareJSONObject() {
        responseLinesArray = new JSONArray();
    }

    @Override
    public void renderHeadLine(String headerTemplateToBeRendered, Object... templateParameters) {
        if (isNullOrEmpty(headerTemplateToBeRendered)) {
            return;
        }
        responseLinesArray.put(createResponseLineObject(DEFAULT_PROPERTY_HEADER, HEADER, format(headerTemplateToBeRendered, templateParameters)));
    }

    @Override
    public void renderTextLine(String lineId, String templateToBeRendered, Object... templateParameters) {
        responseLinesArray.put(createResponseLineObject(lineId, TEXT, format(templateToBeRendered, templateParameters)));
        generatedTextLinesCount++;
    }

    @Override
    public void renderTextLine(String templateToBeRendered, Object... templateParameters) {
        renderTextLine(generateNextTextLineId(), templateToBeRendered, templateParameters);
    }

    private String generateNextTextLineId() {
        return format(TEMPLATE_TEXT_LINE_ID, generatedTextLinesCount + 1);
    }

    @Override
    public <T> void renderListLine(String lineId, int index, T listElement, ListElementFormatter<T> formatter) {
        // TODO rework it!
        // JSONArray jsonArray = new JSONArray();
        // if (jsonObject.has(lineId)) {
        //     jsonArray = jsonObject.getJSONArray(lineId);
        // }
        // jsonArray.put(formatter.formatListElement(index, listElement));
        // jsonObject.put(lineId, createResponseLineObject(DEFAULT_PROPERTY_HEADER, LINE, jsonArray));
    }

    @Override
    public void renderDoubleLine(String lineId, double doubleValue) {
        responseLinesArray.put(createResponseLineObject(lineId, DOUBLE, doubleValue));
        generatedDoubleLinesCount++;
    }

    @Override
    public void renderDoubleLine(double doubleValue) {
        renderDoubleLine(generateNextDoubleLineId(), doubleValue);
    }

    private String generateNextDoubleLineId() {
        return format(TEMPLATE_DOUBLE_LINE_ID, generatedDoubleLinesCount + 1);
    }

    @Override
    public void renderLongLine(String lineId, long longValue) {
        responseLinesArray.put(createResponseLineObject(lineId, LONG, longValue));
        generatedLongLinesCount++;
    }

    @Override
    public void renderLongLine(long longValue) {
        renderLongLine(generateNextLongLineId(), longValue);
    }

    private String generateNextLongLineId() {
        return format(TEMPLATE_LONG_ID, generatedLongLinesCount + 1);
    }

    private JSONObject createResponseLineObject(String lineId, ReactorResponseLineType lineType, Object format) {
        JSONObject lineObject = new JSONObject();
        lineObject.put(KEY_ID, lineId);
        lineObject.put(KEY_TYPE, lineType.getLineType());
        lineObject.put(KEY_VALUE, format);
        return lineObject;
    }

    @Override
    protected void commitBeforeFlush(Writer responseWriter) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_RESPONSE, responseLinesArray);
        jsonObject.write(responseWriter);
    }
}
