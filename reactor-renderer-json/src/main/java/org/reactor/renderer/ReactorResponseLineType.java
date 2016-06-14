package org.reactor.renderer;

public enum  ReactorResponseLineType {

    HEADER("header"),
    TEXT("text"),
    DOUBLE("double"),
    LONG("long"),
    LINE("line");

    private final String lineType;

    ReactorResponseLineType(String lineType) {
        this.lineType = lineType;
    }

    public String getLineType() {
        return lineType;
    }
}
