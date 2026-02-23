package com.kg.sqlengine.Storage;

import com.kg.sqlengine.Schema.Column;
import com.kg.sqlengine.DataType.DataType;

import java.util.*;

public class Table {
    private final String name;
    private final List<Column> columns;
    private final List<Row> rows = new ArrayList<>();

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }
    public void insert(List<Object> values) {
        if (values.size() != columns.size()) {
            throw new RuntimeException("Column count mismatch");
        }
        Row row = new Row();
        for (int i = 0; i < columns.size(); i++) {
            Column col = columns.get(i);
            Object val = values.get(i);

            validateType(col, val);
            row.set(col.name, val);
        }
        rows.add(row);
    }
    private void validateType(Column col, Object val) {
        if (col.type == DataType.INT && !(val instanceof Integer)) {
            throw new RuntimeException("Expected INT for " + col.name);
        }
        if (col.type == DataType.STRING && !(val instanceof String)) {
            throw new RuntimeException("Expected STRING for " + col.name);
        }
    }
    public List<Row> select(String column, String whereColumn,
                            String operator, Object whereValue) {

        List<Row> result = new ArrayList<>();
        for (Row row : rows) {

            if (evaluate(row, whereColumn, operator, whereValue)) {

                Row projected = new Row();
                projected.set(column, row.get(column));
                result.add(projected);
            }
        }
        return result;
    }
    private boolean evaluate(Row row, String col, String op, Object value) {

        Comparable rowValue = (Comparable) row.get(col);
        Comparable compareValue = (Comparable) value;

        int cmp = rowValue.compareTo(compareValue);

        switch (op) {
            case "=": return cmp == 0;
            case ">": return cmp > 0;
            case "<": return cmp < 0;
            default: throw new RuntimeException("Invalid operator");
        }
    }
    public List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }
}

