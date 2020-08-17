package application;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.Main.TockaXY;
import mpi.*;



public class PorazdeljenoPoClusterjih {
	
	
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
		public static TockaXY[] arrayTocke = new TockaXY[dataset.size()];

		
		
		public static TockaXY[] listVarray (List<TockaXY> datset) {
	    	arrayTocke=dataset.toArray( arrayTocke );
//	    	System.out.println("Array tock: ");
//	    	for (int i=0; i<arrayTocke.length; i++) {
//	    		System.out.println(arrayTocke[i]);
//	    	}
	    	return arrayTocke;
	    }
		
	      
	 public static List<TockaXY> podatki(String inputFile) throws Exception {
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
	      System.out.println(" Data = " + dataset);
	      return dataset;
	  }
	
	// RandomCentri prejme število centrov = število clusterjev in zgornjo in spodnjo mejo za Math.random. 
		 // Vrne Array, kjer je vsak center Array z koordinatama [x,y]
		public static TockaXY[] randomCentri(int k, int lowerBound, int upperBound) {
//			   int d = k-1;
			   System.out.println("dolžina arr centri: " +k);
			   TockaXY[] start = new TockaXY[k];
			      for (int i = 0; i < k; i++) {
			          float x = (float)(Math.random() * (upperBound - lowerBound) + lowerBound)/100;
			          float y = (float)(Math.random() * (upperBound - lowerBound) + lowerBound)/100;
			          TockaXY point = new TockaXY(x, y);
			          start[i]=point;
			      }
			      return start;
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
	          System.out.println("Novi centri v fun: "+ noviCentri);
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
	              
	              System.out.println("Kmenas centri v fun: ");
	              for (int i = 0; i <centers.size(); i++) { 
	                      System.out.print(centers.get(i) + " "); 
	              }

	          }
	      } while (!converged);
	    
	      return centers;
	  }
	  
	  
	
	 
	  public static int k;
	  private static int id;
	  private static int size;
	  private static int root = 0;
	  public static long startTime;
	  public static long stopTime;
	  
	 // public static List<TockaXY> centri = Main.randomCentri(n, 1, 10000);
	  
	  
