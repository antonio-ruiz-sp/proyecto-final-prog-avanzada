package model.file_object;

import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class EEG {
    private static Logger logger = LogManager.getLogger(EEG.class);
    private String no;
    private String mainDisorder;
    private String specificDisorder;
    private double[] metrics;
    private String elektrotNumber; //corresponds to the number of eeg reading
    private DefaultCategoryDataset defCatDS;   
    
    public EEG(String no, double[] metrics, String mainDisorder, String specificDisorder, String eegNumber) {
        this.no = no; //corresponds to number of record/pacient
        this.metrics = metrics;
        //this.eegName = "EEG_Elektrot_"+no;
        this.mainDisorder = mainDisorder;
        this.specificDisorder = specificDisorder;
        this.elektrotNumber = eegNumber;
        
    }
    public void displayGraphics(){
        
        logger.debug("Entering displayGraphics");
        SwingUtilities.invokeLater(() -> {  
            XYSeries series = new XYSeries("EEG Data ["+ mainDisorder +" - "+specificDisorder+"]");
            
            for(int i = 0; i< metrics.length; i++){
                series.add(i, metrics[i]);
            }
                
            XYSeriesCollection dataset = new XYSeriesCollection(series);  
            JFreeChart chart = ChartFactory.createXYLineChart(  
                    "EEG_Elektrot_"+elektrotNumber+" ["+this.no+"]",  
                    "X-Axis",  
                    "mV",  
                    dataset,  
                    PlotOrientation.VERTICAL,  
                    true,  
                    true,  
                    false  
            );  

            XYPlot plot = chart.getXYPlot();  
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();  
            yAxis.setRange(-5, 5); // Set the Y-axis range to include negative values  

            ChartPanel chartPanel = new ChartPanel(chart);  
            JFrame frame = new JFrame("EEG Chart");  
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);  
            frame.add(chartPanel);  
            frame.pack();  
            frame.setAlwaysOnTop(true);
            frame.setVisible(true);  
        });  
    }
    
        
    public static double[] parseEEG(String eeg_string) {
        //logger.debug("Entering parseEEG...");
        logger.debug("eeg_string: {} ", eeg_string);
        //eeg_string = eeg_string.replace("\"\["], "");
        String regexLeft = "\"[";
        String regexRight = "]\"";
        //TO-DO try to make into one regex rather than two
        eeg_string = eeg_string.replace(regexLeft, "");
        eeg_string = eeg_string.replace(regexRight, "");
        
        //logger.debug("eeg_string: {} ", eeg_string);
        
        StringTokenizer eeg_values =new StringTokenizer(eeg_string, ",");
        int numValues = eeg_values.countTokens();
        //logger.debug("there are {} comma-separated values in eeg_string", numValues);
        double[] result = new double[numValues]; //change it so it is the amount of comma-separated values in eeg_string
        int index =0;
        while(eeg_values.hasMoreTokens()){
            String token = eeg_values.nextToken(); 
            result[index++] = Double.parseDouble(token);
        }
        
        
        //logger.debug("double[]  has {} values ", result.length);
        return result;
    }
}
