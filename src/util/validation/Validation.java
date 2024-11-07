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
        return true;
    }

    public static void showUsage() {
        System.out.println("Show usage:");
    }
    
}
