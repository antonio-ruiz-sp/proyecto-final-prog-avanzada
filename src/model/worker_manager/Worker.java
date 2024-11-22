package model.worker_manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.stream.Stream;
import me.tongfei.progressbar.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Worker implements Runnable {
    private static Logger logger = LogManager.getLogger(Worker.class);
    //private ProgressBar pb; // = new ProgressBar();
    private final String task;
    int start;
    int stop;
    File origFile;
    File destFile;
    String regex;
    
    //for nested virtual threading
    private Thread.Builder workerVTBuilder;


    public Worker(String task, File origFile, File destFile, int start, int stop, String regex) {
        this.task = task;
        this.start = start;
        this.stop = stop;
        this.destFile = destFile;
        this.origFile = origFile;
        this.regex = regex;
        //pb = new ProgressBar(task, (stop - start));
        workerVTBuilder = Thread.ofVirtual().name("sub-worker"+0);
    }
    
    @Override  
    public void run() {
        logger.info("*************************************");
        logger.info("* Executing run() of : " + task);  
        logger.debug("*     Thread ID: " + Thread.currentThread().threadId());
        logger.debug("*   worker name: " + task);
        logger.debug("*Value of start: "+ start);
        logger.debug("*  Value of end: "+ stop);
        logger.debug("*Destination file: "+ destFile.getName());
        logger.info("*************************************");

        logger.debug("Start measuring time on: "+task);
        Instant begin = Instant.now();
        
        
        try (
            BufferedWriter archivoDestino = new BufferedWriter(new FileWriter(destFile));
            Stream<String> fileStream = Files.lines( Paths.get(origFile.getAbsolutePath()));
            ProgressBar pb = new ProgressBar(task, stop- start) ) {

            //fileStream.skip(start).limit(stop-start).parallel().forEach(line-> {
            fileStream.skip(start).limit(stop-start).forEach(line-> {
                try {
                    /*
                    //one worker per partition
                    String newLine = line.replaceAll(regex, "|");
                    //logger.debug("new line:" +newLine);
                    archivoDestino.write(newLine+"\n");
                    pb.step();
                    */
                    
                    //one vt per line
                    Thread vt = workerVTBuilder.start(()->{
                        try {
                            logger.debug("Thread ID: " + Thread.currentThread().threadId()+
                                    " task name: " + task);
                            String newLine = line.replaceAll(regex,"|");
                            //logger.debug("newLine" + newLine);
                            archivoDestino.write(newLine+"\n");
                            //archivoDestino.write(newLine);+"\n");
                            pb.step();
                        } catch (IOException ex) {
                            java.util.logging.Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    vt.join();
                    
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                }
                //pb.step();
            });
            
        }catch (IOException ioex) {
            logger.error("Error: " + ioex.getMessage());
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(begin, end);
        logger.info("Duration: " + duration.toMillis() +" [ms];" + duration.toSeconds()+"[s]; "+ duration.toMinutes()+" [mins].");
                 
        logger.info("run() completed: " + task);  
    } 
}
