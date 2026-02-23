package com.kg.sqlengine;

import com.kg.sqlengine.Storage.Row;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RowTest {
    @Test
    void testSetAndGetValue() {
        Row row = new Row();
        row.set("name", "Alice");

        assertEquals("Alice", row.get("name"));
    }
}
