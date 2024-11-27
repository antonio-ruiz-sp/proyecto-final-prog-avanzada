package model.statistics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.file_object.PersonalInfo;

public class FileStatistics {

    private static final DecimalFormat df = new DecimalFormat("#.##");

    public static void generateSummary(File file, Map<String, PersonalInfo> dataMap) {
        
        long fileSizeKB = file.length() / 1024;
        int numRows = dataMap.size();
        int numCols = 8; 

       
        List<Integer> ages = dataMap.values().stream().map(PersonalInfo::getAge).collect(Collectors.toList());
        List<Integer> iqValues = dataMap.values().stream().map(PersonalInfo::getIQ).collect(Collectors.toList());

       
        double ageMin = ages.stream().min(Integer::compareTo).orElse(0);
        double ageMax = ages.stream().max(Integer::compareTo).orElse(0);
        double ageMean = ages.stream().mapToInt(Integer::intValue).average().orElse(0);
        double ageStdDev = calculateStdDev(ages, ageMean);

        double iqMin = iqValues.stream().min(Integer::compareTo).orElse(0);
        double iqMax = iqValues.stream().max(Integer::compareTo).orElse(0);
        double iqMean = iqValues.stream().mapToInt(Integer::intValue).average().orElse(0);
        double iqStdDev = calculateStdDev(iqValues, iqMean);

       
        Map<String, Long> sexDistribution = dataMap.values().stream()
                .collect(Collectors.groupingBy(PersonalInfo::getSex, Collectors.counting()));

        Map<String, Long> disorderDistribution = dataMap.values().stream()
                .collect(Collectors.groupingBy(PersonalInfo::getMainDisorder, Collectors.counting()));

       
        String[][] tableData = {
                {"File Size (KB)", String.valueOf(fileSizeKB), "", "", ""},
                {"Number of Rows", String.valueOf(numRows), "", "", ""},
                {"Number of Columns", String.valueOf(numCols), "", "", ""},
                {"", "Age", "IQ", "", ""},
                {"Min", df.format(ageMin), df.format(iqMin), "", ""},
                {"Max", df.format(ageMax), df.format(iqMax), "", ""},
                {"Mean", df.format(ageMean), df.format(iqMean), "", ""},
                {"Std Dev", df.format(ageStdDev), df.format(iqStdDev), "", ""},
                {"", "Sex Distribution", "", "", ""},
        };

        int rowIndex = 9; 
        for (Map.Entry<String, Long> entry : sexDistribution.entrySet()) {
            tableData = appendRow(tableData, new String[]{entry.getKey(), String.valueOf(entry.getValue()), "", "", ""});
        }

        tableData = appendRow(tableData, new String[]{"", "Disorder Distribution", "", "", ""});
        for (Map.Entry<String, Long> entry : disorderDistribution.entrySet()) {
            tableData = appendRow(tableData, new String[]{entry.getKey(), String.valueOf(entry.getValue()), "", "", ""});
        }

       
        displayTable(tableData);
    }

    private static void displayTable(String[][] data) {
        String[] columnNames = {"Attribute", "Value 1", "Value 2", "Value 3", "Value 4"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        JFrame frame = new JFrame("Dataset Summary");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(scrollPane);
        frame.setSize(800, 400);
        frame.setVisible(true);
    }

    private static double calculateStdDev(List<Integer> values, double mean) {
        return Math.sqrt(values.stream().mapToDouble(val -> Math.pow(val - mean, 2)).average().orElse(0));
    }

    private static String[][] appendRow(String[][] array, String[] newRow) {
        String[][] newArray = new String[array.length + 1][array[0].length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = newRow;
        return newArray;
    }
}