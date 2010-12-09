import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class AlgoLourdFlatClusterization extends AlgoLourd {

	ArrayList<DataCluster> clusters;
	
	public AlgoLourdFlatClusterization(ArrayList<DataCluster> clusters)
	{
		this.clusters=clusters;
		try {
			Interprete.writeClusters(clusters);
		} catch (ExceptionRecoNotValid e) {
			System.out.println("Fail sur l'écriture");
			e.printStackTrace();
		}
	}
	
	public AlgoLourdFlatClusterization(int nbclusters, ArrayList<DataVector> vecteurs) throws Exception{
		clusters = new ArrayList<DataCluster>();
		for (int i=0; i<nbclusters; i++){
			if (vecteurs.isEmpty()) throw new Exception("WTF ?!");
			DataCluster cluster = new DataCluster();
			cluster.add(vecteurs.get(0));
			clusters.add(cluster);
			cluster.updateCentroid();
			vecteurs.remove(0);
		}
		clusters.get(0).addAll(vecteurs);
		Interprete.writeClusters(clusters);
	}
	
	@Override
	public void maj() throws Exception {
		if (clusters.size()<1) {
			throw new Exception("cest stupide de demander moins de 1 cluster");
		}
		ArrayList<DataVector> vectors = new ArrayList<DataVector>();
		double lastError= Double.MAX_VALUE;
		double currentError = Double.MAX_VALUE /2 ; 
		for (DataCluster c : clusters) {
			vectors.addAll(c);
		}
		while ((  lastError - currentError) / lastError > 0.01 ) { //tant que ca bouge on continue la manoeuvre
			lastError = currentError;
			currentError = 0;
			for (DataCluster c : clusters) {
				c.clear(); // on vide le cluster pour pouvoir y ajouter ensuite ses nouveaux membres
			}
			for (DataVector vect : vectors) {
				DataCluster bestCandidate= null;
				double bestDistance = Double.MAX_VALUE;
				for (DataCluster candidate : clusters) { //on cherche la centroid la plus proche
					double dist = squaredDistance(candidate.getCentroid(), vect);
					if (dist< bestDistance) { //si on a trouvé une centroid plus proche on maj
						bestCandidate = candidate;
						bestDistance = dist;
					}
				}
				bestCandidate.add(vect); //on ajoute le vecteur dans son nouveau cluster
				currentError += bestDistance; //attention si on utilise plus la methode squaredDistance pour calculer cette valeur il faut faire un pow !!
			}
			for (DataCluster c : clusters) {
				c.updateCentroid();
			}
			
		}
		Interprete.writeClusters(clusters);
	}

	


//	private DataVector centroid(DataCluster c) {
//		c.updateCentroid();
//		return  c.getCentroid();
//	}
	
	
	static public double squaredDistance(DataVector v1, DataVector v2) {
		HashSet<String> union = new HashSet<String>();
		union.addAll(v1.keySet());
		union.addAll(v2.keySet());// on a calculé l'union des clés sur lesquelles on calcule la distance
		double dist=0;
		for (String key : union){
			dist += Math.pow((v1.getOrZero(key)- v2.getOrZero(key)), 2); // on prend la diférence ( les valeurs dont les clés sont non contenues dans un des deux vecteurs sont renvoyées à zéro
		}
		return dist;
	}
	
	static public double distance(DataVector v1, DataVector v2) {
		return Math.sqrt(squaredDistance(v1, v2));
	}
	
	public static double distance2(DataVector v1, DataVector v2) { //on utilise un coefficient jaccard pour pondérer la distance habituelle
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
