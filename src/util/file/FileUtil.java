package util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FileUtil {
    
    public static List<File> divideArchivo(File f, int numFiles) throws FileNotFoundException {
        List<File> files = new ArrayList<File>();
        
        if(f.exists()){
            System.out.println(" file path : " + f.getAbsolutePath());
            System.out.println("file name : " + f.getName());
            BufferedWriter archivoDestino = null;
            try(LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(f));
                    BufferedReader archivoOrigen = new BufferedReader(new FileReader(f));
                    ) {
                //Skip to last line
                lineNumberReader.skip(Integer.MAX_VALUE);
                
                final int numberOfLinesInOrigFile = lineNumberReader.getLineNumber() + 1;
                final int numberOfLinesInNewFiles = numberOfLinesInOrigFile/numFiles;
                
                System.out.println("number of lines in file: "+ numberOfLinesInOrigFile);
                //write code here to split the file usiing Virtual Threads
                System.out.println("There should be "+ numFiles +" files with " 
                        + numberOfLinesInNewFiles + " lines each ");
                
                // write code here for splitting file into numFiles # of files using Virtual Threads
                //non-thread implementation
                //BufferedReader archivoOrigen = new BufferedReader(new FileReader(f));
               
                String linea;
                int currLine = 0;
                int currFile = 1;
                String fileBaseName= f.getName();
                File tempFile = new File("dummy");
                //archivoDestino = new BufferedWriter(new FileWriter(tempFile));
                while((linea = archivoOrigen.readLine()) != null && (currLine <= numberOfLinesInOrigFile)  ){
                    if(currLine%numberOfLinesInNewFiles ==0){
                        String fileName = f.getName();
                        String delimiter = ".";
                        System.out.println("FileName: "+fileName);
                        String[] fileParts = fileName.split(delimiter);
                        
                        System.out.println("fileParts: " + Arrays.toString(fileParts));
                        String fileNameNoExt = "testfile";//fileParts[0];
                        String fileNameExt = "txt";//fileParts[1];
                        tempFile = new File("src/files/output/"+ fileNameNoExt+"-part" +currFile +"."+fileNameExt);
                        archivoDestino = new BufferedWriter(new FileWriter(tempFile));
                        System.out.println("Temp file: "+ tempFile.getAbsolutePath());
                        currFile++;
                        
                    }
                    System.out.println("Current line: " +linea);
                    archivoDestino.write(linea + "\n");
                    //BufferedWriter archivoDestino = new BufferedWriter(new FileWriter());
                    currLine++;
                }
                
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                //do something, like closing resources
            }
            
        }else{
            System.out.println("File does not exist...");
            throw (new FileNotFoundException(f.getName()));
        }
        return files;
    }
    
}
