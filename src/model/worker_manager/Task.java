package model.worker_manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Task {
    private String name;
    private static Logger logger = LogManager.getLogger(Task.class);

    public Task(String name) {
        this.name = name;
    }
    
    public void execute() {
        logger.info("Entering execute...");
    }
}
