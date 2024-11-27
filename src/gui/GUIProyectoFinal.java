package proyectoProgAvanzada;
import model.statistics.FileStatistics;

import model.file_object.EEG;
import model.file_object.PersonalInfo;
import util.file.FileUtil;
import java.time.Instant;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class GUIProyectoFinal extends JFrame {

    private static final Logger logger = LogManager.getLogger(GUIProyectoFinal.class);
    private JTextField filePathField;
    private JTextField partitionsField;
    private Map<String, PersonalInfo> personalInfoMap;

    public GUIProyectoFinal() {
        setTitle("Proyecto Final - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800); 
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Proyecto Final - Menú Principal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton processFileButton = new JButton("1. Procesar Archivo");
        JButton showStatsButton = new JButton("2. Mostrar Estadísticas");
        JButton viewEEGButton = new JButton("3. Ver Datos EEG");
        JButton exitButton = new JButton("4. Salir");

        buttonPanel.add(processFileButton);
        buttonPanel.add(showStatsButton);
        buttonPanel.add(viewEEGButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel filePathLabel = new JLabel("Ruta del Archivo:");
        filePathField = new JTextField("src/files/input/synthetic_eeg_data_testv_original.csv", 20);

        JLabel partitionsLabel = new JLabel("Número de Particiones:");
        partitionsField = new JTextField("10", 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(filePathLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(filePathField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(partitionsLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(partitionsField, gbc);

        add(inputPanel, BorderLayout.SOUTH);

        personalInfoMap = new HashMap<>();

        processFileButton.addActionListener(e -> processFile());
        showStatsButton.addActionListener(e -> showStats());
        viewEEGButton.addActionListener(e -> viewEEG());
        exitButton.addActionListener(e -> exitProgram());

        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void setFilePath(String filePath) {
        filePathField.setText(filePath);
    }

    public void setPartitions(int partitions) {
        partitionsField.setText(String.valueOf(partitions));
    }
    private void processFile() {
        String filePath = filePathField.getText().trim();
        String partitionsStr = partitionsField.getText().trim();

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "El archivo especificado no existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int partitions = Integer.parseInt(partitionsStr);
            if (partitions <= 0) {
                JOptionPane.showMessageDialog(this, "El número de particiones debe ser un entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
            logger.info("Procesando archivo...");

            personalInfoMap.clear();
            personalInfoMap.putAll(FileUtil.splitFileInColumns(file, ",", "|", partitions, regex));
            JOptionPane.showMessageDialog(this, "Archivo procesado exitosamente. Datos cargados.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido para las particiones.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            logger.severe("Error al procesar el archivo: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Ocurrió un error al procesar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStats() {
        if (personalInfoMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero procesa el archivo para generar estadísticas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = new File(filePathField.getText().trim());
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "El archivo especificado no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            FileStatistics.generateSummary(file, personalInfoMap);
        } catch (Exception e) {
            logger.error("Error al generar estadísticas: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Ocurrió un error al generar las estadísticas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calculateMedian(List<Integer> iqList) {
        Collections.sort(iqList);
        int size = iqList.size();
        if (size % 2 == 0) {
            return (iqList.get(size / 2 - 1) + iqList.get(size / 2)) / 2;
        } else {
            return iqList.get(size / 2);
        }
    }

    private int calculateMode(List<Integer> iqList) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int iq : iqList) {
            frequencyMap.put(iq, frequencyMap.getOrDefault(iq, 0) + 1);
        }
        return frequencyMap.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getKey();
    }

    private void viewEEG() {
        if (personalInfoMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero procesa el archivo para cargar los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String record = JOptionPane.showInputDialog(this, "Ingrese el ID del registro (1-10):");
        if (record == null || record.trim().isEmpty()) return;

        record = record.trim();
        if (!personalInfoMap.containsKey(record)) {
            JOptionPane.showMessageDialog(this, "Registro no encontrado. Verifique el ID e intente nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            PersonalInfo currentPi = personalInfoMap.get(record);

            final String finalRecord = record;
            final PersonalInfo finalPi = currentPi;
            final String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

            String elektrotsInput = JOptionPane.showInputDialog(this, "Ingrese los elektrots (ej: 1,3-5):");
            if (elektrotsInput == null || elektrotsInput.trim().isEmpty()) return;

            List<String> elektrotsList = parseElektrotsAsString(elektrotsInput);

            elektrotsList.parallelStream().forEach(eegItem -> {
                try {
                    String eegFileName = finalPi.getFileMappingEEG().get(finalRecord);
                    logger.debug("Obteniendo EEG: EEG_Elektrot_" + eegItem + " del registro " + finalRecord + " en archivo: " + eegFileName);

                    Instant begin = Instant.now();

                    try (Stream<String> fileStream = Files.lines(Paths.get(eegFileName))) {
                        String eegString = fileStream.filter(l -> l.startsWith(finalRecord)).findFirst().orElse(null);
                        if (eegString == null) {
                            logger.warn("No se encontró registro EEG para: " + finalRecord);
                            return;
                        }

                        logger.debug("Cadena EEG encontrada: " + eegString);
                        eegString = eegString.substring(eegString.indexOf("|") + 1).replaceAll(regex, "|");

                        StringTokenizer eegST = new StringTokenizer(eegString, "|");
                        int countTokens = 0;
                        String eegReading = null;
                        while (eegST.hasMoreTokens()) {
                            countTokens++;
                            eegReading = eegST.nextToken();
                            if (countTokens == Integer.parseInt(eegItem)) {
                                logger.debug("Elektrot_" + eegItem + " encontrado: " + eegReading);
                                break;
                            }
                        }

                        EEG eeg = new EEG(finalRecord, EEG.parseEEG(eegReading), finalPi.getMainDisorder(), finalPi.getSpecificDisorder(), eegItem);
                        eeg.displayGraphics();

                        Instant end = Instant.now();
                        Duration duration = Duration.between(begin, end);
                        logger.info("Duración para búsqueda, carga y graficación: " + duration.toMillis() + " ms");
                    }

                } catch (Exception ex) {
                    logger.error("Error al procesar EEG: " + ex.getMessage());
                    JOptionPane.showMessageDialog(this, "Error al procesar el EEG: " + eegItem, "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

        } catch (Exception ex) {
            logger.error("Error al mostrar el gráfico: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Ocurrió un error al procesar los datos EEG.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exitProgram() {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres salir?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUIProyectoFinal::new);
    }
    private List<String> parseElektrotsAsString(String elektrotsInput) {
    List<String> elektrotsList = new ArrayList<>();
    try {
        StringTokenizer st = new StringTokenizer(elektrotsInput, ",");
        while (st.hasMoreTokens()) {
            elektrotsList.add(st.nextToken().trim());
        }
    } catch (Exception ex) {
        logger.warning("Formato inválido para elektrots: " + elektrotsInput);
    }
    return elektrotsList;
}
    private void generateSummary(File file, Map<String, PersonalInfo> dataMap) {
        DecimalFormat df = new DecimalFormat("#.##");

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

        String[][] tableData = {
                {"File Size (KB)", String.valueOf(fileSizeKB), "", "", ""},
                {"Number of Rows", String.valueOf(numRows), "", "", ""},
                {"Number of Columns", String.valueOf(numCols), "", "", ""},
                {"", "Age", "IQ", "", ""},
                {"Min", df.format(ageMin), df.format(iqMin), "", ""},
                {"Max", df.format(ageMax), df.format(iqMax), "", ""},
                {"Mean", df.format(ageMean), df.format(iqMean), "", ""},
                {"Std Dev", df.format(ageStdDev), df.format(iqStdDev), "", ""},
        };

        displayTable(tableData);
    }
    private void displayTable(String[][] data) {
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
    private double calculateStdDev(List<Integer> values, double mean) {
        return Math.sqrt(values.stream().mapToDouble(val -> Math.pow(val - mean, 2)).average().orElse(0));
    }

}
