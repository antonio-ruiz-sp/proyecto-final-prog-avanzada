package model.worker_manager;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class EEG {
    private static Logger logger = LogManager.getLogger(EEG.class);
    String no;
    double[] metrics;
    //DefaultCategoryDataSet defCatDS;
    DefaultCategoryDataset defCatDS;   
    
    public EEG(String no, double[] metrics) {
        this.no = no;
        this.metrics = metrics;
        //this.eegName = "EEG_Elektrot_"+no;
    }
    public void displayGraphics(){
        
        logger.debug("Entering displayGraphics");
        SwingUtilities.invokeLater(() -> {  
            XYSeries series = new XYSeries("EEG Data");
            double[] eegReadings = parseEEG("X");
            for(int i = 0; i< eegReadings.length; i++){
                series.add(i, eegReadings[i]);
            }
                
            XYSeriesCollection dataset = new XYSeriesCollection(series);  
            JFreeChart chart = ChartFactory.createXYLineChart(  
                    "EEG_Elektrot_1",  
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
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
            frame.add(chartPanel);  
            frame.pack();  
            frame.setVisible(true);  
        });  
    }
    
        
    public static double[] parseEEG(String eeg_string) {
        logger.debug("Entering parseEEG...");
        double[] result = {2.6019423635486465, 3.540371604979485, 0.9609250412751008, -0.4164976930414887, 0.21526728422639177, 
            -0.07165589153210532, -0.3898004112180022, 0.8644281281075242, 1.119118172819912, 1.427344427085588, -0.8843407809833588, 
            -0.6340986855296802, -0.6291372105399154, -2.621901303046682, 1.9370210225890354, -0.07650044270785443, -0.4355146386295813, 
            -0.4114959634800343, 0.6299191254113096, -0.2525107011834211, 0.6140009315860746, -0.6123706334242315, -0.4096554541077786, 
            -0.07715675739786512, -0.4767129473575854, -1.103614862816703, 0.5829211967990185, -0.6116826015525053, 2.1344782611634585, 
            -1.5220442767615414, -0.6412590235304821, 0.7740081823085226, 1.3444123188377166, 1.0817337570585157, -0.8950618521530275, 
            -0.8570875223870916, 1.0259765438845725, 0.31362436996426674, -1.7582415998664165, 1.6472895491578146, 1.978179368167025, 
            0.15030685754334994, 1.0974528914612458, 0.3159661529281205, 0.2061806656114853, -1.8646755809158646, -1.284437204632527, 
            0.5527722092163174, 0.14272121109773445, -0.08956136476062473, 0.30482871552583457, 0.06690672254219643, 0.611357658167547, 
            0.5513202219218716, -1.169518992239012, 1.0959071200580777, 0.8651305392813349, -1.046021448546959, -0.179075055977896, 
            0.30938849840535854, 0.5581372936293324, -0.37330549750325764, 1.4628931896049737, 0.8449144421036454, 1.9651595493972625, 
            -0.6681648154817568, 0.2417792788343344, 1.1256753761964156, -0.052389978753280976, -2.5571220066286906, 0.4906090243229439, 
            0.8567130723724575, 0.309070572128335, 1.1790996847570545, 0.4586272904364096, 1.661300473131957, -1.0359137847749669, 0.27917732694969716,
            -0.8379145393028384, 1.2682973207817083, 1.7249853738822851, -2.6108945259111955, 0.6995260996627506, 0.3956622740379559, 
            -0.9967631954657074, -0.24854996514358893, 0.1788169618302546, 1.3606452245879204, -0.2860789727483181, 1.0263729308496625, 
            -0.546317096181393, 0.20640238863649132, -0.013313264486149697, 2.1692707884553566, -0.2697562409273901, 1.1539898704989873, 
            -0.04077084399289299, 1.0046326752576684, 0.7028803105710308, 1.8191990216143574, 1.0686451104752988, 1.1977094904441252, 
            -0.6208259868057102, 0.8018953233471134, 0.09645091638545554, 0.5380953440417431, 0.06014999408370755, 0.10404273515236584, 
            -0.2406918921704755, -1.0445258693467399, 0.2802082109603265, 1.5794116955467479, 0.5999814914559332, 0.38445869374463715, 
            -1.3670427435991148, 1.074040040718309, -1.108020703905181, -0.15847933271455472, -0.2966407866707183, -2.6202702036284737, -1.7061026317886847, 0.6247097160212052, -0.6733575264695477, -0.30430635616123575, 
            -0.6699152926033473, -0.17220311157166124, 1.6605814817874456, 0.6882287973865189, -0.7988870705010118, -0.04030800531239819, -0.6182142450758501, -0.8700777198857512, -0.8165935966731188, 0.7443548377711602, 
            2.0300075479868327, -0.5715357842945356, -0.30056665781921593, -1.038340075183336, 0.29494506820627714, -1.5947647954356496, -1.0778372720506186, 0.8149487049539736, -0.9353285270347222, -0.5943699992746379, 
            -1.6827724406177214, 0.9819077821832328, -1.03030043510044, -0.4923653305439699, -0.07591871017519275, 0.1047055452510997, -2.237106631268824, -0.3242581644380016, -1.051119959497565, -2.4162558142850945, 
            -1.3085807297943757, 0.3153351432634656, -0.41063987776344635, 0.7188143806493169, -1.0738996972543406, 1.3064450269009065, 0.6321293316945044, -0.5417464649998277, -0.04927358222954935, 2.2242022244544666, 
            1.6230338531442132, -0.6714292556321121, -1.1180332102606214, 1.7699478291492803, -0.5873587411635831, 0.7081689259794827, -2.1727178538512177, 1.367122418246896, -0.3762204120100284, 1.1190304958501374, -0.31648888277701587, 
            -0.568149325725953, -0.4084727097554031, 0.823211987164342, -0.21191179964000303, -1.0519403368094726, 0.5475549317023098, 1.6321377094735456, 1.534156637907164, -1.9640419005238885, -0.18142040367059842, 0.09140404006883708, 
            0.9355315800678868, -0.6217778894160368, 0.7922868412261933, -1.9920420896210544, -0.7419687676989467, -0.048394644704586764, -0.7285365276472723, -0.6010489269639109, -0.30119373380419356, -0.5786924894730863, -0.793223914974951, 
            0.3694714162780482, 1.4207711047367184, 1.126625914108573, 2.3674245418802924, 0.15611259102671932, -2.047410699905879, 1.2619368713811843, 0.17712079702396796, -0.5033985943800537, 0.45883341068855926, -0.05839878290452457, 
            -0.3982458974969479, 0.26388785193761416, 0.7493150042428594, -0.3883901944627989, -1.1297951324624325, -2.3866297597843587, 0.44127038448204614, -0.2215533791260159, 0.3767918966290825, -0.38894208292832294, 0.41672502118539945, 
            -1.3015891934556043, -0.07325287987506911, -0.6933574901209986, -2.644189367081205, -0.3910482774124275, -2.1867814432096333, 2.7574239096354782, -0.9793741165110645, -0.4885776099029951, 0.05809737285903956, -0.11512096214528975, 
            0.013281041360747939, 0.1482584216125057, -1.1301160077322805, 0.4048924407293548, -0.7025897725539925, 0.6431538980293657, 0.18087842072549654, -0.5487843394825647, -0.06049325467289124, 0.15933497260178783, -3.062746047722807, 
            -1.5357160376126178, -3.0293782741695994, 0.16280994561697373, -0.855006366008742, -1.540920264850214, 0.9030497431996508, -1.1754486896188927, -0.34916049392676907, 0.17629335639839788, -0.4388223997263195, -0.302852399267824, 
            -0.06907750166813523, 0.8610589750386383, -0.9632512124556527, 0.011203351811018203};
        logger.debug("double[]  has {} values ", result.length);
        return result;
    }
}
