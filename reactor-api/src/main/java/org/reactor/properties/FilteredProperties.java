package org.reactor.properties;

import java.util.Properties;
import java.util.function.Predicate;

public class FilteredProperties extends Properties {

    public static final Predicate<String> propertyKeyStartPredicate(final String prefix) {
        return propertyKey -> propertyKey.startsWith(prefix);
    }


    public FilteredProperties(Properties properties, Predicate<String> propertyKeyPredicate) {
        properties.stringPropertyNames().stream()
                .filter(propertyKeyPredicate)
                .forEach(filteredKey -> setProperty(filteredKey, properties.getProperty(filteredKey)));

    }
}
