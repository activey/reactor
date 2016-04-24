package org.reactor.response.renderer.simple;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.Writer;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.reactor.response.list.ListElementFormatter;
import org.reactor.response.renderer.AbstractAutoFlushableResponseRenderer;

public class SimpleReactorResponseRenderer extends AbstractAutoFlushableResponseRenderer {

    private static final String TEMPLATE_TEXT_LINE_ID = "text_%d";
    private static final String TEMPLATE_DOUBLE_LINE_ID = "double_%d";
    private static final String TEMPLATE_LONG_ID = "long_%d";

    private long generatedTextLinesCount = 0;
    private long generatedDoubleLinesCount = 0;
    private long generatedLongLinesCount = 0;

    private String header;
    private Multimap<String, String> responseElements = LinkedListMultimap.create();

    @Override
    public void renderHeadLine(String headerTemplateToBeRendered, Object... templateParameters) {
        header = format(headerTemplateToBeRendered, templateParameters);
    }

    @Override
    public void renderTextLine(String lineId, String templateToBeRendered, Object... templateParameters) {
        responseElements.put(lineId, format(templateToBeRendered, templateParameters));
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
        responseElements.put(lineId, formatter.formatListElement(index, listElement));
    }

    @Override
    public void renderDoubleLine(String lineId, double doubleValue) {
        responseElements.put(lineId, Double.toString(doubleValue));
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
        responseElements.put(lineId, Long.toString(longValue));
        generatedLongLinesCount++;
    }

    @Override
    public void renderLongLine(long longValue) {
        renderLongLine(generateNextLongLineId(), longValue);
    }

    private String generateNextLongLineId() {
        return format(TEMPLATE_LONG_ID, generatedLongLinesCount + 1);
    }

    @Override
    protected void commitBeforeFlush(Writer responseWriter) {
        PrintWriter printWriter = new PrintWriter(responseWriter);
        if (!isNullOrEmpty(header)) {
            printWriter.print(header);
        }
        responseElements.values().forEach(printWriter::print);
    }
}
