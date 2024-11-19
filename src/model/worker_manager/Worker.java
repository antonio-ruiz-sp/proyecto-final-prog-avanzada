package model.worker_manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Worker implements Runnable {
    private static Logger logger = LogManager.getLogger(Worker.class);
    private final String task;
    int start;
    int stop;
    File origFile;
    File destFile;
    String regex;

    public Worker(String task, File origFile, File destFile, int start, int stop, String regex) {
        this.task = task;
        this.start = start;
        this.stop = stop;
        this.destFile = destFile;
        this.origFile = origFile;
        this.regex = regex;
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
            Stream<String> fileStream = Files.lines(Paths.get(origFile.getAbsolutePath())) ;
            /*LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(origFile))*/ ) {
            //String newLine;
            fileStream.skip(start).limit(stop-start).forEach(line-> {
                try {
                    String newLine = line.replaceAll(regex, "|");
                    //logger.debug("new line:" +newLine);
                    archivoDestino.write(newLine+"\n");
                } catch (IOException ex) {
                    logger.error( ex.getMessage());
                }
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
