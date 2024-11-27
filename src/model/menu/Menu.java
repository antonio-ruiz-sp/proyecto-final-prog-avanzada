package model.menu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import model.file_object.EEG;
import model.file_object.PersonalInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.file.FileUtil;

public class Menu {
    private Map<String, PersonalInfo>  piMap;
    private Map<String, EEG> eegMap;
    private Map<String, String> fileMappingsEEG;
    private File origFile;
    private String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private Thread.Builder menuVTBuilder = Thread.ofVirtual().name("menu-worker",0);

    private static final Logger logger = LogManager.getLogger(Menu.class);

    public Menu(File origFile) {
        this.origFile = origFile;
    }
    
    public void showMenu(/*File origFile, String regex, Map<String, PersonalInfo> piMap*/) {
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Menu:");
            System.out.println("1. Process File");
            System.out.println("2. Show stats");
            System.out.println("3. View EEG");
            System.out.println("4. Exit");
            System.out.println("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    piMap = FileUtil.splitFileInColumns( origFile, ",", "|", 8, regex);
                    System.out.println("Process file...");
                    break;
                case 2:
                    //piMap = FileUtil.manager.getPersonalInfoMap().get("2");//.getFirstEEG()
                    //int registro = showSubMenu()
                    
                    break;
                case 3:
                    
                    System.out.println("Enter Record[1-10]: ");
                    Scanner recordScanner = new Scanner(System.in);
                    String record = recordScanner.nextLine();
                    PersonalInfo currentPi = piMap.get(record);
                    System.out.println("chosen record: " + record);
                    //piMap.get(record).getFirstEEG().displayGraphics();
                    //System.out.println("view EEG_Elektrot_1 of record " + record);
                    System.out.println("Choose EEG values i.e 3, 4-7, 8, 500: ");
                    Scanner elektrotScanner = new Scanner(System.in);
                    String elektrots = elektrotScanner.nextLine();
                    System.out.println("elektrots: "+ elektrots );
                    /*
                    String input = "12, 34, 56-78, 90";
                    */
                    //Pattern pattern = Pattern.compile("\\d+(,\\s*\\d+)*|\\d+-\\d+");
                    //String regexNumbers ="\\d+(,\\s*\\d+)*|\\d+-\\d+";
                    //String regexNumbers = "(\\d+|\\d+(?:,\\s*\\d+)*|\\d+-\\d+)";
                    //String regexNumbers ="^((\\d+-(\\*|\\d+))|((\\*|\\d+)-\\d+)|((\\d)(,\\d)+))$";

                    /*String regexNumbers = "\\d+(,\\s*\\d+)*|\\d+-\\d+";
                    Pattern pattern = Pattern.compile(regexNumbers);
                    Matcher matcher = pattern.matcher(elektrots);
                    System.out.println("Obtaining following EEG(s) for record[ " + record+" ]");
                    if(matcher.matches()){
                        System.out.println("...complies");
                    }else{
                        System.out.println("...does not comply");
                    }
                    while (matcher.find()) {
                        String group = matcher.group();
                        System.out.println("group:"+ group );
                        
                    }
                    */
                    //Map<String, String> fileMappingsEEG = getEEGMappings();
                    StringTokenizer st = new StringTokenizer(elektrots,",");
                    List<String> elektrotsList = new ArrayList<>();
                    while(st.hasMoreTokens()){
                        String token =st.nextToken();
                        logger.debug("token: {} ",token);
                        if(token.equals("1")){
                            currentPi.getFirstEEG().displayGraphics();
                            continue;
                        }
                        elektrotsList.add(token);
                    }
                    //TO_DO: find raw String common to all  EEG's for this record
                    
                    //Now with common String split work to display EEG
                    elektrotsList.parallelStream().forEach(eegItem->{
                        //try(BufferedWriter EEGLines = new BufferedWriter(new FileWriter("src/files/output/eeglines.csv",true))) {
                        try {
                            String eegFileName = piMap.get(record).getFileMappingEEG().get(record);
                            logger.debug("Obtaining EEG: EEG_Elektrot_"+ eegItem + " of record "+ record +" which is found in: " + eegFileName);
                            Instant begin = Instant.now();

                            Thread vt = menuVTBuilder.start(()->{
                                try(Stream<String> fileStream = Files.lines( Paths.get(eegFileName))) {

                                    logger.debug("Thread ID: " + Thread.currentThread().threadId());
                                    String eegString =fileStream.filter(l-> l.startsWith(record)).findFirst().get();
                                    logger.debug("eegString found!");

                                    logger.debug("raw string: {} ", eegString);
                                    //EEGLines.write(eegString+"\n");

                                    //String eegStringTmp = eegString.substring(eegString.indexOf("]")+1);
                                    eegString = eegString.substring(eegString.indexOf("|")+1);
                                    
                                    logger.debug("string after pipe: {}", eegString);
                                    //EEGLines.write(eegString+"\n");
                                    //change outside commas por "pipe"
                                    eegString = eegString.replaceAll(regex, "|");
                                    //EEGLines.write(eegString+"\n");
                                    StringTokenizer eegST = new StringTokenizer(eegString, "|");
                                    int countTokens = 0;
                                    String eegReading = null;
                                    logger.debug("Number being looked for: {}",eegItem);
                                    while (eegST.hasMoreTokens()){
                                        countTokens++;
                                        eegReading = eegST.nextToken();

                                        if(countTokens == Integer.parseInt(eegItem)){
                                            logger.debug("elektrot_{} found: {}",eegItem, eegReading);
                                            break;
                                        }                                            
                                    }
                                    logger.debug("iterated over {}  eegReadings!", countTokens);
                                    //logger.debug("string after commas outside double quotes got replaced by pipe : {}", cleanEEGString);
                                    //EEGLines.write(cleanEEGString+"\n");
                                    String[] eegMeasures = eegString.split(",");
                                    
                                    EEG eeg = new EEG(record, EEG.parseEEG(eegReading), currentPi.getMainDisorder(), currentPi.getSpecificDisorder(),eegItem);
                                    eeg.displayGraphics();
                                } catch (IOException ex) {
                                    logger.debug(ex.getMessage());
                                }
                            });
                            vt.join();
                            Instant end = Instant.now();
                            //Duration d = Duration.between(begin - end);
                            Duration duration = Duration.between(begin, end);
                            logger.info("Duration to complete search, load and graphing: " + duration.toMillis() +" [ms];" + duration.toSeconds()+"[s]; "+ duration.toMinutes()+" [mins].");
                
                        } catch (InterruptedException ex) {
                            logger.error( ex.getMessage());
                        }
                    });
                    break;
                case 4:
                    System.out.println("Exit...");
                    
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        
    }

}
