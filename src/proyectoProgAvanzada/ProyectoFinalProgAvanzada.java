package proyectoProgAvanzada;

import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.System.exit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.file.FileUtil;
import util.validation.Validation;

public class ProyectoFinalProgAvanzada {
    private static File originalFile = null;
    private static List<File> archivos = null;
    private Statistics stats;
    
    
    public static void main(String[] args) {
           
        if (Validation.validateArgs(args)){
            int numFiles = Integer.parseInt(args[1]);
            originalFile = new File(args[0]);
            //ProyectoFinalProgAvanzada project = new ProyectoFinalProgAvanzada();
            System.out.println("Proceed ...");
            try {
                archivos = FileUtil.divideArchivo( originalFile , numFiles);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ProyectoFinalProgAvanzada.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Partition files: ");
            for(File f: archivos){
                System.out.println("File: "+ f.getName() );
            }
        }else{
            Validation.showUsage();
            exit(1);
        }
        
        
    }

    private static class Statistics {

        public Statistics() {
        }
    }
}
