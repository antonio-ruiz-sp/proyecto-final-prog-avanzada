package util.validation;

import java.util.Arrays;


public final class Validation {
    
    public static boolean validateArgs(String[] args) {
        boolean result = false;
        System.out.println("args: "+ Arrays.toString(args));
        System.out.println("num args: "+ args.length);
        if(args.length < 2 || (args[0]==null && args[1]==null)){
            result = true;
        }
        //Check for first arg (arg[0]) to be String (file name + path) ending in .csv using regex
        //also check for second arg to be an integer > 1 (number of partitions)
        
        return true;
    }

    public static void showUsage() {
        System.out.println("Show usage:");
    }
    
}
