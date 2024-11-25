package model.file.type;

import model.file.Column;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import model.enums.Column_Data_Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.file.FileUtil;

public class CSVFile extends File {
    static Logger logger = LogManager.getLogger(CSVFile.class);
    
    //MetaData meta =  new MetaData();
    private final String defaultColumnDelimiter = ",";
    private String currentColumnDelimiter = defaultColumnDelimiter;
    private Map<String, Column> header;
    int fileSizeInLines;
    
    private Map<String, Column> mapStringToHeader(String[] firstNonEmptyLines) {
        logger.info("Entering mapStringToHeader...");
        HashMap<String, Column> result = new HashMap<>();
        int colNumber = 0;
        int rowNumber = 0;
        Column c = null;
        String[] columnHeader = firstNonEmptyLines[0].split(","); //First row (header)
        //String[] firstDataRow = firstNonEmptyLines[1].split(","); //First actual data row
        logger.debug("Before splitting with look ahead...");
        String[] firstDataRow = firstNonEmptyLines[1].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); //to ignore commas in double quotes
        logger.debug("First row of data: " + Arrays.asList(firstDataRow).toString());
        logger.debug("each row contains: "+columnHeader.length +"|"+columnHeader.length+ " columns");
        
        for(int i=0; i < columnHeader.length; i++){
            String cellValue = firstDataRow[i];
            String columnName = columnHeader[i];
            //int columnNumber = i;
            Column_Data_Type colType = Column_Data_Type.UNKNOWN;
            if (cellValue != null && !cellValue.isBlank()){
                
                if( isNumeric(cellValue)){
                    //now determine if its int-type or floating-point
                    if(isInt(cellValue))
                        colType = Column_Data_Type.NUMBER_INT;
                    else
                        colType = Column_Data_Type.NUMBER_FLOAT;

                }else{
                    //it is either String or list [val1, val2, ...valn] or date YYYY-MM-DD
                    if(isDate(cellValue)){
                        colType = Column_Data_Type.STRING_DATE;
                    }else if(isList(cellValue)){
                        colType = Column_Data_Type.LIST;
                        logger.debug(cellValue);
                    }else{
                        colType = Column_Data_Type.STRING;
                    }
                }
            }
            
            c = new Column(columnName, i, colType);
            int columnsToDisplay = 10;
            logger.debug("logging only first " +columnsToDisplay);
            if(i<columnsToDisplay){ //log only the first columnsToDisplay columns
                //logger.debug("");
                logger.debug("Adding column: " + c + " to header");

            }
            result.put(columnName, c);
        }
        logger.info("Exiting mapStringToHeader...");

        return result;
    }
    
    public static boolean isCSVFile(File f){
        logger.info("Entering isCSVFile...");
        boolean result = true;
        String fileName = f.getName();
        //logger.debug("File name: " + fileName + " FILE NAME: " +fileName.toUpperCase());
        //check extension, columns header, delimiter
        if ( (!fileName.toUpperCase().endsWith("CSV") ) ){
            logger.error("File does not have csv extension.");
            return false;
        }
        logger.info("file has csv|CSV extension");
       /*TO-DO validate file content
        String[] firstTwoNonEmptyLines = FileUtil.readFirstNLinesofFile(f, 2 , true);
        if( firstTwoNonEmptyLines != null && firstTwoNonEmptyLines[0].split(",").length < 1){
            logger.debug("Either there are no two non-empty lines or there is less that one column...");
            //result = false;
            return false;
        }
        */
        
        //Map<String, Column> header = mapStringToHeader( firstTwoNonEmptyLines );
        logger.info("Exiting isCSVFile...");
        return result;
    }
    
    public int columnsInHeader(){
        return header.size();
    }
    
    public CSVFile(String pathName) {
        super(pathName);
        logger.info("Entering constructor of CSVFile(String pathName)");
        File f = new File(pathName);
        String[] firstTwoNonEmptyLines = FileUtil.readFirstNLinesofFile(f, 2, true);
        this.header = mapStringToHeader(firstTwoNonEmptyLines);
        logger.info("Exiting constructor of CSVFile(String pathName)");
    }    

    private boolean isNumeric(String parseInt) {
        
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        return pattern.matcher(parseInt).matches();
    }

    private boolean isInt(String token) {
        Pattern pattern = Pattern.compile("\\d+");
        return pattern.matcher(token).matches();
    }    
    private boolean isList(String token) {
        
        /*
        String test="\"[345.44, -23456.6, 0.3456, 0.34567]\"";
        Pattern pattern = Pattern.compile("\"\\[(.*?)\\]\""); //   
        logger.debug("token: " + token);
        return pattern.matcher(token).matches();
        */
        Pattern pattern = Pattern.compile("\"([^\"]*)\""); //starts and ends with double quotes
        
        return pattern.matcher(token).matches();

    }

    private boolean isDate(String token) {
        Pattern pattern = Pattern.compile("^([0-9]{4})-([0-9]{2})-([0-9]{2})$");
        return pattern.matcher(token).matches();
    }
    private static class MetaData {
        List<Column> header = null;
        String defaultDelimiter = ",";
        int numRows = 0;
        public MetaData(List<Column> h, String defaultDelim, int numRows) {
            this.header= h;
            this.defaultDelimiter = defaultDelim;
            this.numRows = numRows;
        }

        public List<Column> getHeader() {
            return header;
        }

        public String getDefaultDelimiter() {
            return defaultDelimiter;
        }

        public int getNumRows() {
            return numRows;
        }

        public void setHeader(List<Column> header) {
            this.header = header;
        }
    }
    
}

