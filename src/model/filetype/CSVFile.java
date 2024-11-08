package model.filetype;

import java.io.File;
import java.util.Map;
import model.enums.Column_Data_Type;

public class CSVFile extends File {
    private char columnDelimiter = ',';
    private Map<String, Column_Data_Type> columnNamesAndTypes;
    int numLines;
    
    public CSVFile(String pathname) {
        super(pathname);
    }
    
    
}