//////////////////////////////////////// MAIN ////////////////////////////////// MAIN ////////////////////////////////////////////////////
//////////////////////////TockaXY[] randomCentriStart;////////////// MAIN ////////////////////////////////// MAIN ////////////////////////////////////////////////////	  
	  
	  
	public static void main(String[] args) {
		//Main.main(args); // kličem argumente iz Main classa
		
		MPI.Init(args);
		
	    id = MPI.COMM_WORLD.Rank();
	    size = MPI.COMM_WORLD.Size();
	 
	    // Pot do datoteke z podatki
	      String fileTock = "s2alex.txt";
		  List<TockaXY> dataset = null;
	      
			 // Omejitev števila clusterjev
//	      if (1< Integer.valueOf(args[1]) && Integer.valueOf(args[1])< 11) {
//	       k = Integer.valueOf(args[1]);
//	      }else {System.err.println("Število clusterjev je lahko med 2 in 10");}
	      
	      k=2;

		
	    TockaXY[] randomCentriStart= new TockaXY [k];
	    
	    
	    if(id==root) {
	    	
	    	randomCentriStart = randomCentri (k, 1, 10000);
			
	    	System.out.println("Centri so: " );
		 	for(int b=0; b< randomCentriStart.length; b++) {
		 		System.out.print(randomCentriStart[b]);
		 	}    
		    
			  /* Zapiši points v ListArray datset */ 

		      try {
		          dataset = podatki(fileTock);
		      } catch (Exception e) {
		          System.err.println("ERROR: Ni mogoče brati datoteke " + fileTock);
		          System.exit(-1); 
		      }
		      
		      // Prepiše ArrayList v Array
		      arrayTocke = listVarray(dataset);	
		      
		    // Vse kar je za rešit preden razpošlješ
			  int rows     = arrayTocke.length/randomCentriStart.length + (arrayTocke.length%randomCentriStart.length == 0 ? 1 : 2);
			  int columns  = randomCentriStart.length;
			  int rowCount = 0;

			  TockaXY[][] clusterji  = new TockaXY[rows][columns];
			  clusterji[rowCount++] = randomCentriStart;
			  for(int i=0;i < arrayTocke.length; i += columns){
			        clusterji[rowCount++] = Arrays.copyOfRange(arrayTocke, i, Math.min(arrayTocke.length,i+columns));
			  } 
			  System.out.println("Začetni clusterji: ");
			  Arrays.stream(clusterji).forEach(row->{System.out.println(Arrays.toString(row));});
		    	
			  // Število vseh elementov
		      int sizeArray = 0;
		      
			// Razdelitev podatkov clusterjem
			  System.out.println("ničta vrstica: ");
			  for(int j = 0; j < rows; j++) {
				System.out.println(clusterji[j][0]);   
				sizeArray++;
			  }			  
			  System.out.println("št elemntov: " + sizeArray);
			  
//				System.out.println("Prva vrstica");
//		    for(int j = 0; j < rows; j++) {
//		    	   
//		    	sendBuffer[0]=clusterji[j][0];
//	    		sendBuffer[1]=clusterji[j][1];
//	    		sendBuffer[2]=clusterji[j][2];
//	    		sendBuffer[3]=clusterji[j][3];
//
//	    }
		/* (array z številom vseh elementov, index prvega eleme
	    	
	         
	    nta sendBuffer, število elementov na proces, tip vsakega elementa, kdo dobi poslane elemente (id), 
	    index prvega elementa reciveBufferja, število elementov na proces za dobit, tip vsakega elementa, ki ga dobi vsak proces, id procesa ki pošilja )
	    */
	    }
	    
	    int stElNaProces = 1; // število elemntov na proces * TODO = vsakemu en cluster za računat *
	    TockaXY[] sendBuffer = null; //
	    //sendBuffer = new int [stElNaProces * size]; // [(število elementov na proces) * (število procesov)]
	    //sendBuffer = new int [size]; // tukaj mora v sak proces imet en buffer
	    
	    TockaXY reciveBuffer[] = new TockaXY [stElNaProces]; // [število elemntov na proces]
	    //int New_reciveBuffer [] = new int [stElNaProces * size]; // to je za root v gather metod
	    
		MPI.COMM_WORLD.Scatter(sendBuffer, 0, stElNaProces, MPI.FLOAT, reciveBuffer, 0, stElNaProces, MPI.FLOAT, root);
		
		// Kar delajo vsi ostali razen root
		if(id != root) {
			

			
			
			List<TockaXY> rcsList = new ArrayList<TockaXY>(Arrays.asList(randomCentriStart));
			List<TockaXY> tockeList = new ArrayList<TockaXY>(Arrays.asList(arrayTocke));
			
		    
		    
		    kmeans2(rcsList, tockeList, k);

			
			
			//TODO na koncu v ARRAY TO LIST ZA VSAKEGA
			
			// reciveBuffer določimo vrednosti. Za vsak id procesa določimo vrednosti, ki se mu jih dodeli. če je stElNaProces=4, potem dobim 4 krat dobim 0, 4 krat dobim 1,..., 4 krat dobim 3
//			for(int i=0; i<stElNaProces;i++) {
//				reciveBuffer[i] = id;
//			}
			
		
		}
		
		// (array kjer so vsi podatki in se jih pošlje v New_reciveBuffer,_,_,_, New_reciveBuffer je tisti ki ga root potegne, _,_,_,_ )
		MPI.COMM_WORLD.Gather(reciveBuffer, 0, stElNaProces, MPI.FLOAT, reciveBuffer, 0, stElNaProces, MPI.FLOAT, root);
		
		// sprintat rezultat
		if(id==root) {
//			for(int i=0; i< (stElNaProces * size); i++) {
//				System.out.println(New_reciveBuffer[i] + " ");
//			}
//			
//			
//		}
		
		MPI.Finalize();

	}
	
	}
}


/* TODO
* glavni program kliče Porazdeljeno.java in posreduje args v klicu (v main args spakiraš v String[] parametri; in tukaj kličeš mein samo da args = paramtri)
* 

*/