package proyectoProgAvanzada;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.validation.Validation;

public class ProyectoFinalProgAvanzada {

    private static Logger logger = LogManager.getLogger(ProyectoFinalProgAvanzada.class);

    public static void main(String[] args) {
        logger.info("Entering main...");
        
        if (Validation.validateArgs(args)) {
            new GUIProyectoFinal(); // Solo instancia la GUI
        } else {
            logger.info("Entrada inválida...");
            Validation.showUsage();
            logger.info("Exiting main...");
            System.exit(1);
        }

        logger.info("Exiting main...");
    }
}
