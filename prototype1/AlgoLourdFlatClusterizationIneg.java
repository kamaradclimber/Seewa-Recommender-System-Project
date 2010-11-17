import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;


public class AlgoLourdFlatClusterizationIneg extends AlgoLourd {

	ArrayList<Cluster> clusters;
	public int nbClusters;
	private Hashtable<DataVector, Integer> whosMyCluster = new Hashtable<DataVector, Integer>();
	public AlgoLourdFlatClusterizationIneg(int nbClusters, ArrayList<DataVector> vecteurs) throws Exception{
		clusters = new ArrayList<Cluster>();
		this.nbClusters = nbClusters;
		for (int i=0; i<nbClusters; i++){
			if (vecteurs.isEmpty()) throw new Exception("WTF ?!");
			Cluster cluster = new Cluster(i);
			cluster.add(vecteurs.get(0));
			clusters.add(cluster);
			cluster.updateCentroid();
			vecteurs.remove(0);
		}
		clusters.get(0).addAll(vecteurs);
		for(Cluster c : clusters) { // pour savoir où sont les vecteurs
			for(DataVector vect :c) {
				whosMyCluster.put(vect, c.getId());
			}
		}
	}
	
	@Override
	public void maj() throws Exception {
		if (clusters.size()<1) {
			throw new Exception("cest stupide de demander moins de 1 cluster");
		}
		
		//d'apres le papier d'Elkan
		Double[][] distanceInterCentroid = new Double[nbClusters][nbClusters];
		Hashtable<DataVector, Double> upperBound= new Hashtable<DataVector, Double>();
		Hashtable<DataVector, Hashtable<Integer, Double>> lowerBound = new Hashtable<DataVector, Hashtable<Integer, Double>>();
		Hashtable<Integer, Double> s = new Hashtable<Integer, Double>();
		Hashtable<DataVector, Boolean> r= new Hashtable<DataVector, Boolean>();
		
		ArrayList<DataVector> vectors = new ArrayList<DataVector>();
		double lastError= Double.MAX_VALUE;
		double currentError = Double.MAX_VALUE /2 ; 
		for (Cluster c : clusters) {
			vectors.addAll(c);
		}
		
		//initialisation des lowerBound et upperBound
		for(DataVector x : vectors) {
			r.put(x, false);
			Double minDistance = Double.MAX_VALUE;
			lowerBound.put(x,new Hashtable<Integer, Double>());
			for(Cluster c : clusters) {
				lowerBound.get(x).put(c.getId(), new Double(0));
				Double candidateDistance = distance(x,c.getCentroid());
				lowerBound.get(x).put(c.getId(), candidateDistance);
				if(candidateDistance < minDistance) {
					minDistance = candidateDistance;
				}
			}
			upperBound.put(x, minDistance);
		}

		while ((  lastError - currentError) / lastError > 0.01 ) { //tant que ca bouge on continue la manoeuvre
			//maj des distances inter Centroid
			for(int i=0;i<this.nbClusters;i++) {
				for(int j=i;j<this.nbClusters;j++) {
					distanceInterCentroid[i][j] =  distance(this.clusters.get(i).getCentroid(), this.clusters.get(j).getCentroid());
					distanceInterCentroid[j][i] = distanceInterCentroid[i][j];
				}
			}
			//maj de s
			for(int i=0;i<this.nbClusters;i++) {
				Double minDistance = Double.MAX_VALUE;
				for(int j=0;j<this.nbClusters;j++) {
							if (distanceInterCentroid[i][j] < minDistance) {
								minDistance = distanceInterCentroid[i][j];
							}
				}
				s.put(i, minDistance);
			}
			
			lastError = currentError;
			currentError = 0;
			for (Cluster c : clusters) {
				c.clear(); // on vide le cluster pour pouvoir y ajouter ensuite ses nouveaux membres
			}
			for (DataVector vect : vectors) {
				if (upperBound.get(vect) <= s.get(whosMyCluster.get(vect))) {
					continue;
				}
				Cluster bestCandidate = clusters.get(whosMyCluster.get(vect));
				for(Cluster candidate : clusters) {
					if(candidate == clusters.get(whosMyCluster.get(vect))) {
						continue;
					}
					if (upperBound.get(vect) > lowerBound.get(vect).get(candidate.getId()) && upperBound.get(vect) > 1/2 * distanceInterCentroid[whosMyCluster.get(vect)][candidate.getId()]) {
						Double dxcx = new Double(0);
						if(r.get(vect)) {
							dxcx = distance(vect,clusters.get(whosMyCluster.get(vect)).getCentroid());
							lowerBound.get(vect).put(clusters.get(whosMyCluster.get(vect)).getId(), dxcx);
							r.put(vect,false);
						} else {
							dxcx = upperBound.get(vect);
						}
						if(dxcx > lowerBound.get(vect).get(candidate.getId()) || dxcx > 1/2 * distanceInterCentroid[whosMyCluster.get(vect)][candidate.getId()]) {
							Double dxc = distance(vect, candidate.getCentroid());
							lowerBound.get(vect).put(candidate.getId(), dxc);
							if(dxc < dxcx) {
								bestCandidate = candidate;
							}
						}	
					}
				}
				bestCandidate.add(vect);
				whosMyCluster.put(vect, bestCandidate.getId());
			}
			Hashtable<Integer, DataVector> lastCentroid= new Hashtable<Integer, DataVector>();
			for (Cluster c : clusters) {
				lastCentroid.put(c.getId(),(DataVector) c.getCentroid().clone());
				
				c.updateCentroid();
			}
			//maj des lowerBound et upperBound
			for(DataVector x : vectors) {
				for (Cluster c : clusters) {
					Double deplacement = distance(lastCentroid.get(c.getId()),c.getCentroid() );
					lowerBound.get(x).put(c.getId(),Math.max(0,lowerBound.get(x).get(c.getId()) - deplacement ));
					lowerBound.get(x).put(c.getId(),0.0);
					if(c.getId() == whosMyCluster.get(x)) {
						upperBound.put(x, upperBound.get(x) + deplacement );
						r.put(x, true);
					}
				}
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
	
	
	static private double squaredDistance(DataVector v1, DataVector v2) {
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
