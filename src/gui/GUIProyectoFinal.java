package proyectoProgAvanzada;
import model.file_object.EEG;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.logging.Logger;
import model.file_object.PersonalInfo;
import java.io.File;

public class GUIProyectoFinal extends JFrame {

    private static final Logger logger = Logger.getLogger(GUIProyectoFinal.class.getName());
    private JTextField filePathField; // ruta del archivo
    private JTextField partitionsField; //  número de particiones
    private Map<String, PersonalInfo> personalInfoMap; // mapa de información personal

    public GUIProyectoFinal() {
        setTitle("Proyecto Final - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); 
        setLayout(new BorderLayout(10, 10)); // margenes entre secciones

        // Título en la parte superior
        JLabel titleLabel = new JLabel("Proyecto Final - Menú Principal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Panel para los botones principales
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // botones en columna con espaciado
        JButton processFileButton = new JButton("1. Procesar Archivo");
        JButton showStatsButton = new JButton("2. Mostrar Estadísticas");
        JButton viewEEGButton = new JButton("3. Ver Datos EEG");
        JButton exitButton = new JButton("4. Salir");

        buttonPanel.add(processFileButton);
        buttonPanel.add(showStatsButton);
        buttonPanel.add(viewEEGButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER); // botones en el centro

        // Panel para las entradas de usuario
        JPanel inputPanel = new JPanel(new GridBagLayout()); // mas control sobre el posicionamiento
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // margenes entre los elementos
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

        add(inputPanel, BorderLayout.SOUTH); // entradas en la parte inferior

        // inicializa mapa para estadísticas
        personalInfoMap = new HashMap<>();

        // listeners para los botones
        processFileButton.addActionListener(e -> processFile());
        showStatsButton.addActionListener(e -> showStats());
        viewEEGButton.addActionListener(e -> viewEEG());
        exitButton.addActionListener(e -> exitProgram());

        setLocationRelativeTo(null); // centrar la ventana
        setVisible(true);
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

            // Procesar archivo y llenar el mapa
            Map<String, PersonalInfo> resultMap = util.file.FileUtil.splitFileInColumns(file, ",", "|", 8, regex);
            if (resultMap == null || resultMap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron datos válidos en el archivo.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            personalInfoMap.clear();
            personalInfoMap.putAll(resultMap);
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

        java.util.List<Integer> iqList = new ArrayList<>();
        for (PersonalInfo info : personalInfoMap.values()) {
            iqList.add(info.getIQ());
        }

        if (iqList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron datos de IQ para analizar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Calcular estadísticas
        double average = iqList.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        int median = calculateMedian(iqList);
        int mode = calculateMode(iqList);

        // Mostrar resultados
        String message = String.format(
                "Estadísticas de IQ:\nPromedio: %.2f\nMediana: %d\nModa: %d",
                average, median, mode
        );
        JOptionPane.showMessageDialog(this, message, "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
    }

    private int calculateMedian(java.util.List<Integer> iqList) {
        Collections.sort(iqList);
        int size = iqList.size();
        if (size % 2 == 0) {
            return (iqList.get(size / 2 - 1) + iqList.get(size / 2)) / 2;
        } else {
            return iqList.get(size / 2);
        }
    }

    private int calculateMode(java.util.List<Integer> iqList) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int iq : iqList) {
            frequencyMap.put(iq, frequencyMap.getOrDefault(iq, 0) + 1);
        }
        return frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow().getKey();
    }

    private void viewEEG() {
        if (personalInfoMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero procesa el archivo para cargar los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Solicitar al usuario que seleccione un registro
        String record = JOptionPane.showInputDialog(this, "Ingrese el ID del registro (1-10):");
        if (record == null || record.trim().isEmpty()) {
            return; // Usuario canceló la entrada
        }

        // Validar entrada
        record = record.trim();
        if (!personalInfoMap.containsKey(record)) {
            JOptionPane.showMessageDialog(this, "Registro no encontrado. Verifique el ID e intente nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mostrar la gráfica del EEG correspondiente
        try {
            EEG eeg = personalInfoMap.get(record).getFirstEEG();
            if (eeg == null) {
                JOptionPane.showMessageDialog(this, "No hay datos EEG disponibles para este registro.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            eeg.displayGraphics();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al mostrar la gráfica: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.severe("Error al mostrar la gráfica: " + ex.getMessage());
        }
    }


    private void exitProgram() {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres salir?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
