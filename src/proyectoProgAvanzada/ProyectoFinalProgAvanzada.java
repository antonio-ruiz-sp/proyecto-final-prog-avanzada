package proyectoProgAvanzada;

import java.io.File;
import static java.lang.System.exit;
import java.util.List;
import util.file.FileUtil;
import util.validation.Validation;

public class ProyectoFinalProgAvanzada {
    private static File originalFile = null;
    private static List<File> archivos = null;
    
    
    public static void main(String[] args) {
        
        
        if (Validation.validateArgs(args)){
            int numFiles = Integer.parseInt(args[1]);
            originalFile = new File(args[0]);
            //ProyectoFinalProgAvanzada project = new ProyectoFinalProgAvanzada();
            System.out.println("Proceed ...");
            archivos = FileUtil.divideArchivo( originalFile , numFiles);
            for(File f: archivos){
                
                f.getName();
            }
        }else{
            Validation.showUsage();
            exit(1);
        }
        
        
    }

    
}
