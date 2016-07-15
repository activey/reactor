package org.reactor.response.list;

public interface ListElementFormatter<T> {

    String formatListElement(long elementIndex, T listElement);
}
