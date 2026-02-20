package com.kg.sqlengine.Schema;
import com.kg.sqlengine.DataType.DataType;

public class Column {
    public final String name;
    public final DataType type;

    public Column(String name, DataType type) {
        this.name = name;
        this.type = type;
    }
}
