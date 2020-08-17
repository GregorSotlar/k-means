package application;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.Main.TockaXY;
import mpi.*;



public class Porazdeljeno {
	private static boolean koncanoRacunanje = false;
	public static List<TockaXY> koncniCentri = new ArrayList<>();

	
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
	
	// Inicializacija ArrayList in Array
		public static List<TockaXY> dataset = new ArrayList<>();	
	      
	 public static List<TockaXY> podatki(String inputFile) throws Exception {
	      List<TockaXY> dataset = new ArrayList<>();
	      BufferedReader br = new BufferedReader(new FileReader(inputFile));
	      String line;
	     
	      while ((line = br.readLine()) != null) {
	          String[] tokens = line.split(",");
	          float x = Float.valueOf(tokens[0]);
	          float y = Float.valueOf(tokens[1]);
	          TockaXY point = new TockaXY(x,y);
//	          System.out.println("Tokens x(0) = "+ x + " \n " +"Tokens y(1) =" + y);
	     //    for (int i = 0; i < mnogokratnik; i++) // Če želimo pomnožit število podatkov
	              dataset.add(point);
	      }
	      br.close();
//	      System.out.println(" Data = " + dataset);
	      return dataset;
	  }
	
	// RandomCentri prejme število centrov = število clusterjev in zgornjo in spodnjo mejo za Math.random. 
		 // Vrne Array, kjer je vsak center Array z koordinatama [x,y]
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
	      for (TockaXY data : dataset) {
	          int index = data.najblizjaTIndex(centers);
	          clusters.get(index).add(data);
	          
	      }
	      List<TockaXY> noviCentri = new ArrayList<>(centers.size());
//			System.out.println("Novi centri pred funkcijo: " + noviCentri);	

	      for (List<TockaXY> cluster : clusters) {
	          noviCentri.add(TockaXY.povprecje(cluster));
//	          System.out.println("Novi centri v fun: "+ noviCentri);
	      }

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
	  public static List<TockaXY> kmeans2(List<TockaXY> centers, List<TockaXY> dataset, int k) {
	      boolean converged=false;
	      do {
	          List<TockaXY> noviCentri = noviCentri(dataset, centers);
	          double dist = dobiRazdaljoCenters(centers, noviCentri);
	          centers = noviCentri;
	          converged = dist == 0;
	          if (converged) {
	        	koncanoRacunanje = true;
//	              System.out.println("Kmenas centri v fun: ");
//	              for (int i = 0; i <centers.size(); i++) { 
//	                      System.out.print(centers.get(i) + " "); 
//	              }
	              koncniCentri=centers;
	          }
	      } while (!converged);
	      koncniCentri=centers;
	      
	      return centers;
	  }
	  /// preveri če res dela
	  /// 
	  public static float[] izListVFloat (List<TockaXY> neki) {
	  float[] floatArrayX= new float[neki.size()];
	    float[] floatArrayY= new float[neki.size()];
		
	    for (int i = 0; i < neki.size(); i++)
	    	  floatArrayX[i]=neki.get(i).getX();
	    for (int i = 0; i < neki.size(); i++)
	    	  floatArrayY[i]=neki.get(i).getY();
	    	  
	    float[] floatArrayXY = new float[neki.size() * 2];
	      	int index = 0;
	      	for (int i = 0; i < neki.size(); i++) {
	      		floatArrayXY[index++] = floatArrayX[i];
	      		floatArrayXY[index++] = floatArrayY[i];
	      	}	  
	      	for(int i=0; i<floatArrayXY.length;i++) {
		    	  System.out.print(floatArrayXY[i]);
		      }
	      	return floatArrayXY;
	  }
	 
	  public static List<TockaXY> izFloatVList (float[] neki){
	    List<TockaXY> arrayListXY = new ArrayList<>(neki.length);
       for (int i = 0; i < neki.length;  i += 2) {
           TockaXY point = new TockaXY(neki[i], neki[i+1]);
           arrayListXY.add(point);
       }
       return arrayListXY;
	  }
	  
	  public static int k=4;
	  private static int id;
	  private static int size;
	  private static int root = 0;
	  public static long startTime;
	  public static long stopTime;
	  public static float[] nakljucniCentri = new float[k];
	  public static float[] podatki = new float[dataset.size()];
	  public static String inputFile = "s2alex.txt";
      

//////////////////////////////////////// MAIN ////////////////////////////////// MAIN ////////////////////////////////////////////////////
//////////////////////////TockaXY[] randomCentriStart;////////////// MAIN ////////////////////////////////// MAIN ////////////////////////////////////////////////////	  
	  
