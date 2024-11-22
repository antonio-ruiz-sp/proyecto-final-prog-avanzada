package util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import model.worker_manager.Manager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FileUtil {
    
    private static final Logger logger = LogManager.getLogger(FileUtil.class);
    private static final int availableProcessors = Runtime.getRuntime().availableProcessors();
    //private static Stream<String> fileStream;
    
    public static boolean fileExists(File f){
        return f.exists();
    }
    
    private static long numLines(File f){
        //int numberOfLinesInOrigFile = -1;
        long numLinesOrigFile = -1;
        //Assumes File exists
        //logger.debug( "file path: " + f.getAbsolutePath());
        //logger.debug(" file name: "+ f.getName());
        //BufferedWriter archivoDestino = null;
        
        try(Stream<String> fileStream = Files.lines(Paths.get(f.getAbsolutePath())) ) {
            /* method 1
            //Skip to last line
            lineNumberReader.skip(Integer.MAX_VALUE);

            numberOfLinesInOrigFile = lineNumberReader.getLineNumber() + 1;
            //final int numberOfLinesInNewFiles = numberOfLinesInOrigFile/numFiles;
            */
            
            //second method of counting lines in orig file
            //fileStream = Files.lines(Paths.get(f.getAbsolutePath())) ;
            //Lines count
            numLinesOrigFile = (int) fileStream.count();
            
            logger.debug("number of lines in file (numLinesOrigFile using Stream): "+ numLinesOrigFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            logger.error("Error FNFE:" + ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error("Error IOE: "+ex.getMessage());
        }    
        //return numberOfLinesInOrigFile;
        return numLinesOrigFile;
    }
    
    public static void replaceDelimiterInCSVFile(File origFile, String oldDelimiter, 
            String newDelimiter, boolean skipDelimiterInsideDoubleQuotes, 
            int numPartitions){
        
        logger.info("*************************************************************************");
        logger.info("* Entering replaceDelimiterInCSVFile(File, String, String, boolean, int) ");
        logger.debug("* skip commas in DoubleQuotes: " + skipDelimiterInsideDoubleQuotes);
        logger.debug("*               numPartitions: " + numPartitions);
        logger.debug("* file Name: " + origFile.getAbsolutePath());
        logger.info("*************************************************************************");

        String destFileName = "src/files/input/"+ origFile.getName().split("\\.")[0];//tempFileWithPipes";
        //File destFile = new File(destFileName);
        
        //find commas outside double quotes only
        String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        
        
        long parFileSizeInLines = numLines(origFile);
        Instant start_replace = Instant.now();
        //Manager manager = new Manager(availableProcessors * 1000 ); //fine tune this parameter
        int partitionSizeInLines = (int)parFileSizeInLines/numPartitions;
        Manager manager = new Manager( partitionSizeInLines);
        int partStart = 0;
        
        List<Partition> partitionsList = new ArrayList<>(numPartitions);
        
        //Define the partition plan
        logger.debug("Creating partition plan...");
        for(int p=0; p < numPartitions; p++){
            
            String partName = "partition-"+p;
            // logger.debug("partition name: "+partName);
            // public Partition(String partName,int begin, int end, Manager m, File origFile, String destfileName) {
            Partition partition = new Partition(partName, partStart, (partStart + partitionSizeInLines), manager, origFile, (destFileName+"-part-"+p+".csv"),regex);
            partStart += (partitionSizeInLines + 1);//so next iteration won't overlap with previous partition
            //logger.debug("adding partition: "+partition+" to List of partitions...");
            //partition.startWork();
            partitionsList.add(partition);
            
        }
        logger.debug("partition list(plan) ["+partitionsList.size()+" count] : ");
        partitionsList.forEach(p-> logger.debug(p));
        //In parallel execute the partitions work
        partitionsList.parallelStream()
                .forEach(p-> {logger.debug("start work: "+p.getPartName());p.startWork();});
        
        manager.shutdown();
        
        /*
        //String output = null;
        String linea = null;
        //File tempFile = new File(fileName);
        Instant start_replace = Instant.now();
        BufferedWriter archivoDestino = null;
        try(BufferedReader archivoOrigen = new BufferedReader(new FileReader(origFile))){
            File tempFile = new File(destFileName);
            if(!tempFile.exists()){
                logger.info("File does not exist, creating file :"+ tempFile.getName());
                tempFile.createNewFile();
            }
            int lineCounter = 0;
            archivoDestino = new BufferedWriter(new FileWriter(tempFile));
            while((linea = archivoOrigen.readLine()) != null ){
                //tempFile = new File("src/files/input/new-"+fileName );
                
                
                String newLine = linea.replaceAll(regex, "|");
                if(lineCounter<10)
                    logger.debug("pipe-separated new line: " + newLine);
                archivoDestino.write(newLine + "\n");
                lineCounter++;   
            }
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            //do something, like closing resources
            if(archivoDestino != null){
                try {
                    archivoDestino.close();
                } catch (IOException ex) {
                    logger.error( ex.getMessage());
                }
            }
        }
        
        */
        
        Instant end_replace = Instant.now();
        Duration duration = Duration.between(start_replace, end_replace);
        logger.info("Duration of replacing old delimiter ("+oldDelimiter+") "
                + "by new one: ( " +newDelimiter+") "+ duration.toMillis() +" [ms]\n" 
                + duration.toSeconds()+"[s]\n"+duration.toMinutes()+" [mins]\n");
    }
    
    public static void replaceDelimiterInCSVFile(File origFile, String oldDellimiter, 
            String newDelimiter, boolean skipDelimiterInsideDoubleQuotes){
        logger.info("Entering replaceDelimiterInCSVFile... \nskipDelimiterInsideDoubleQuotes: " + skipDelimiterInsideDoubleQuotes);
        logger.debug("fileName: " + origFile.getAbsolutePath());
        String origFileName = origFile.getName();
        String destFileName = "src/files/input/"+origFile.getName().split("\\.");
        
        //find commas outside double quotes only
        String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        //String output = null;
        String linea = null;
        //File tempFile = new File(fileName);
        Instant start = Instant.now();
        BufferedWriter archivoDestino = null;
        try(BufferedReader archivoOrigen = new BufferedReader(new FileReader(origFile))){
            File tempFile = new File(destFileName);
            if(!tempFile.exists()){
                logger.info("File does not exist, creating file :"+ tempFile.getName());
                tempFile.createNewFile();
            }
            int lineCounter = 0;
            archivoDestino = new BufferedWriter(new FileWriter(tempFile));
            while((linea = archivoOrigen.readLine()) != null ){
                //tempFile = new File("src/files/input/new-"+fileName );
                
                
                String newLine = linea.replaceAll(regex, "|");
                if(lineCounter<10)
                    logger.debug("pipe-separated new line: " + newLine);
                archivoDestino.write(newLine + "\n");
                lineCounter++;   
            }
        } catch (FileNotFoundException ex) {
            logger.error( ex.getMessage() );
        } catch (IOException ex) {
            logger.error( ex.getMessage());
        }finally{
            //do something, like closing resources
            if(archivoDestino != null){
                try {
                    archivoDestino.close();
                } catch (IOException ex) {
                    logger.error( ex.getMessage());
                }
            }
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        logger.info("Duration: " + duration.toMillis() +" [ms]" + duration.toMinutes()+" [mins]");
  
    }
    
    public static List<File> divideArchivo(File f, int numFiles) throws FileNotFoundException {
        logger.info("Entering divideArchivo...");
        List<File> files = new ArrayList<File>();
        
        if(f.exists()){
            logger.debug( "file path: " + f.getAbsolutePath());
            logger.debug("file name: "+ f.getName());
            long numberOfLinesInOrigFile = numLines(f);
            
            /*
            BufferedWriter archivoDestino = null;
            try(LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(f));
                    BufferedReader archivoOrigen = new BufferedReader(new FileReader(f));
                    ) {
                //Skip to last line
                lineNumberReader.skip(Integer.MAX_VALUE);
                
                //final int numberOfLinesInOrigFile = lineNumberReader.getLineNumber() + 1;
                
                final double numberOfLinesInNewFiles = numberOfLinesInOrigFile/numFiles;
                
                //second method of counting lines in orig file:
                long numLinesOrigFile = -1;
                try (Stream<String> fileStream = Files.lines(Paths.get(f.getName()))) {
                    //Lines count
                    numLinesOrigFile = (int) fileStream.count();
                }
                
                logger.debug("number of lines in file (numberOfLinesInOrigFile): "+ numberOfLinesInOrigFile);
                logger.debug("number of lines in file (numLinesOrigFile): "+ numLinesOrigFile);
                //TO-DO: write code here to split the file using Virtual Threads
                logger.debug("There should be "+ numFiles +" files with " 
                        + numberOfLinesInNewFiles + " lines each ");
                
                // write code here for splitting file into numFiles # of files using Virtual Threads
                //non-thread implementation
                //BufferedReader archivoOrigen = new BufferedReader(new FileReader(f));
                
                String linea;
                int currLine = 0;
                int currFile = 1;
                //String fileBaseName= f.getName();
                File tempFile = new File("dummy");
                //archivoDestino = new BufferedWriter(new FileWriter(tempFile));
                while((linea = archivoOrigen.readLine()) != null && (currLine <= numberOfLinesInOrigFile)  ){
                    if(currLine%numberOfLinesInNewFiles ==0){
                        String fileName = f.getName();
                        String delimiter = "\\.";
                        
                        String[] fileParts = fileName.split(delimiter);
                        logger.debug("fileParts: " + Arrays.toString(fileParts));
                        String fileNameNoExt = fileParts[0];
                        String fileNameExt = fileParts[1];
                        tempFile = new File("src/files/output/"+ fileNameNoExt+"-part" +currFile +"."+fileNameExt);
                        archivoDestino = new BufferedWriter(new FileWriter(tempFile));
                        logger.info("Temp file: "+ tempFile.getAbsolutePath());
                        files.add(tempFile);
                        currFile++;        
                    }
                    logger.debug("Current line: " +linea);
                    archivoDestino.write(linea + "\n");
                    //BufferedWriter archivoDestino = new BufferedWriter(new FileWriter());
                    currLine++;
                }
                
            } catch (FileNotFoundException ex) {
                logger.error(ex.getMessage());
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }finally{
                //do something, like closing resources
                if(archivoDestino != null){
                    try {
                        archivoDestino.close();
                    } catch (IOException ex) {
                        logger.error( ex.getMessage());
                    }
                }
            }*/
            
        }else{
            logger.error("File does not exist: " + f.getName());
            throw (new FileNotFoundException(f.getName()));
        }
        logger.info("exiting divideArchivo...");
        return files;
    }
    
    public static String[] readFirstNLinesofFile(File f, int n, boolean skipEmptyLines){
        String[] result = new String[n];
        int numLinesRead = 0;
        String line;
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            while((line = br.readLine())!=null && numLinesRead < n){
                logger.debug("line read: " + line);
                if(line.isBlank() && skipEmptyLines){
                    continue;
                }
                result[numLinesRead] = line;
                numLinesRead++;
            }
        } catch (FileNotFoundException ex) {
            logger.error( ex.getMessage());
        } catch (IOException ex) {
            logger.error( ex.getMessage());
        }
        return result;
    }
    
    public static String readFirstNonEmptyLine(File f) {
        return readFirstNLinesofFile(f, 1, true)[0];
    }
    
}