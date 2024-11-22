package model.file;

import model.enums.Column_Data_Type;
import util.json.JsonUtil;

public class Column {
    String name;
    int columnNo;
    Column_Data_Type type;

    public Column() {
        
    }
    
    public Column(String name, int columnNo, Column_Data_Type type) {
        this.name = name;
        this.columnNo = columnNo;
        this.type = type;
    }

    public Column(String token) {
        Column c = new Column();
        this.name = token;
    }

    public String getName() {
        return name;
    }

    public int getColumnNo() {
        return columnNo;
    }

    public Column_Data_Type getType() {
        return type;
    }
    
    @Override
    public String toString(){
        return JsonUtil.toJsonRepresentation(this);
    }
}


