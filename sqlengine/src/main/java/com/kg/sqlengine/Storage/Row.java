package com.kg.sqlengine.Storage;
import java.util.HashMap;
import java.util.Map;

public class Row {

    private final Map<String, Object> values = new HashMap<>();

    public void set(String column, Object value) {
        values.put(column, value);
    }

    public Object get(String column) {
        return values.get(column);
    }

    public Map<String, Object> getAll() {
        return values;
    }
}

