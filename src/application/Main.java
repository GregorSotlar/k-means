package application;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.org.apache.xerces.internal.xs.StringList;

import application.Main.TockaXY;
import javafx.application.Application;
import javafx.stage.Stage;
import mpi.*;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;



/*
 * Za zagnat program brez GUI se vnese 3 argumente(args):
 * 1. pot do daoteke z koordinatami (trentuno: s2.txt)
 * 2. število clusterjev (trenutno: 4)
 * 3. način računanja (trenutno: vzporedno)
 * 
 * Preko GUI se izbriše vse argumente in se zažene program. (zažene ma ne dela)
 * 
 * */


public class Main extends Application  {
	
	
//////JAVAFX STUFF		JAVAFX STUFF		JAVAFX STUFF		JAVAFX STUFF		JAVAFX STUFF		//////	
@Override
public void start(Stage primaryStage) {
	try {
		primaryStage.setTitle("K-means clustering");
		VBox root = (VBox)FXMLLoader.load(getClass().getResource("Sample.fxml"));
		Scene scene = new Scene(root,800,600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	} catch(Exception e) {
		e.printStackTrace();
	}
}
/////////////////////////////////////////// KONEC JAVAFX STUFF ////////////////////////////////////////////////////////////////////

	private static boolean koncanoRacunanje = false;
	private static boolean podatkiVDataset = false;

	public static List<TockaXY> dataset = new ArrayList<>();
	public static List<TockaXY> randomCentri = new ArrayList<>();
	public static List<TockaXY> koncniCentri = new ArrayList<>();

  		/* Class TockaXY predstavlja koordinate vskake točke */
      public static class TockaXY {
      
      private float x;
      private float y;
      
      public TockaXY(float x, float y) { 
          this.x = x;
          this.y = y;  
      }
      public  float getX() {
    	  return x;
      }
      public float getY() {
    	  return y;
      }
      	 
	 /* Evklidska razdalja med dvemi točkami */  
	 private double dobiRazdaljo(TockaXY other) {
	     return Math.sqrt(Math.pow(this.x - other.x, 2)
	             + Math.pow(this.y - other.y, 2));
	 }  
 
      
      
      /* vrne index najbližje točke v sezanmu, tako, da se sprehodi čez seznam točk in prepisuje minDist če je odkrita krajša razdlja 
         in zapiše njen indeks v index, vrne index točke z najkrajšo razdaljo, rabimo za izračun najbližjega centra*/
      public int najblizjaTIndex(List<TockaXY> points) {
          int index = -1;							// = -1, ker lahko bo index najbližje točke = 0
          double minDist = Double.MAX_VALUE;		// največja možna pozitivna vrednost
          for (int i = 0; i < points.size(); i++) {
              double dist = this.dobiRazdaljo(points.get(i));
              if (dist < minDist) {
                  minDist = dist;
                  index = i;
              }
          }
          return index;
      }
      
	  
  	/* Metoda ki dobi seznam točk (points) in vsako točko v seznamu prišteva prejšnjim po X in po Y in potem zračuna njihovo povprečje. 
  	   Vrne središčno točko tega seznama, rabimo za izračun novih centrov */
      public static TockaXY povprecje(List<TockaXY> points) {
          float accumX = 0;
          float accumY = 0;
          if (points.size() == 0) return new TockaXY(accumX, accumY);
          for (TockaXY point : points) {
              accumX += point.x;
              accumY += point.y;
          }
          return new TockaXY(accumX / points.size(), accumY / points.size()); 
      }
          
      // Metoda za zapisat x in y v string
    @Override
      public String toString() {
          return "[" + this.x + "," + this.y + "]";
      }
    // Preverimo, če ni null 
     @Override
      public boolean equals(Object obj) {
          if (obj == null || !(obj.getClass() != TockaXY.class)) {
              return false;
          }
          TockaXY other = (TockaXY) obj;
          return this.x == other.x && this.y == other.y;
      }
    
  }

      /* Dobi pot do vhodne datoteke in vrne seznam točk. BufferedReader prebere vsebino datoteke in jo vstavi v String line, ki ga razedlimo z ","
   	 	in pretvorimo v tokene (torej en token je levo od vejice drugi desno) katere uporabimo za ustavrit novo instanco TockaXY
   	 	iz njih ustvarit seznam ki bo vrnjen (dataset). */
   
      public static List<TockaXY> podatki(String inputFile) throws Exception {
      List<TockaXY> dataset = new ArrayList<>();
      BufferedReader br = new BufferedReader(new FileReader(inputFile));
      String line;
     
      while ((line = br.readLine()) != null) {
          String[] tokens = line.split(",");
          float x = Float.valueOf(tokens[0]);
          float y = Float.valueOf(tokens[1]);
          TockaXY point = new TockaXY(x,y);
//          System.out.println("Tokens x(0) = "+ x + " \n " +"Tokens y(1) =" + y);
     //    for (int i = 0; i < mnogokratnik; i++) // Če želimo pomnožit število podatkov
              dataset.add(point);
      }
      br.close();
      podatkiVDataset= true;
      System.out.println(" Data = " + dataset);
      return dataset;
  }
  

  	/* Inicializiramo seznam random centralnih točk in jih damo v Array (n= število clusterjev) */
  public static List<TockaXY> randomCentri(int n, int lowerBound, int upperBound) {
      List<TockaXY> centers = new ArrayList<>(n);
      for (int i = 0; i < n; i++) {
          float x = (float)(Math.random() * (upperBound - lowerBound) + lowerBound)/100;
          float y = (float)(Math.random() * (upperBound - lowerBound) + lowerBound)/100;
          TockaXY point = new TockaXY(x, y);
          centers.add(point);
      }
      System.out.println(" Random centri: " + centers);
      return centers;
      
  }


  	/* JEDRO
	 Najprej dodelimo seznam iz seznamov (clusters), ki ga inicializiramo kot praznega (centers.size()). 
	 Nato (2. for loop) za vsak podatek (točko) iz dataset dobimo index najbližjega centra z metodo najblizjaTIndex,
	 na koncu (3. for loop) še v zadnjem for loopu za vsak cluster v arrayu clusters izračunamo mean in ga dodamo v noviCentri*/
  public static List<TockaXY> noviCentri(List<TockaXY> dataset, List<TockaXY> centers) {
		
	  List<List<TockaXY>> clusters = new ArrayList<>(centers.size());        
      for (int i = 0; i < centers.size(); i++) {
          clusters.add(new ArrayList<TockaXY>());
      }
      System.out.println("\n" + "noviCentri 2.for loop: ");
      for (TockaXY data : dataset) {
          int index = data.najblizjaTIndex(centers);
          clusters.get(index).add(data);         
          System.out.print(centers);      
      }
      List<TockaXY> noviCentri = new ArrayList<>(centers.size());

      System.out.println("\n" + "Novi centri v cluster 3. for loop: ");	
      for (List<TockaXY> cluster : clusters) {
          noviCentri.add(TockaXY.povprecje(cluster));
      }
      System.out.println( Arrays.toString(noviCentri.toArray())

);
      return noviCentri;
  }
  
  	/* Metoda sešteva razdalje med starimi in novimi centri s pomočjo metode dobiRazdaljo */
  public static double dobiRazdaljoCenters(List<TockaXY> oldCenters, List<TockaXY> noviCentri) {
      double accumDist = 0;
      for (int i = 0; i < oldCenters.size(); i++) {
          double dist = oldCenters.get(i).dobiRazdaljo(noviCentri.get(i));
          accumDist += dist;
      }
		return accumDist;
  }
  
  	/* Metoda dobi tri parametre,  centre (centers), število clusterjev (k) in točke (dataset).
 		Vrne seznam točk, ki so konvergirani centri. 
  		To dela tako, da vsakič kliče metodo noviCentri in preračuna razdaljo med novimi in starimi centri in to počne dokler ni razlike med njimi.
  		Ko je razdalja med njimi 0 vrne centre*/
  public static List<TockaXY> kmeans(List<TockaXY> centers, List<TockaXY> dataset, int k) {
      boolean converged=false;
      do {
          List<TockaXY> noviCentri = noviCentri(dataset, centers);
          double dist = dobiRazdaljoCenters(centers, noviCentri);
          centers = noviCentri;
          converged = dist == 0;
          System.out.println("Kmenas znotraj: ");
          for (int i = 0; i <centers.size(); i++) { 
                  System.out.print(centers.get(i) + " "); 
          }
          if (converged) {
              koncanoRacunanje = true;
              System.out.println("\n" +"Kmenas centri v fun: ");
              for (int i = 0; i <centers.size(); i++) { 
                      System.out.print(centers.get(i) + " "); 
              }
              koncniCentri=centers;

          }
      } while (!converged);
      return centers;
  }
  

/////////// VZPOREDNA VERZIJA /////////////// VZPOREDNA VERZIJA //////////////////// VZPOREDNA VERZIJA /////////////

/* VZPOREDNA VERZIJA - ubistvu se vzporedno računa samo najbližji center za vsako točko */
  
  
  
  private static final int NUM_THREADS = 15; 
  
  public static List<TockaXY> concurrentKmeans(List<TockaXY> centers, List<TockaXY> dataset, int k) {
      boolean converged;
      do {
          List<TockaXY> noviCentri = concurrentNoviCentri(dataset, centers);
          double dist = Main.dobiRazdaljoCenters(centers, noviCentri);
          centers = noviCentri;
          converged = dist == 0;
          if (converged) {
              koncanoRacunanje = true;
              System.out.println("Kmenas centri v fun: ");
              for (int i = 0; i <centers.size(); i++) { 
                      System.out.print(centers.get(i) + " "); 
              }
          }
      } while (!converged);

      return centers;
  }
  
  	/* Kot metoda noviCentri le da porazdeli podatke z metodo partition na threade (število smo določli v NUM_THREADS) in kilče metodo createWorker, 
  	   kjer porazdeli delo workerjem na porazdeljenih podatkih */
  public static List<TockaXY> concurrentNoviCentri(final List<TockaXY> dataset, final List<TockaXY> centers) {
      final List<List<TockaXY>> clusters = new ArrayList<List<TockaXY>>(centers.size());
      for (int i = 0; i < centers.size(); i++) {
          clusters.add(new ArrayList<TockaXY>());
      }
      List<List<TockaXY>> partitionedDataset = partition(dataset, NUM_THREADS);
      
      /* ExecutorService - omogoča, da izvajamo samo omejeno število thredov naenkrat, newFixedThreadPool določi omejeno število threadov, 
       availableProcessors() pa poda število procesorjev, ki so na voljo */
      ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); /* Treba še shutdown FixedThreadPool? */
      List<Callable<Void>> workers = new ArrayList<>();
      for (int i = 0; i < NUM_THREADS; i++) {
          workers.add(createWorker(partitionedDataset.get(i), centers, clusters));
      }
      try {
    	  /* Doda vse thrade v pool in ustavi trenutni thread dokler vsi threadi ne končajo. Pričakuje seznam callable */
          executor.invokeAll(workers);		
      } catch (InterruptedException e) {
          e.printStackTrace();
          System.exit(-1);
      }
      List<TockaXY> noviCentri = new ArrayList<>(centers.size());
      for (List<TockaXY> cluster : clusters) {
          noviCentri.add(TockaXY.povprecje(cluster));
      }
      return noviCentri;
  }
  

