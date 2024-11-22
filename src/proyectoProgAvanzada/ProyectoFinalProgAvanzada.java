package proyectoProgAvanzada;
import javax.swing.*;
import gui.MainMenuJFrame;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.System.exit;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import model.file.type.CSVFile;
import model.statistics.Statistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.file.FileUtil;
import util.validation.Validation;

public class ProyectoFinalProgAvanzada{
    private File originalFile = null;
    private List<File> archivos = null;
    private Statistics stats;
    private int availableProcessors;
    
    private static Logger logger = LogManager.getLogger(ProyectoFinalProgAvanzada.class);

    public ProyectoFinalProgAvanzada() {
        this.availableProcessors = java.lang.Runtime.getRuntime().availableProcessors();
        
    }
    
    
    private Component getComponentByName(Component[] container, String cName){
        Component c = null;
        Stream<Component> componentsMenu = Stream.of(container);
        logger.debug("components: "+container.length);
        for(Component com: container){
            logger.debug("Component:"+com.getName()==null?"Null":com.toString());
        }
        //c = componentsMenu.filter(obj -> obj.getName().equals(cName)).findFirst().get();  //findFirst().get();
        logger.debug("Found component: "+ c);
        return c;
    } 
    
    public static void main(String[] args) {
        logger.info("Entering main...");
        
        ProyectoFinalProgAvanzada proyecto = new ProyectoFinalProgAvanzada();
        logger.info("Available processors in this machine: " + proyecto.availableProcessors);
        if (Validation.validateArgs(args)){
            int numPartitions = Integer.parseInt(args[1]);
            String originalFileName = args[0];
            
            
            MainMenuJFrame menu = new MainMenuJFrame();//.setVisible(true);
            /*
            JLabel fileNameLbl = new JLabel("File:");
            fileNameLbl.setName("fileNameLbl");
            //JTextField fileNameTxt = new JTextField( originalFileName );
            JTextField fileNameTxt = new JTextField(originalFileName);
            fileNameTxt.setName("fileNameTxt");
            JLabel partitionsLbl = new JLabel("partitions (recommended)");
            partitionsLbl.setName("partitionsLbl");
            JTextField partitionsTxt = new JTextField(String.valueOf(proyecto.availableProcessors));
            partitionsTxt.setName("partitionsTxt");
            JButton continueProcessFileBtn = new JButton("Continue");
            continueProcessFileBtn.setName("continueProcessFileBtn");
            
            
            menu.add(fileNameLbl);
            menu.add(fileNameTxt);
            menu.add(partitionsLbl);
            menu.add(partitionsTxt);
            menu.add(continueProcessFileBtn);

            //menu.setLayout(new javax.swing.GroupLayout(menu));
            menu.setSize(300,200);
            menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            menu.setVisible(true);
            
            logger.debug("now what?");
            
            */
            
            menu.setVisible(true);
            menu.setAlwaysOnTop(true);
            
            //logger.debug("menu: "+menu);
            //menu.getcom
            //((JTextField)proyecto.getComponentByName(menu.getComponents(), "partitionsTxt")).setText(String.valueOf(proyecto.availableProcessors));
            //((JTextField)proyecto.getComponentByName(menu.getComponents(), "fileNameTxt")).setText(String.valueOf(proyecto.originalFile.getName()));
            
            //menu.getComponents()
            //componentsMenu.toList()
            //Streammenu.getContentPane().getComponents
            
            exit(1);
            //transform file i.e change comma to pipe and keep commas inside double quotes
            FileUtil.replaceDelimiterInCSVFile(new File(originalFileName), ",", "|", true, numPartitions );
            
            exit(0); //stop here to test divide file and replacement of delimiter
            
            proyecto.originalFile = new CSVFile(args[0]);
            if(!CSVFile.isCSVFile(proyecto.originalFile)){
                logger.warn("File " +proyecto.originalFile.getName() + " is not a CSV file");
                exit(0);                
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
