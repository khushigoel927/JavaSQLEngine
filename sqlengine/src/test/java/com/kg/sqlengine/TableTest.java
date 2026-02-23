package com.kg.sqlengine;

import com.kg.sqlengine.DataType.DataType;
import com.kg.sqlengine.Schema.Column;
import com.kg.sqlengine.Storage.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    private Table table;

    @BeforeEach
    void setup() {
        table = new Table("students", List.of(
                new Column("id", DataType.INT),
                new Column("name", DataType.STRING),
                new Column("age", DataType.INT)
        ));
    }

    @Test
    void testInsertValidRow() {
        table.insert(List.of(1, "Alice", 21));
        assertEquals(1, table.getRows().size());
    }

    @Test
    void testInsertWrongTypeThrows() {
        assertThrows(RuntimeException.class, () ->
                table.insert(List.of(1, "Alice", "notAnInt"))
        );
    }

    @Test
    void testSelectWithGreaterThanFilter() {
        table.insert(List.of(1, "Alice", 21));
        table.insert(List.of(2, "Bob", 19));

        var results = table.select("name", "age", ">", 20);

        assertEquals(1, results.size());
        assertEquals("Alice", results.get(0).get("name"));
    }

    @Test
    void testSelectEqualsFilter() {
        table.insert(List.of(1, "Alice", 21));
        table.insert(List.of(2, "Bob", 19));

        var results = table.select("name", "age", "=", 19);

        assertEquals(1, results.size());
        assertEquals("Bob", results.get(0).get("name"));
    }

    @Test
    void testSelectNoMatchesReturnsEmpty() {
        table.insert(List.of(1, "Alice", 21));

        var results = table.select("name", "age", ">", 100);

        assertTrue(results.isEmpty());
    }
}
