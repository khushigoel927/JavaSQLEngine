package com.kg.sqlengine;

import com.kg.sqlengine.DataType.DataType;
import com.kg.sqlengine.Schema.Column;
import com.kg.sqlengine.Storage.Database;
import com.kg.sqlengine.Storage.Row;

import java.util.ArrayList;
import java.util.List;

public class SimpleSQLExecutor {

    private final Database db;

    public SimpleSQLExecutor(Database db) {
        this.db = db;
    }

    public void execute(String sql) {

        sql = sql.trim();

        if (sql.toUpperCase().startsWith("CREATE TABLE")) {
            handleCreate(sql);
        }
        else if (sql.toUpperCase().startsWith("INSERT INTO")) {
            handleInsert(sql);
        }
        else if (sql.toUpperCase().startsWith("SELECT")) {
            handleSelect(sql);
        }
        else {
            System.out.println("Unsupported command.");
        }
    }

    private void handleCreate(String sql) {

        // CREATE TABLE students (id INT, name STRING, age INT);

        String insideParens = sql.substring(sql.indexOf("(") + 1, sql.lastIndexOf(")"));
        String tableName = sql.split("\\s+")[2];

        String[] columnDefs = insideParens.split(",");

        List<Column> columns = new ArrayList<>();

        for (String colDef : columnDefs) {
            String[] parts = colDef.trim().split("\\s+");
            String name = parts[0];
            DataType type = DataType.valueOf(parts[1].toUpperCase());
            columns.add(new Column(name, type));
        }

        db.createTable(tableName, columns);
        System.out.println("Table created.");
    }

    private void handleInsert(String sql) {

        // INSERT INTO students VALUES (1, 'Alice', 21);

        String tableName = sql.split("\\s+")[2];

        String insideParens = sql.substring(sql.indexOf("(") + 1, sql.lastIndexOf(")"));
        String[] valuesRaw = insideParens.split(",");

        List<Object> values = new ArrayList<>();

        for (String val : valuesRaw) {
            val = val.trim();

            if (val.startsWith("'") && val.endsWith("'")) {
                values.add(val.substring(1, val.length() - 1));
            }
            else {
                values.add(Integer.parseInt(val));
            }
        }

        db.getTable(tableName).insert(values);
        System.out.println("Row inserted.");
    }

    private void handleSelect(String sql) {

        // SELECT name FROM students WHERE age > 20;

        String upper = sql.toUpperCase();

        String column = sql.split("\\s+")[1];
        String tableName = sql.split("\\s+")[3];

        String whereColumn = null;
        String operator = null;
        Object whereValue = null;

        if (upper.contains("WHERE")) {

            String wherePart = sql.substring(upper.indexOf("WHERE") + 5).trim();
            String[] parts = wherePart.split("\\s+");

            whereColumn = parts[0];
            operator = parts[1];

            String val = parts[2].replace(";", "");

            if (val.startsWith("'") && val.endsWith("'")) {
                whereValue = val.substring(1, val.length() - 1);
            }
            else {
                whereValue = Integer.parseInt(val);
            }
        }

        List<Row> results = db.getTable(tableName)
                .select(column, whereColumn, operator, whereValue);

        for (Row row : results) {
            System.out.println(row.get(column));
        }
    }
}

