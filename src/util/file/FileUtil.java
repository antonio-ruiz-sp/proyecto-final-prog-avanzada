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
import java.util.Map;
import java.util.stream.Stream;
import model.worker_manager.Manager;
import model.file_object.PersonalInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FileUtil {
    
    private static final Logger logger = LogManager.getLogger(FileUtil.class);
    private static final int availableProcessors = Runtime.getRuntime().availableProcessors();
    private static Thread.Builder vtBuilder = Thread.ofVirtual().name("vertical-partition",0);
    //private static Map<String, String> fileMappingsEEG;
    //public static Manager manager;

    public static boolean fileExists(File f){
        return f.exists();
    }
    
    private static long numLines(File f){
        //int numberOfLinesInOrigFile = -1;
        long numLinesOrigFile = -1;
        //Assumes File exists
        //logger.debug( "file path: " + f.getAbsolutePath());
        //logger.debug(" file name: "+ f.getName());
        
        try(Stream<String> fileStream = Files.lines(Paths.get(f.getAbsolutePath())) ) { 
            //second method of counting lines in orig file
            numLinesOrigFile = (int) fileStream.count();
            logger.debug("number of lines in file (numLinesOrigFile using Stream): "+ numLinesOrigFile);
        } catch (FileNotFoundException ex) {
            logger.error("Error FNFE:" + ex.getMessage());
        } catch (IOException ex) {
            logger.error("Error IOE: "+ex.getMessage());
        }    
        return numLinesOrigFile;
    }
    
    /*
    * if oldDelimiter is equal to de newDelimiter then only proceed to the splitting part,
    * if any of them is null or empty, throw exception
    * otherwise, both must be non-empty and different, continue with the replacement of columns up to the N first columns 
    * and split de file in two:
    * 1: A file with the 1,147 row split in first file with newDelimiter as separator
    * 2: A second file with the same 1,147 rows with the complimentary file contents as one line and the primary column duplicated per record
    */    
    public static Map<String, PersonalInfo> splitFileInColumns(File origFile, String oldDelimiter, 
            String newDelimiter, int firstNColumns, String regex) {
        logger.info("*************************************************************************");
        logger.info("* Entering splitFileInColumns(File origFile, String oldDelimiter, String newDelimiter, int firstNColumns, String regex)");
        logger.debug("*    firstNColumns: " + firstNColumns);
        logger.debug("*oldDelimiter: " + oldDelimiter);
        logger.debug("*orig file Name: " + origFile.getAbsolutePath());
        logger.debug("*       regex: " + regex);
        logger.info("*************************************************************************");
        
        //=======================================
        String destFileName = "src/files/output/"+ origFile.getName().split("\\.")[0];
        String destFileNameEEG = destFileName+ "-EEG";
        String destFileNamePersonalInfo = destFileName + "-PersonalInfo";
        int linesInFile = (int)numLines(origFile);
        
        int numPartitions = 100; //for developing and testing. Set back to 8
        long parFileSizeInLines = -1;
        //Instant start_replace = Instant.now();
        //Manager manager = new Manager(availableProcessors * 1000 ); //fine tune this parameter
        int partitionSizeInLines = (int)linesInFile/numPartitions;
        logger.debug("number of lines per partition: " + partitionSizeInLines);
        Manager manager = new Manager( partitionSizeInLines);
        int partStart = 0;
        
        List<Partition> partitionsList = new ArrayList<>(numPartitions);
        
        //Define the partition plan
        logger.debug("Creating partition plan...");
        //for(int p=0; p < numPartitions ; p++){
        for(int p=0; p < 2 ; p++){
            
            
            String partName = "partition-"+p;
            // logger.debug("partition name: "+partName);
            Partition partition = new Partition(partName, partStart, (partStart + partitionSizeInLines ), manager, origFile, (destFileName+"-vpart-"+p+".csv"),regex);
            partStart += (partitionSizeInLines + 1);//so next iteration won't overlap with previous partition
            partitionsList.add(partition);      
            //break;
        }
        logger.debug("partition list(plan) ["+partitionsList.size()+" count] : ");
        partitionsList.forEach(p-> logger.debug(p));
        Instant begin = Instant.now();
        
        //In parallel execute the partitions work
        partitionsList.parallelStream()
                .forEach(p-> {logger.debug("start work: "+p.getPartName());p.startWork();});
        
        //manager.shutdown();
    
        Instant end = Instant.now();
        Duration duration = Duration.between(begin, end);
        logger.info("Duration of completing all partitions: " + duration.toMillis() +" [ms];" + duration.toSeconds()+"[s]; "+ duration.toMinutes()+" [mins].");                
        //fileMappingsEEG = manager.getFileMappingsEEG();
        return manager.getPersonalInfoMap();
    }
    
    
    public static void replaceDelimiterInCSVFile(File origFile, String oldDelimiter, 
            String newDelimiter, boolean skipDelimiterInsideDoubleQuotes, 
            int numPartitions){
        
        logger.info("*************************************************************************");
        logger.info("* Entering replaceDelimiterInCSVFile(File, String, String, boolean, int) ");
        logger.debug("* skip commas in DoubleQuotes: " + skipDelimiterInsideDoubleQuotes);
        logger.debug("*               numPartitions: " + numPartitions);
        logger.debug("* Orig File Name: " + origFile.getAbsolutePath());
        logger.info("*************************************************************************");

        
        long parFileSizeInLines = -1;//numLines(origFile);
        Instant start_replace = Instant.now();
        //Manager manager = new Manager(availableProcessors * 1000 ); //fine tune this parameter
        int partitionSizeInLines = (int)parFileSizeInLines/numPartitions;
        Manager manager = new Manager( partitionSizeInLines);
        int partStart = 0;
        
        List<Partition> partitionsList = new ArrayList<>(numPartitions);
        
        //Define the partition plan
        logger.debug("Creating partition plan...");
        for(int p=0; p < 1 ; p++){
            
            String partName = "partition-"+p;
            // logger.debug("partition name: "+partName);
            //            public Partition(String partName, int begin, int end, Manager m, File origFile, String destFileName, String regex) {
            //Partition partition = new Partition(partName, partStart, (partStart + ), manager, origFile, (destFileName+"-vpart-"+p+".csv"),"");
            partStart += (partitionSizeInLines + 1);//so next iteration won't overlap with previous partition
            //logger.debug("adding partition: "+partition+" to List of partitions...");
            //partition.startWork();
            //partitionsList.add(partition);
            
        }
        logger.debug("partition list(plan) ["+partitionsList.size()+" count] : ");
        partitionsList.forEach(p-> logger.debug(p));
        //In parallel execute the partitions work
        partitionsList.parallelStream()
                .forEach(p-> {logger.debug("start work: "+p.getPartName());p.startWork();});
        
        manager.shutdown();
        
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

    public static Map<String, String> getFileMappingsEEG() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}