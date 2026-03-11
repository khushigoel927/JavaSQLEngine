package com.kg.sqlengine;

import com.kg.sqlengine.DataType.DataType;
import com.kg.sqlengine.Schema.Column;
import com.kg.sqlengine.Storage.Database;
import com.kg.sqlengine.Storage.Row;
import com.kg.sqlengine.Storage.Table;

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
        else if (sql.toUpperCase().startsWith("DELETE FROM")) {
            handleDelete(sql);
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

            if ((val.startsWith("'") && val.endsWith("'") ) ||
                    (val.startsWith("\"") && val.endsWith("\""))) {

                values.add(val.substring(1, val.length() - 1));

            } else {
                values.add(Integer.parseInt(val));
            }
        }

        db.getTable(tableName).insert(values);
        System.out.println("Row inserted.");
    }

    private void handleSelect(String sql) {

        // SELECT name FROM students WHERE age > 20;
        // SELECT c.name FROM students INNER JOIN classes ON students.class_id = classes.id WHERE classes.id = 2;

        String upper = sql.toUpperCase();

        // detect JOIN
        if (upper.contains(" JOIN ")) {

            // naive parsing for: SELECT <col> FROM <left> INNER JOIN <right> ON <left>.<colA> = <right>.<colB> [WHERE ...];
            String selectPart = sql.substring(0, upper.indexOf("FROM")).trim();
            String column = selectPart.split("\\s+")[1];

            // get from/join/etc
            String afterFrom = sql.substring(upper.indexOf("FROM") + 4).trim();

            // expect: <left> INNER JOIN <right> ON <cond> [WHERE ...]
            String[] parts = afterFrom.split("\\s+", 5);
            String leftTable = parts[0];
            // parts[1] should be INNER
            // parts[2] should be JOIN
            String rightTable = parts[3];

            // find ON clause
            int onIdx = upper.indexOf(" ON ");
            if (onIdx == -1) {
                throw new RuntimeException("Malformed JOIN: missing ON");
            }

            String onPart = sql.substring(onIdx + 4).trim();
            String onCond = onPart.split("\\s+")[0];

            // onCond expected: left.col = right.col
            String[] onSides = onCond.split("=");
            if (onSides.length != 2) {
                throw new RuntimeException("Malformed ON condition");
            }

            String leftSide = onSides[0].trim();
            String rightSide = onSides[1].trim();

            // remove possible trailing commas/semicolons
            leftSide = leftSide.replaceAll(";", "");
            rightSide = rightSide.replaceAll(";", "");

            String leftJoinCol = leftSide.contains(".") ? leftSide.split("\\.")[1] : leftSide;
            String rightJoinCol = rightSide.contains(".") ? rightSide.split("\\.")[1] : rightSide;

            Table lt = db.getTable(leftTable);
            Table rt = db.getTable(rightTable);

            List<Row> joined = lt.join(rt, leftJoinCol, rightJoinCol);

            // optional WHERE parsing
            String whereColumn = null;
            String operator = null;
            Object whereValue = null;

            if (upper.contains("WHERE")) {
                String wherePart = sql.substring(upper.indexOf("WHERE") + 5).trim();
                String[] wparts = wherePart.split("\\s+");
                whereColumn = wparts[0].replaceAll(";", "");
                operator = wparts[1];
                whereValue = parseValue(wparts[2].replaceAll(";", ""));
            }

            // filter joined rows if WHERE present
            List<Row> filtered = new ArrayList<>();
            for (Row r : joined) {
                if (whereColumn == null) {
                    filtered.add(r);
                } else {
                    Object val = r.get(whereColumn.contains(".") ? whereColumn : (leftTable + "." + whereColumn));
                    if (val == null) {
                        val = r.get(rightTable + "." + whereColumn);
                    }
                    if (val == null) {
                        throw new RuntimeException("Unknown column in WHERE: " + whereColumn);
                    }

                    Comparable rowValue = (Comparable) val;
                    Comparable cmpValue = (Comparable) whereValue;
                    int cmp = rowValue.compareTo(cmpValue);
                    boolean ok = false;
                    switch (operator) {
                        case "=": ok = cmp == 0; break;
                        case ">": ok = cmp > 0; break;
                        case "<": ok = cmp < 0; break;
                        default: throw new RuntimeException("Invalid operator");
                    }
                    if (ok) filtered.add(r);
                }
            }

            // print selected column for each filtered row
            for (Row r : filtered) {
                Object out;
                if (column.contains(".")) {
                    out = r.get(column);
                } else {
                    // try leftTable.col then rightTable.col
                    out = r.get(leftTable + "." + column);
                    if (out == null) out = r.get(rightTable + "." + column);
                }

                if (out == null) {
                    throw new RuntimeException("Unknown column in SELECT: " + column);
                }

                System.out.println(out);
            }

            return;
        }

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

            if ((val.startsWith("'") && val.endsWith("'")) ||
                    (val.startsWith("\"") && val.endsWith("\""))) {
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

    // helper to parse a literal value
    private Object parseValue(String val) {
        val = val.trim();
        if ((val.startsWith("'") && val.endsWith("'")) ||
                (val.startsWith("\"") && val.endsWith("\""))) {
            return val.substring(1, val.length() - 1);
        }
        return Integer.parseInt(val);
    }

    // new: handle DELETE statements
    private void handleDelete(String sql) {

        // supports:
        // DELETE FROM students WHERE age > 20;
        // DELETE FROM students;

        String upper = sql.toUpperCase();
        String[] tokens = sql.split("\\s+");
        String tableName = tokens[2];

        String whereColumn = null;
        String operator = null;
        Object whereValue = null;

        if (upper.contains("WHERE")) {
            String wherePart = sql.substring(upper.indexOf("WHERE") + 5).trim();
            String[] parts = wherePart.split("\\s+");

            whereColumn = parts[0];
            operator = parts[1];

            String val = parts[2].replace(";", "").trim();

            if (val.startsWith("'") && val.endsWith("'")) {
                whereValue = val.substring(1, val.length() - 1);
            }
            else {
                whereValue = Integer.parseInt(val);
            }
        }

        int deleted = db.getTable(tableName).delete(whereColumn, operator, whereValue);
        System.out.println("Rows deleted: " + deleted);
    }
}
