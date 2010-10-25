import java.util.ArrayList;
import java.util.Set;


public class FlatClusterization extends AlgoLourd {

	ArrayList<Cluster> clusters= new ArrayList<Cluster>();
	
	@Override
	public void maj() throws Exception {
		if (clusters.size()<1) {
			throw new Exception("cest stupide de demander moins de 1 cluster");
		}
		ArrayList<DataVector> vectors = new ArrayList<DataVector>();
		double lastError= Double.MAX_VALUE;
		double currentError = Double.MAX_VALUE /2 ; 
		while ((  lastError - currentError) / lastError > 0.1 ) { //tant que ca bouge on continue la manoeuvre
			lastError = currentError;
			currentError = 0;
			for (Cluster c : clusters) {
				vectors.addAll(c);
				c.clear(); // on vide le cluster pour pouvoir y ajouter ensuite ses nouveaux membres
			}
			for (DataVector vect : vectors) {
				Cluster bestCandidate= null;
				double bestDistance = Double.MAX_VALUE;
				for (Cluster candidate : clusters) { //on cherche la centroid la plus proche
					double dist = squaredDistance(candidate.getCentroid(), vect);
					if (dist< bestDistance) { //si on a trouvé une centroid plus proche on maj
						bestCandidate = candidate;
						bestDistance = dist;
					}
				}
				bestCandidate.add(vect); //on ajoute le vecteur dans son nouveau cluster
				currentError += bestDistance; //attention si on utilise plus la methode squaredDistance pour calculer cette valeur il faut faire un pow !!
			}
			for (Cluster c : clusters) {
				c.updateCentroid();
			}
			
		}
	}


	
	@Override
	Data input() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void output(Data d) {
		// TODO Auto-generated method stub

	}

	private DataVector centroid(Cluster c) {
		c.updateCentroid();
		return  c.getCentroid();
	}
	
	
	private double squaredDistance(DataVector v1, DataVector v2) {
		Set<String> union = v1.keySet();
		union.addAll(v2.keySet());// on a calculé l'union des clés sur lesquelles on calcule la distance
		double dist=0;
		for (String key : union){
			dist += Math.pow((v1.getOrZero(key)- v2.getOrZero(key)), 2); // on prend la diférence ( les valeurs dont les clés sont non contenues dans un des deux vecteurs sont renvoyées à zéro
		}
		return dist;
	}
	
	private double distance(DataVector v1, DataVector v2) {
		return Math.sqrt(this.squaredDistance(v1, v2));
	}
	
	private double distance2(DataVector v1, DataVector v2) { //on utilise un coefficient jaccard pour pondérer la distance habituelle
		//on calcule les unions/intersections
		Set<String> union = v1.keySet();
		Set<String> intersection = v1.keySet();
		union.addAll(v2.keySet());
		intersection.retainAll(v2.keySet());
		
		double jaccard = intersection.size() / union.size()  ; //calcul de la distance de Jaccard
		
		double dist=0;
		for (String key : intersection){  // on calcule la distance uniquement sur les clés communes
			dist += Math.pow((v1.get(key)- v2.get(key)), 2); 
		}
		return (dist / (0.00001 + jaccard)); // on malusifie les jaccards un peu faiblards
	}
	
}
