package model.worker_manager;

import java.lang.Thread.Builder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.file.Partition;

public class Manager {
    Logger logger = LogManager.getLogger(Manager.class);
    
    int numWorkers;
    Map<String, PersonalInfo> personalInfoMap;
    Map<String, EEG> EEGMap;

    public int getNumWorkers() {
        return numWorkers;
    }
    
    //private ExecutorService executor;  
    private Builder vtBuilder;

    public Manager(int numberOfWorkers) {  
        this.numWorkers = numberOfWorkers;
        vtBuilder = Thread.ofVirtual().name("worker-", 0);
        personalInfoMap = new ConcurrentHashMap<>();
        EEGMap = new ConcurrentHashMap<>();
        /*
        //executor = Executors.newFixedThreadPool(numberOfWorkers);  
        //executor = Executors.newVirtualThreadPerTaskExecutor();
        
        try (ExecutorService myExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
        Future<?> future = myExecutor.submit(() -> System.out.println("Running thread"));
        future.get();
        System.out.println("Task completed");

        }   catch (InterruptedException ex) {  
            java.util.logging.Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            java.util.logging.Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
    public void submitTask(Partition p) {  
        //logger.info("Entering submitTask(worker)..." + p.getPartName());
        /*Worker worker = new Worker(p.getPartName(), p.getOrigFile(), new File(p.getDestfileName()), 
                p.getBegin(), p.getEnd(), p.getRegex() );
        */
        Worker worker = new Worker(p, personalInfoMap, EEGMap);
        //logger.debug(p.getPartName()+ " worker created...and before start()");
        Thread vt = vtBuilder.start(worker);
        try {
            vt.join();
        } catch (InterruptedException ex) {
            logger.error("Exception in submitTask(Partition): " + ex.getMessage());
        }
        //logger.debug("task "+ vt.getName()+" completed!");
        
        /*
        //Future<?> f = executor.submit(worker);
        
        try {
            Future<?> f = executor.submit(worker);
            f.get(); //this is like executing join().
        } catch (InterruptedException | ExecutionException ex) {
            logger.error(ex.getMessage());
        }
        */
        //logger.info("Exiting submitTask");
    }  
    
    
    public void shutdown() {
        //executor.shutdown();
        vtBuilder = null;
    }
    
}
