package com.kg.sqlengine.Schema;
import com.kg.sqlengine.DataType.dataType;

public class Column {
    public final String name;
    public final dataType type;

    public Column(String name, dataType type) {
        this.name = name;
        this.type = type;
    }
}
