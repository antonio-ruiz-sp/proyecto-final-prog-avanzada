package proyectoProgAvanzada;
import gui.MainMenuJFrame;
import java.io.File;
import static java.lang.System.exit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import model.menu.Menu;
import model.statistics.Statistics;
import model.file_object.PersonalInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.file.FileUtil;
import util.validation.Validation;

public class ProyectoFinalProgAvanzada{

    //private Map<String, PersonalInfo> personalInfo;
    private File originalFile = null;
    private List<File> archivos = null;
    private Statistics stats;
    private int availableProcessors;
    
    private static Logger logger = LogManager.getLogger(ProyectoFinalProgAvanzada.class);

    public ProyectoFinalProgAvanzada() {
        this.availableProcessors = java.lang.Runtime.getRuntime().availableProcessors();
        
    }
     
    
    public static void main(String[] args) {
        logger.info("Entering main...");
        
        ProyectoFinalProgAvanzada proyecto = new ProyectoFinalProgAvanzada();
        logger.info("Available processors in this machine: " + proyecto.availableProcessors);
        if (Validation.validateArgs(args)){
            int numPartitions = Integer.parseInt(args[1]);
            String originalFileName = args[0];
            
            
            //showvalidation(originalFileName, proyecto);
            
            //logger.debug("menu: "+menu);
            //menu.getcom
            //((JTextField)proyecto.getComponentByName(menu.getComponents(), "partitionsTxt")).setText(String.valueOf(proyecto.availableProcessors));
            //((JTextField)proyecto.getComponentByName(menu.getComponents(), "fileNameTxt")).setText(String.valueOf(proyecto.originalFile.getName()));
            
            //menu.getComponents()
            //componentsMenu.toList()
            //Streammenu.getContentPane().getComponents
            
            //exit(1);
            //transform file i.e change comma to pipe and keep commas inside double quotes
            File origFile = new File(originalFileName);
            //find commas outside double quotes only
            //String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
            //Map<String, PersonalInfo> personalInfoMap = new HashMap<>();
            //Map<String, String> fileMappingsEEG = FileUtil.getFileMappingsEEG();
            Menu menu = new Menu(origFile);
            menu.showMenu();
            //FileUtil.splitFileInColumns( origFile, ",", "|", 8,regex); now this is being called from Menu 
           
        }else{
            logger.info("invalid user input...");
            Validation.showUsage();
            logger.info("Exiting main...");
            exit(1);
        }
        
        logger.info("Exiting main...");   
    }

    private static void showvalidation(String originalFileName, ProyectoFinalProgAvanzada proyecto) {
       
        MainMenuJFrame menu = new MainMenuJFrame();//.setVisible(true);
        
        JLabel fileNameLbl = new JLabel("File:");
        fileNameLbl.setName("fileNameLbl");
        //JTextField fileNameTxt = new JTextField( originalFileName );
        JTextField fileNameTxt = new JTextField(originalFileName);
        fileNameTxt.setName("fileNameTxt");
        fileNameTxt.setText(originalFileName);
        JLabel partitionsLbl = new JLabel("partitions (recommended)");
        partitionsLbl.setName("partitionsLbl");
        JTextField partitionsTxt = new JTextField(String.valueOf(proyecto.availableProcessors));
        partitionsTxt.setName("partitionsTxt");
        JButton continueProcessFileBtn = new JButton("Continue");
        continueProcessFileBtn.setName("continueProcessFileBtn");
        //continueProcessFileBtn.setA
        
        menu.add(fileNameLbl);
        menu.add(fileNameTxt);
        menu.add(partitionsLbl);
        menu.add(partitionsTxt);
        menu.add(continueProcessFileBtn);
        
        //menu.setLayout(new javax.swing.GroupLayout(menu));
        menu.setSize(300,200);
        menu.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        menu.setVisible(true);
        
        logger.debug("now what?");
        
        
        
        menu.setVisible(true);
        //menu.setAlwaysOnTop(true);
    }
}
