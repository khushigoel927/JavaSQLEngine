package com.kg.sqlengine;


import com.kg.sqlengine.DataType.DataType;
import com.kg.sqlengine.Schema.Column;
import com.kg.sqlengine.Storage.Database;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTest {

    private Database db;

    @BeforeEach
    void setup() {
        db = new Database();
    }

    @Test
    void testCreateTable() {
        db.createTable("students", List.of(
                new Column("id", DataType.INT)
        ));

        Assertions.assertDoesNotThrow(() -> db.getTable("students"));
    }

    @Test
    void testCreateDuplicateTableThrows() {
        db.createTable("students", List.of(
                new Column("id", DataType.INT)
        ));

        assertThrows(RuntimeException.class, () ->
                db.createTable("students", List.of(
                        new Column("id", DataType.INT)
                ))
        );
    }

    @Test
    void testGetNonExistentTableThrows() {
        assertThrows(RuntimeException.class, () ->
                db.getTable("ghost")
        );
    }
}