  /* Podobno kot noviCentri pri sekvenčni veziji. Ne uporabimo synchronized v prvem loopu, ker bi ubilo paralelnost (samo en thread bi lahko izvajal loop) */
  private static Callable<Void> createWorker(final List<TockaXY> partition, final List<TockaXY> centers,
          final List<List<TockaXY>> clusters) {
      return new Callable<Void>() {

          @Override
          public Void call() throws Exception {
              int indexes[] = new int[partition.size()];
              for (int i = 0; i < partition.size(); i++) {
                  TockaXY data = partition.get(i);
                  int index = data.najblizjaTIndex(centers); 
                  indexes[i] = index;
              }
              
              /* Ta del je sinhroniziran, ker zapisuje v clusterje in to je hitro */
              synchronized (clusters) {
                  for (int i = 0; i < indexes.length; i++) {
                      clusters.get(indexes[i]).add(partition.get(i));
                  }    
              }
              return null;
          }
          
      };
  }
  
  
  	/* Metoda dobi seznam in ga prepiše v drugi seznam tako, da ga razdeli glede na določeno število parts in vrne seznam lists
  	 	rabimo za concurrentNoviCentri */
  private static <V> List<List<V>> partition(List<V> list, int parts) {
      List<List<V>> lists = new ArrayList<List<V>>(parts);
      for (int i = 0; i < parts; i++) {
          lists.add(new ArrayList<V>());
      }
      for (int i = 0; i < list.size(); i++) {
          lists.get(i % parts).add(list.get(i));
      }
      return lists;
  }


  
  public static int k;
  private static int id;
  private static int size;
  private static int root = 0;
  public static long startTime;
  public static long stopTime;
  
///////// MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	///////
///////// MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	///////  
///////// MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	MAIN	///////
  
