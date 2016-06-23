package org.reactor.renderer;

import static com.google.common.collect.FluentIterable.from;
import static java.lang.String.format;
import static org.fest.assertions.Assertions.assertThat;
import static org.reactor.renderer.JSONReactorResponseRenderer.DEFAULT_PROPERTY_HEADER;
import static org.reactor.renderer.JSONReactorResponseRenderer.KEY_ID;
import static org.reactor.renderer.JSONReactorResponseRenderer.KEY_RESPONSE;
import static org.reactor.renderer.JSONReactorResponseRenderer.KEY_VALUE;

import java.io.StringWriter;

import org.json.JSONObject;
import org.junit.Test;
import org.reactor.AbstractUnitTest;

public class JSONReactorResponseRendererTest extends AbstractUnitTest {

    private static final String HEADER_TEMPLATE = "Header template, param1 = %s, param2 = %s";
    private static final String HEADER_PARAM_1 = "param1";
    private static final String HEADER_PARAM_2 = "param2";
    private static final String LONG_LINE_1 = "longLine1";
    private static final String LONG_LINE_2 = "longLine2";
    private static final long LONG_VALUE_1 = 100L;
    private static final long LONG_VALUE_2 = 200L;
    private static final String TEXT_LINE_1 = "textLine1";
    private static final String TEXT_VALUE_1 = "text1";
    private static final String TEXT_LINE_2 = "textLine2";
    private static final String TEXT_VALUE_2 = "text2";
    private static final String DOUBLE_LINE_1 = "doubleLine1";
    private static final String DOUBLE_LINE_2 = "doubleLine2";
    private static final double DOUBLE_VALUE_1 = 1.1d;
    private static final double DOUBLE_VALUE_2 = 2.2d;

    @Test
    public void shouldHaveAllEntriesInOutputJSON() {
        // given
        JSONReactorResponseRenderer jsonRenderer = new JSONReactorResponseRenderer();

        // when render lines
        jsonRenderer.renderHeadLine(HEADER_TEMPLATE, HEADER_PARAM_1, HEADER_PARAM_2);
        jsonRenderer.renderLongLine(LONG_LINE_1, LONG_VALUE_1);
        jsonRenderer.renderLongLine(LONG_LINE_2, LONG_VALUE_2);
        jsonRenderer.renderTextLine(TEXT_LINE_1, TEXT_VALUE_1);
        jsonRenderer.renderTextLine(TEXT_LINE_2, TEXT_VALUE_2);
        jsonRenderer.renderDoubleLine(DOUBLE_LINE_1, DOUBLE_VALUE_1);
        jsonRenderer.renderDoubleLine(DOUBLE_LINE_2, DOUBLE_VALUE_2);

        // and after commit to writer
        StringWriter writer = new StringWriter();
        jsonRenderer.commit(writer);
        String jsonContents = writer.toString();
        JSONObject jsonObject = new JSONObject(jsonContents);

        // then
        assertThat(getResponseLineObject(jsonObject, DEFAULT_PROPERTY_HEADER).getString(KEY_VALUE))
                .isEqualTo(format(HEADER_TEMPLATE, HEADER_PARAM_1, HEADER_PARAM_2));
        assertThat(getResponseLineObject(jsonObject, LONG_LINE_1).getLong(KEY_VALUE)).isEqualTo(LONG_VALUE_1);
        assertThat(getResponseLineObject(jsonObject, LONG_LINE_2).getLong(KEY_VALUE)).isEqualTo(LONG_VALUE_2);

        assertThat(getResponseLineObject(jsonObject, TEXT_LINE_1).getString(KEY_VALUE)).isEqualTo(TEXT_VALUE_1);
        assertThat(getResponseLineObject(jsonObject, TEXT_LINE_2).getString(KEY_VALUE)).isEqualTo(TEXT_VALUE_2);
        assertThat(getResponseLineObject(jsonObject, DOUBLE_LINE_1).getDouble(KEY_VALUE)).isEqualTo(DOUBLE_VALUE_1);
        assertThat(getResponseLineObject(jsonObject, DOUBLE_LINE_2).getDouble(KEY_VALUE)).isEqualTo(DOUBLE_VALUE_2);
    }

    private JSONObject getResponseLineObject(JSONObject responseObject, String lineId) {
        return from(responseObject.getJSONArray(KEY_RESPONSE))
                .filter(JSONObject.class)
                .firstMatch(jsonObject -> jsonObject.getString(KEY_ID).equals(lineId))
                .orNull();
    }
}
