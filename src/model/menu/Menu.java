package model.menu;

import java.io.File;
import java.util.Map;
import java.util.Scanner;
import model.file_object.PersonalInfo;
import util.file.FileUtil;

public class Menu {
    public static void showMenu(File origFile, String regex, Map<String, PersonalInfo> piMap) {
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
                    System.out.println("chosen record: " + record);
                    piMap.get(record).getFirstEEG().displayGraphics();
                    //System.out.println("vies stats");
                    System.out.println("view EEG_Elektrot_1 of record " + record);
                    break;
                case 4:
                    System.out.println("Exit...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        
    }

    /*
    public Menu(File origFile, String regex) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }*/
}