  public static void main(String[] args) { 
	if (args.length == 0) {
	  launch(args); 
	}else {

		 
	  /* Opozorilo če premalo argumentov */
      if (args.length >= 1 && args.length <3 && args.length >3) {
          System.err.println("Vnesi 3 parametre: <INPUT_FILE> (pot do datoteke z podtki), <K> (število clusterjev) in Način računanja (sekvencno, vzporedno, porazdeljeno) ali pa ne vnesi noben args in se bo zagnal grafični vmesnik");
          System.exit(-1);
      } 
      
      // zapakiramo argumente za Porazdeljen.java
     // argumenti = args;
      
      
      // Pot do datoteke z podatki
      String inputFile = args[0];
      
      // Število clusterjev
      if (1< Integer.valueOf(args[1]) && Integer.valueOf(args[1])< 11) {
       k = Integer.valueOf(args[1]);
      }else {System.err.println("Število clusterjev je lahko med 2 in 10");}
      
      List<TockaXY> dataset = null;
      try {
          dataset = podatki(inputFile);
      } catch (Exception e) {
          System.err.println("ERROR: Ni mogoče brati datoteke " + inputFile);
          System.exit(-1); 
      }

      /* Tu nafilamo parametre za randomCentri */
      //List<TockaXY> centers = randomCentri(k, 1, 10000);
      String nacinRacunanja = (args[2]);
//      System.out.println("Sem pred načinom računanja: " + args[2]);
      
   if (nacinRacunanja.equals("sekvencno") ) {
      System.out.println("Sekvenčna verzija");
      long start = System.currentTimeMillis();
      kmeans(randomCentri(k, 1, 10000), dataset, k);
      if (koncanoRacunanje == true) {
      System.out.println("Pretekel čas: " + (System.currentTimeMillis() - start) + "ms");
      System.out.println("Koncni centri: " );
      for (int i=0; i<koncniCentri.size(); i++) {
    	  System.out.println(koncniCentri.get(i));
      }
      }
      }
   else if (nacinRacunanja.equals("vzporedno")){
      System.out.println("Vzporedna verzija");
      long start1 = System.currentTimeMillis();
      concurrentKmeans(randomCentri(k, 1, 10000), dataset, k);
      if (koncanoRacunanje == true) {
      System.out.println("Pretekel čas: " + (System.currentTimeMillis() - start1) + "ms");
      }
      }
   
   /////////// PORAZDELJEN DEL//////////////  MPI ///////////////
   /////////// PORAZDELJEN DEL//////////////  MPI //////////////// NE DELA ŠE /////////////////
   /////////// PORAZDELJEN DEL//////////////  MPI ///////////////
   else if (nacinRacunanja.equals("porazdeljeno")) {
	   System.out.println("Porazdeljena verzija");

	      }
   else {System.out.println("Ni bil zaznan način računanja");
   }
   }
   System.exit(0);

  
  }

}





