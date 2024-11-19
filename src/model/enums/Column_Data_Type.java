package model.enums;


public enum Column_Data_Type {
    STRING("String"),
    NUMBER_INT("Integer"), 
    NUMBER_FLOAT("Float"),
    CHAR("Char"),LIST("List"),
    STRING_DATE("Date"),
    UNKNOWN("Unknown");
    
    private final String name;       

    private Column_Data_Type(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false 
        return name.equals(otherName);
    }
    @Override
    public String toString() {
       return this.name;
    }
}