	  /*
	   * Ta class pošilja vsem procesom iste točke, tako da porazdeljeno računa več istih procesov, vsak proces nato kliče kmeans2
	   * 
	   * */
	  
	public static void main(String[] args) {
		//Main.main(args); // kličem argumente iz Main classa
	MPI.Init(args);
		
	    id = MPI.COMM_WORLD.Rank();
	    size = MPI.COMM_WORLD.Size();
		// Pot do datoteke z podatki. KAKO POSLATI PREKO VM argumente?
//	      String inputFile = args[0];
	   
			
//	     if (id==root) {
	    	 podatki = izListVFloat(dataset);
	    	 nakljucniCentri= izListVFloat(randomCentri(k, 1, 10000));
//			 System.out.println("Tukaj " + id + "so podatki " + Arrays.toString(podatki));
//			 System.out.println("Tukaj " + id + "so centri " + Arrays.toString(nakljucniCentri));
	    	 
	    	 List<TockaXY> dataset = null;
		      try {
		          dataset = podatki(inputFile);
		      } catch (Exception e) {
		          System.err.println("ERROR: Ni mogoče brati datoteke " + inputFile);
		          System.exit(-1); 
		      }
//	     }
//   
	    int stElNaProces = podatki.length; // število elemntov na proces * TODO = vsakemu en cluster za računat *
	    float[] sendBuffer = izListVFloat(dataset); //
//	    float[] sendBufferC = nakljucniCentri; //

	    //sendBuffer = new int [stElNaProces * size]; // [(število elementov na proces) * (število procesov)]
	    System.out.println("sendBuffer pred posredovanjem: " + Arrays.toString(sendBuffer));
	    
	    float receiveBuffer[] = new float [stElNaProces]; // [število elemntov na proces]
//	    float receiveBufferC[] = new float [k]; // [število elemntov na proces]


	    float New_receiveBuffer[] = new float [stElNaProces*3]; // to je za root v gather metod, in je array vseh rešitev
		
		MPI.COMM_WORLD.Scatter(sendBuffer, 0, stElNaProces, MPI.FLOAT, receiveBuffer, 0, stElNaProces, MPI.FLOAT, root);
//		MPI.COMM_WORLD.Scatter(sendBufferC, 0, stElNaProces, MPI.FLOAT, receiveBufferC, 0, stElNaProces, MPI.FLOAT, root);
//		MPI.COMM_WORLD.Scatter(sendBufferK, 0, stElNaProces, MPI.INT, receiveBufferK, 0, stElNaProces, MPI.INT, root);
	    System.out.println("sendBuffer pred procesi v Scatter: " + Arrays.toString(sendBuffer));

		// Kar delajo vsi ostali razen root
		if(id != root) {				
			
			// sendBuffer = podatki
			receiveBuffer=izListVFloat(kmeans2(izFloatVList(nakljucniCentri), izFloatVList(podatki), k));
			System.out.println(" ");
			System.out.println("Tukaj " + id + " je reciveBuffer " + Arrays.toString(receiveBuffer));
			System.out.println("Tukaj " + id + " je sendBuffer " + Arrays.toString(sendBuffer));
			System.out.println("Tukaj " + id + " so nakljucni centri: ");
			for (int i = 0; i < izFloatVList(nakljucniCentri).size(); i++) {
				System.out.print(izFloatVList(nakljucniCentri).get(i));
			}
			System.out.println(" ");
			System.out.println("Tukaj " + id + " so podatki ");
			for (int i = 0; i < izFloatVList(podatki).size(); i++) {
				System.out.print(izFloatVList(podatki).get(i));
			}	
			System.out.println(" ");
			System.out.println("k: " + k);
		}
		
		// (array kjer so vsi podatki in se jih pošlje v New_receiveBuffer,_,_,_, New_receiveBuffer je tisti ki ga root potegne, _,_,_,_ )
		MPI.COMM_WORLD.Gather(receiveBuffer, 0, stElNaProces, MPI.FLOAT, New_receiveBuffer, 0, stElNaProces, MPI.FLOAT, root);
		
		// sprintat rezultat
		if(id==root) {			
			System.out.println(" Končano New_receiveBuffer je: " );
			System.out.println(Arrays.toString(New_receiveBuffer));		
		}	
		MPI.Finalize();
	}
}
