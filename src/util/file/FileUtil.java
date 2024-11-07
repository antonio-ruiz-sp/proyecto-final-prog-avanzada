package util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FileUtil {
    
    public static List<File> divideArchivo(File f, int numFiles) {
        List<File> files = new ArrayList<File>();
        
        if(f.exists()){
            System.out.println(" file path : " + f.getAbsolutePath());
            System.out.println("file name : " + f.getName());
            
            try(LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(f))) {
                //Skip to last line
                lineNumberReader.skip(Integer.MAX_VALUE);
                
                int numberOfLines = lineNumberReader.getLineNumber() + 1;
                System.out.println("number of lines in file: "+ numberOfLines);
                //write code here to split the file usiing Virtual Threads
                System.out.println("There should be "+ numFiles +" files with " 
                        + (numberOfLines/numFiles) + " lines each ");
                
                // write code here for splitting file into numFiles # of files using Virtual Threads
                //non-thread implementation
                
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{
            System.out.println("File does not exist...");
        }
        return files;
    }
    
}
