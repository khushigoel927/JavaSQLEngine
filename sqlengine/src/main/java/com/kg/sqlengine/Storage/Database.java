package com.kg.sqlengine.Storage;

import com.kg.sqlengine.Schema.Column;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    private final Map<String, Table> tables = new HashMap<>();

    public void createTable(String name, List<Column> columns) {
        if (tables.containsKey(name)) {
            throw new RuntimeException("Table already exists: " + name);
        }

        tables.put(name, new Table(name, columns));
    }

    public Table getTable(String name) {
        Table table = tables.get(name);

        if (table == null) {
            throw new RuntimeException("Table does not exist: " + name);
        }

        return table;
    }
}
