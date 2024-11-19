package proyectoProgAvanzada;

import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.System.exit;
import java.util.List;
import model.file.type.CSVFile;
import model.statistics.Statistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.file.FileUtil;
import util.validation.Validation;

public class ProyectoFinalProgAvanzada {
    private File originalFile = null;
    private List<File> archivos = null;
    private Statistics stats;
    private int availableProcessors;
    
    private static Logger logger = LogManager.getLogger(ProyectoFinalProgAvanzada.class);

    public ProyectoFinalProgAvanzada() {
        availableProcessors = java.lang.Runtime.getRuntime().availableProcessors();
    }
    
    
    
    public static void main(String[] args) {
        logger.info("Entering main...");
        ProyectoFinalProgAvanzada proyecto = new ProyectoFinalProgAvanzada();
        logger.info("Available processors in this machine: " + proyecto.availableProcessors);
        if (Validation.validateArgs(args)){
            int numPartitions = Integer.parseInt(args[1]);
            String originalFileName = args[0];
            
            //transform file i.e change comma to pipe and keep commas inside double quotes
            //FileUtil.replaceDelimiterInCSVFile(new File(originalFileName), ",", "|", true);
            //FileUtil.replaceDelimiterInCSVFile(new File(originalFileName), ",", "|", true, proyecto.availableProcessors );
            FileUtil.replaceDelimiterInCSVFile(new File(originalFileName), ",", "|", true, numPartitions );
            
            exit(1); //stop here to test divide file and replacement of delimiter
            
            proyecto.originalFile = new CSVFile(args[0]);
            if(!CSVFile.isCSVFile(proyecto.originalFile)){
                logger.warn("File " +proyecto.originalFile.getName() + " is not a CSV file");
                exit(1);                
            }
                
            //ProyectoFinalProgAvanzada project = new ProyectoFinalProgAvanzada();
            logger.info("Passed file validations.Proceed! ...");
            
            try {
                proyecto.archivos = FileUtil.divideArchivo(proyecto.originalFile , numPartitions);
            } catch (FileNotFoundException ex) {
                logger.error(ex.getMessage());
            }
            logger.debug("Partition files: ");
            for(File f: proyecto.archivos){
                logger.debug("File: "+ f.getName() );
            }
            
        }else{
            logger.info("invalid user input...");
            Validation.showUsage();
            logger.info("Exiting main...");
            exit(1);
        }
        
        //proyecto.archivos = archivos;
        
        
        logger.info("Exiting main...");
        
    }

}
