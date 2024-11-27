package proyectoProgAvanzada;
import proyectoProgAvanzada.GUIProyectoFinal;

import java.io.File;
import static java.lang.System.exit;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.validation.Validation;

public class ProyectoFinalProgAvanzada {

    private static Logger logger = LogManager.getLogger(ProyectoFinalProgAvanzada.class);

    public static void main(String[] args) {
        logger.info("Entering main...");

        ProyectoFinalProgAvanzada proyecto = new ProyectoFinalProgAvanzada();
        logger.info("Available processors in this machine: " + Runtime.getRuntime().availableProcessors());

        if (Validation.validateArgs(args)) {
            String originalFileName = args[0];
            int numPartitions = Integer.parseInt(args[1]);

            showGUI(originalFileName, numPartitions);
        } else {
            logger.info("Invalid user input...");
            Validation.showUsage();
            logger.info("Exiting main...");
            exit(1);
        }

        logger.info("Exiting main...");
    }

    private static void showGUI(String originalFileName, int numPartitions) {
        logger.info("Launching GUI...");
        GUIProyectoFinal gui = new GUIProyectoFinal();
        gui.setFilePath(originalFileName); 
        gui.setPartitions(numPartitions); 
    }
}
