package model.worker_manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import me.tongfei.progressbar.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.file.Partition;

public class Worker implements Runnable {
    private static Logger logger = LogManager.getLogger(Worker.class);
    //private ProgressBar pb; // = new ProgressBar();
    private String taskName;
    int start;
    int stop;
    File origFile;
    File destFile;
    String regex;
    Map<String, EEG> EEGMap;
    Map<String, PersonalInfo> personalInfoMap;
    
    //for nested virtual threading
    private Thread.Builder workerVTBuilder;
    public Worker(int line){
        int start = line;
    }


    public Worker(String task, File origFile, File destFile, int start, int stop, String regex) {
        this.taskName = task;
        this.start = start;
        this.stop = stop;
        this.destFile = destFile;
        this.origFile = origFile;
        this.regex = regex;
        //pb = new ProgressBar(taskName, (stop - start));
        workerVTBuilder = Thread.ofVirtual().name("sub-worker"+0);
    }

    Worker(Partition p, Map<String, PersonalInfo> personalInfoMap, Map<String, EEG> EEGMap) {
        this.taskName = p.getPartName();
        this.start = p.getBegin();
        this.stop = p.getEnd();
        this.destFile = new File(p.getDestfileName());
        this.origFile = p.getOrigFile();
        this.regex = p.getRegex();
        this.EEGMap = EEGMap;
        this.personalInfoMap = personalInfoMap;
        workerVTBuilder = Thread.ofVirtual().name("sub-worker"+0);

    }
    
    @Override  
    public void run() {
        logger.info("*************************************");
        logger.info("* Executing run() of : " + taskName);  
        logger.debug("*     Thread ID: " + Thread.currentThread().threadId());
        logger.debug("*   worker name: " + taskName);
        logger.debug("*Value of start: "+ start);
        logger.debug("*  Value of end: "+ stop);
        logger.debug("*Destination file: "+ destFile.getName());
        logger.info("*************************************");
        
        String destFileName = "src/files/output/"+ destFile.getName().split("\\.")[0];
        String destFileNameEEG = destFileName+ "-EEG.csv";
        String destFileNamePersonalInfo = destFileName + "-PersonalInfo.csv";
 
        //logger.debug("Start measuring time on: "+taskName);
        Instant begin = Instant.now();
        
        
        try (
            BufferedWriter archivoDestinoPersonal = new BufferedWriter(new FileWriter(destFileNamePersonalInfo));
            BufferedWriter archivoDestinoEEG = new BufferedWriter(new FileWriter(destFileNameEEG));
            Stream<String> fileStream = Files.lines( Paths.get(origFile.getAbsolutePath()));
            ProgressBar pb = new ProgressBar(taskName, stop - start) ) {

            //fileStream.skip(start).limit(stop-start).parallel().forEach(line-> {
            fileStream.skip(start).limit(stop - start).forEach(line-> {
                try {
                    /*
                    //one worker per partition
                    String newLine = line.replaceAll(regex, "|");
                    //logger.debug("new line:" +newLine);
                    archivoDestinoPersonal.write(newLine+"\n");
                    pb.step();
                    */
                    //one vt per line
                    Thread vt = workerVTBuilder.start(()->{
                        try {
                            //int counter = 0;
                            
                            /*logger.debug("Thread ID: " + Thread.currentThread().threadId()+
                            " task name: " + taskName);*/
                            //===============================================
                            String newLine = line.
                                    replaceFirst(",","|"). //no.
                                    replaceFirst(",","|"). //sex
                                    replaceFirst(",","|"). //age
                                    replaceFirst(",","|"). //eeg.date
                                    replaceFirst(",","|"). //education
                                    replaceFirst(",","|"). //IQ
                                    replaceFirst(",","|"). //main.disorder
                                    replaceFirst(",","|"). //specific.disorder
                                    replaceFirst(regex,"|"); //EEG_Elektrot_1
                            //===============================================
                            //logger.debug("newLine:" + newLine);
                            //newLine.split(regex, 9);
                            int lastPipe = newLine.lastIndexOf("|"); //persona info + first EEG_Elektrot_1
                            int firstPipe = newLine.indexOf("|");
                            String personalInfoStr = newLine.substring(0, lastPipe);
                            //logger.debug("personal Info: " +personalInfoStr);
                            String recordNumber = newLine.substring(0, firstPipe);
                            String EEGLine = recordNumber + newLine.substring(lastPipe); //start with record number to keep the reference to personal info
                            
                            //write to two logs, while saving info into Java objects i.e ConcurrentHashMap
                            archivoDestinoPersonal.write(personalInfoStr+"\n");
                            archivoDestinoEEG.write(EEGLine+"\n");
                            
                            //0-no, 1-sex, 2-age, 3-eeg.date, 4-education, 5-IQ, 6-main.disorder, 7-specific.disorder, 8- EEG_Elektrot_1
                            personalInfoMap.computeIfAbsent(recordNumber, pInfo->{
                                //PersonalInfo(String no, String sex, int age, String eegDate, String education, int IQ, String mainDisorder, String specificDisorder, String EEGString) {
                                
                                //logger.debug("first token: "+ personalInfoStr.substring(lastPipe) +" fifth token: "+piArray[5]);
                                if(recordNumber.contains("no.") ){
                                    return null; //this means it's the header
                                }
                                StringTokenizer piTokenizer = new StringTokenizer(personalInfoStr,"|");
                                
                                //TO-DO make this more robuts, as it can easily throw NPE if one of the tokens is null or empty
                                PersonalInfo pi = new PersonalInfo(
                                        piTokenizer.nextToken(), //0-no
                                        piTokenizer.nextToken(), //1-sex
                                        Integer.valueOf(piTokenizer.nextToken()).intValue(),//2-age <== this does not work if it's the header
                                        piTokenizer.nextToken(), //3-eeg.date
                                        piTokenizer.nextToken(), //4-education
                                        Integer.valueOf(piTokenizer.nextToken()).intValue(), //5-IQ <== this does not work if it's the header
                                        piTokenizer.nextToken(), //6-main.disorder
                                        piTokenizer.nextToken(), //7-specific.disorder
                                        piTokenizer.nextToken() //8-EEG_Elektrot_1
                                );
                                return pi;
                            });
                            newLine = null; //for GC
                            pb.step();
                        } catch (IOException ex) {
                            logger.debug(ex.getMessage());
                        }
                    });
                    vt.join();
                    
                    
                } catch (InterruptedException ex) {
                    logger.error("Error: "+ex.getMessage());                
                }
                //pb.step();
            });
            
        }catch (IOException ioex) {
            logger.error("Error: " + ioex.getMessage());
        }
        logger.debug("Now partition "+taskName +" has extracted file records into Concurrent Hash map. " 
                + " containing: " +personalInfoMap.size() +" elements.");
        //personalInfoMap.forEach((k,v)->{ logger.debug("Key: {}, Value: {}", k, v);});
        Instant end = Instant.now();
        Duration duration = Duration.between(begin, end);
        logger.info("Duration: " + duration.toMillis() +" [ms];" + duration.toSeconds()+"[s]; "+ duration.toMinutes()+" [mins].");
                 
        logger.info("run() completed: " + taskName);  
    } 
}