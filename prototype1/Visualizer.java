import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;


public class Visualizer {
	static Random generator = new Random();
	private class Position {
		public Double x;
		public Double y;
		
		public Position(Double x, Double y) {
			this.x=x;
			this.y = y;
		}
		public Position() {
			this.x = generator.nextDouble();
			this.y = generator.nextDouble();
		}
		
	}
	
	//static Double distance(DataVector v1, DataVector v2);
	
	public void visualizerCluster(Cluster c) {
		//travaille sur un cluster et determine une representation possible
		Hashtable<DataVector, Position> positions = new Hashtable<DataVector, Visualizer.Position>();
		for(DataVector v : c) {
			positions.put(v, new Position());
		}
		Hashtable<DataVector, Hashtable<DataVector, Double>> distances = new Hashtable<DataVector, Hashtable<DataVector,Double>>();
		for(DataVector v1 :c) {
			for(DataVector v2 : c) {
				boolean bv1=  distances.containsKey(v1);
				boolean bv2 = distances.containsKey(v2);
				if (!bv1) distances.put(v1, new Hashtable<DataVector, Double>());
				if (!bv2) distances.put(v2, new Hashtable<DataVector, Double>());
				//Double d= distance(v1,v2)
				//if (!bv1) distances.get(v2).put(v1, value)
				}
			}
		}
	
	
	
	public void visualizer(ArrayList<Cluster> clusters) {
		//travaille sur un esemble de clsuter et permet de les placer par rapport aux autres
		//en pratique : determine une visualisation pour chacun des clusters, trouve la place prose par cette visu
		//puis positionne les clusters les uns par rapport aux autres et affiche le tout
		Hashtable<DataVector, Position> positions = new Hashtable<DataVector, Visualizer.Position>();
		
	}
}
