package application;

import java.util.List;
import java.util.Map;

import application.Main.TockaXY;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;

public class WebviewLoadListner implements ChangeListener<State> {
	Map<String, TockaXY> centri;
	List<TockaXY> tocke;

	public WebviewLoadListner(Map<String, TockaXY> centri, List<TockaXY> tocke) {
		this.centri = centri;
		this.tocke = tocke;
	}

	@Override
	public void changed(ObservableValue<? extends State> observable, State from, State to) {

		if (to == State.SUCCEEDED) {
//			 for (Map.Entry<String, TockaXY> entry : SampleController.output.entrySet()) { 
//				 System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); }

//			SampleController.output.forEach((key,value) -> System.out.println(key + " = " + value));
			if (tocke != null) {
				for (int i = 0; i < tocke.size(); i++) {
					// kako za vsak i =
					// SampleController.engine.executeScript("addMarker(layer_markers, i,\"kmeans
					// 1\");");
					System.out.println("Draw point " + tocke.get(i).getX());
					SampleController.engine.executeScript("addMarker(layer_markers," + tocke.get(i).getX() + ","
							+ tocke.get(i).getY() + ",\"kmeans 1\");");
 
				}
			}
			if (centri != null) {
				for (TockaXY t : centri.values()) {
					// kako za vsak i =
					// SampleController.engine.executeScript("addMarker(layer_markers, i,\"kmeans
					// 1\");");
					System.out.println("Draw point " + t.getX() + " : " + t.getY());
					SampleController.engine
							.executeScript("addMarker(layer_markers," + t.getX() + "," + t.getY() + ",\"kmeans 1\");");
				}
				SampleController.engine.executeScript("jumpTo(" + 46.641389 + "," + 49.756667 + "," + 8 + ")");
			}

			// TODO preveri če so vse točke v zemljevidu
			//SampleController.engine.executeScript("jumpTo(" + 46.641389 + "," + 49.756667 + "," + 8 + ")");
//			SampleController.output.forEach((key,value) -> System.out.println(key + " = " + value));
			SampleController.engine
					.executeScript("addMarker(layer_markers, " + "46.641389" + ", 49.756667" + ",\"kmeans 1\");");
		}
	}
}
