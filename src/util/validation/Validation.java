package util.validation;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public final class Validation {
    public static Logger logger = LogManager.getLogger(Validation.class);
    
    public static boolean validateArgs(String[] args) {
        boolean result = false;
        logger.debug("args: "+ Arrays.toString(args));
        logger.debug("num args: "+ args.length);
        if(args.length <= 2 || (args[0]==null && args[1]==null)){
            result = true;
        }
        //Check for first arg (arg[0]) to be String (file name + path) ending in .csv using regex
        //also check for second arg to be an integer > 1 (number of partitions)
        
        return result;
    }

    public static void showUsage() {
        System.out.println("Show usage:");
    }
    
}
