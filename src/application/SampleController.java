package application;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import application.Main.TockaXY;

import java.net.MalformedURLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class SampleController implements Initializable {

/* Seznam za izbiro pri nacinRacunanjaBox */
ObservableList<String> nacinRacunannjaList = FXCollections.observableArrayList("Sekvenčno", "Vzporedno", "Porazdeljeno");
	
	
	@FXML
	private ChoiceBox nacinRacunanjaBox;
	@FXML
    private Label lblNastavitve;
    @FXML
    private Font x1;
    @FXML
    private Color x2;
    @FXML
    private Button btnIzracunaj;
//    @FXML
//    private TextField StCentrov;
    @FXML
    private TextArea tfRezultati;
    @FXML
    private TextField StGrozdov;
    @FXML
    private Label lblNacinRacunanja;
    @FXML
    private Label lblPogled;
    @FXML
    private Label lbl;
    @FXML
    private Font x3;
    @FXML
    private Color x4;
    @FXML
    private Label lblSekvencno;
    @FXML
    private WebView webView;
    @FXML
    public static  WebEngine engine;
   
              
    /* Inicializira tekst v nacinRacunanjaBox  */
    @FXML
	private void initialize(){
    	nacinRacunanjaBox.setValue("Sekvenčno");
    	nacinRacunanjaBox.setItems(nacinRacunannjaList);
    }
	
    /* Tukaj prebere vse izbire in glede na te izbire zažene program */ /* ZA NAREDIT */
    @FXML
    private void menuChoice() {
 //   	long start = System.currentTimeMillis();
    	
//    	   if (Main.args.length != 2) {
//    	          System.err.println("Vnesi 2 parametra: <INPUT_FILE> (pot do datoteke z podtki) in <K> (število clusterjev)");
//    	          System.exit(-1);
//    	      }
    }
    public static Map<String, TockaXY> output;
    
    public static List<TockaXY> tocke;
    
    @FXML
    void displayResoults(ActionEvent event) {
///////////////////////////////////////////////
    		String rezultati = "rezultati.txt";
    		// Pot do datoteke z podatki
    		String inputFile = "/home/gredolgor/eclipse-workspace/Prog3/Projekat/kmGUI/s2.txt";
    		// zrihtaj še da lahko user vnese samo številjke in ne stringa ali neki   	
    		
    		String d= StGrozdov.getText(); 
    		System.out.println(d);
    		int k= Integer.valueOf(d); 
    		
    		System.out.println("eco me do if ");
    			//btnIzracunaj.setOnAction(event -> d.setText(StCentrov.getText()));
    			// Število clusterjev
    			if (1< Integer.valueOf(d) && Integer.valueOf(d)< 11) {
    				k = Integer.valueOf(d);
    			}else {System.err.println("Število clusterjev je lahko med 2 in 10");}

    			List<TockaXY> dataset = null;
    			try {
    				dataset = Main.podatki(inputFile);
    			} catch (Exception e) {
    				System.err.println("ERROR: Ni mogoče brati datoteke " + inputFile);
    				System.exit(-1); 
    			}
    			/* Če bi želel zapisat v file (osnova) */
    			try (BufferedWriter bw = new BufferedWriter(new FileWriter(rezultati))) {
    				String content = "This is the content to write into file\n";
    				bw.write(content);

    				// baje ni treba zapret, če pa bi  
    				//bw.close(); 

    				System.out.println("Done writing");
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			
    			/* Tu nafilamo parametre za randomCentri */
   	      List<TockaXY> centers = Main.randomCentri(k, 0, 1000000);
   	        	     
   	      
   	      if (nacinRacunanjaBox.getValue() == "Sekvenčno") {
   	    	  System.out.println("Sekvenčna verzija");
    	      long start = System.currentTimeMillis();
    	      Main.kmeans(centers, dataset, k);
    	      output = Main.pobarvajClusterje(Main.kmeans(centers, dataset, k), k);
    	      redraw(output);
    	      try {
				tocke=Main.podatki(inputFile);
			} catch (Exception e) {
				e.printStackTrace();
			} 
    	      System.out.println("Pretekel čas: " + (System.currentTimeMillis() - start) + "ms");
    			System.out.println("Kmeans : " + output);	

    	      tfRezultati.setText("Sekvenčna verzija:" + "\n" +"Pretekel čas: " + (System.currentTimeMillis() - start) + "ms");
   	      }
   	      else if (nacinRacunanjaBox.getValue() == "Vzporedno") {    
    	      System.out.println("Paralelna verzija");
    	      long start2 = System.currentTimeMillis();
    	      Main.concurrentKmeans(centers, dataset, k);
    	      System.out.println("Pretekel čas: " + (System.currentTimeMillis() - start2) + "ms");
    	      tfRezultati.setText("Vzporedna verzija:" + "\n" +"Pretekel čas: " + (System.currentTimeMillis() - start2) + "ms");
   	      } 
   	      else {
   	    	  long start3 = System.currentTimeMillis();
//   	    	  Main.porazdeljenKmeans(centers, dataset, k);
   	    	  System.out.println("Pretekel čas: " + (System.currentTimeMillis() - start3) + "ms");
   	    	  tfRezultati.setText("Porazdeljena verzija:" + "\n" +"Pretekel čas: " + (System.currentTimeMillis() - start3) + "ms");
   	      }
   }
    
    /* Tukaj pišeš stvari ki hočeš da se izvajao v ozadju in se zloudajo takoj ko se prikaže grafika */
    	@Override
	public void initialize(URL location, ResourceBundle resources) {
    		engine = webView.getEngine();
    //		engine.load("https://www.openstreetmap.org");
    //		File f = new File("\\home\\gredolgor\\eclipse-workspace\\Prog3\\Projekat\\kmGUI\\src\\application\\xhtml.html");
    //		engine.load(f.toURI().toString());
    		try { File file = new File("/home/gredolgor/eclipse-workspace/Prog3/Projekat/kmGUI/src/application/xhtml.html");
             URL url = file.toURI().toURL();
			 System.out.println("Local URL: " + url.toString());
             engine.load(url.toString());
    		} catch (MalformedURLException e) {
				e.printStackTrace();
			}
    		
    		engine.getLoadWorker().stateProperty().addListener(new WebviewLoadListner(SampleController.output,SampleController.tocke));
    			
//			addMarker(layer_markers, 6.641389, 49.756667,"SERVUS 1");
			
    } 
    	
    	public void redraw(Map<String, TockaXY> output) {
    		engine = webView.getEngine();
    //		engine.load("https://www.openstreetmap.org");
    //		File f = new File("\\home\\gredolgor\\eclipse-workspace\\Prog3\\Projekat\\kmGUI\\src\\application\\xhtml.html");
    //		engine.load(f.toURI().toString());
    		try { File file = new File("xhtml.html");
             URL url = file.toURI().toURL();
			 System.out.println("Local URL: " + url.toString());
             engine.load(url.toString());
    		} catch (MalformedURLException e) {
				e.printStackTrace();
			}
    		
    		engine.getLoadWorker().stateProperty().addListener(new WebviewLoadListner(output,SampleController.tocke));
    	    			
    	}
    	

}
