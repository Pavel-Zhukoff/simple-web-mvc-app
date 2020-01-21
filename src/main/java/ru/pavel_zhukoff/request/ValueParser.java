package ru.pavel_zhukoff.request;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;

public class ValueParser {

    public static Object parseValue(String type, Object value) {
        Object obj = null;
        String stringValue = String.valueOf(value);
        ValueType valueType = ValueType.getByName(type);
        if (valueType == null) {
            return null;
        }
        switch (valueType) {
            case BOOLEAN:
                obj = Boolean.getBoolean(stringValue);
                break;
            case INTEGER:
                obj = Integer.valueOf(stringValue);
                break;
            case STRING:
                obj = stringValue;
                break;
            case DATE:
                try {
                    obj = DateUtils.parseDate(stringValue, "yyyy-mm-dd");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        return obj;
    }

    private enum ValueType {
        BOOLEAN("boolean"),
        INTEGER("int"),
        STRING("java.lang.String"),
        DATE("java.util.Date");

        private final String name;

        ValueType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static ValueType getByName(String name) {
            for (ValueType e: ValueType.values()) {
                if (e.getName().equals(name)) {
                    return e;
                }
            }
            return null;
        }

    }
}
