import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;


public class Visualizer {
	static Random generator = new Random();
	private class Position {
		public Double x;
		public Double y;
		
		public Position(Double x, Double y) {
			this.x = x;
			this.y = y;
		}
		public Position() {
			this.x = 10* generator.nextDouble();
			this.y = 10* generator.nextDouble();
		}
		
		
		
	}
	
	static public Double distance(Position p1, Position p2) { //renvoie la distance euclidienne (on l'utilise car cest celle du plan, sur lequel on projette nos clusters)
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y-p2.y, 2));
	}
	

	public Hashtable<DataVector, Position> visualizerCluster(Cluster c) throws Exception {
		//travaille sur un cluster et determine une representation possible
		Hashtable<DataVector, Position> positions = new Hashtable<DataVector, Visualizer.Position>();
		for(DataVector v : c) {
			positions.put(v, new Position());
		}
		//initialisation de la matrice de distance
		Hashtable<DataVector, Hashtable<DataVector, Double>> distances = new Hashtable<DataVector, Hashtable<DataVector,Double>>();
		for(DataVector v1 :c) {
			for(DataVector v2 : c) {
				boolean bv1=  distances.containsKey(v1);
				boolean bv2 = distances.containsKey(v2);
				if (!bv1) distances.put(v1, new Hashtable<DataVector, Double>());
				if (!bv2) distances.put(v2, new Hashtable<DataVector, Double>());
				Double d= FlatClusterization.distance(v1,v2);
				distances.get(v2).put(v1, d);
				distances.get(v1).put(v2, d);
				}
			}
		
		//boucle pour "équilibrer les forces" d'attraction entre les points
		Double errorTotTot = Double.MAX_VALUE /2;
		Double lastErrorTotTot = Double.MAX_VALUE;
		while (errorTotTot > 0.1 && lastErrorTotTot > errorTotTot) {
			lastErrorTotTot = errorTotTot;
			errorTotTot = new Double(0);
			for (DataVector v1 : c ) {
				Position errorVector = new Position(new Double(0), new Double(0)); //on va (hérésie !) assimiler point et vecteur
				Double error = new Double(0); Double errorTot =new Double(0);
				Position p1 = positions.get(v1);  
				for (DataVector v2 : c) {
					if (v1==v2) continue;
					if (distances.get(v1).get(v2)<0.0001) continue;
					Position p2 = positions.get(v2);
					error = (Visualizer.distance(p1, p2) - distances.get(v1).get(v2)) / distances.get(v1).get(v2);
					errorTot += Math.abs(error);
					Position vector = new Position(p2.x - p1.x , p2.y - p1.y);
					errorVector = new Position(errorVector.x + (error * vector.x), errorVector.y + (error * vector.y));
				}
				positions.put(v1, new Position(p1.x+ (errorVector.x * 0.01),p1.y+(errorVector.y* 0.01)));
				errorTotTot += errorTot;
//				System.out.println("erreur totaltotal : " +errorTotTot);
			}
		}
		System.out.println("erreur totaltotal : " +errorTotTot);
		System.out.println("lastError : "+ lastErrorTotTot);
		for (DataVector v : c) {
			System.out.println(positions.get(v).x +" "+positions.get(v).y);
		}
		return positions;
		}
	
	
	
	public void visualizer(ArrayList<Cluster> clusters) {
		//travaille sur un esemble de clsuter et permet de les placer par rapport aux autres
		//en pratique : determine une visualisation pour chacun des clusters, trouve la place prose par cette visu
		//puis positionne les clusters les uns par rapport aux autres et affiche le tout
		Hashtable<DataVector, Position> positions = new Hashtable<DataVector, Visualizer.Position>();
		
	}
}
