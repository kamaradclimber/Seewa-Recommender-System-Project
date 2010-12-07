import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;


public class AlgoLourdFlatClusterizationIneg extends AlgoLourd {

	ArrayList<DataCluster> clusters;
	public int nbClusters;
	private Hashtable<DataVector, Integer> whosMyCluster = new Hashtable<DataVector, Integer>();
	private int nb_vectors = 0;
	
	public AlgoLourdFlatClusterizationIneg(ArrayList<DataVector> newVectors) throws Exception{
		System.out.print("Lecture des clusters dans la base de données...");
		clusters = Interprete.readClusters(new Request(null));
		System.out.print("...[done]\n");
		
		this.nbClusters = clusters.size();
		
		for (DataCluster c : clusters) { this.nb_vectors += c.size(); } //on compte ausi les vecteurs deja presents
		this.nb_vectors += newVectors.size();
		
		for (int i=0; i<nbClusters; i++){
			if (newVectors.isEmpty() && clusters.get(i).isEmpty()) throw new Exception("WTF ?!");
			DataCluster cluster = clusters.get(i);
			cluster.setId(i); //on impose l'id pour que ca matche bien la position dans le tableau
			if (cluster.isEmpty()) {
				cluster.add(newVectors.get(0));
				newVectors.remove(0);
			}
			cluster.updateCentroid(); // cette maj n'est pas forcement utile et un peu lourde je trouve alors qu'on pourrait peut etre la sortir de cette boucle et la faire a la fin FIXME
		}
		clusters.get(0).addAll(newVectors); // on met tous les nouveaux vecteurs dans un cluster au pif.
		for(DataCluster c : clusters) { // pour savoir où sont les vecteurs
			for(DataVector vect :c) {
				whosMyCluster.put(vect, c.getArrayId());
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
		Double[] upperBound= new Double[nb_vectors]; // remplace la hashtable qui associait DataVector et upperbound
		Double[][] lowerBound = new Double[nb_vectors][nbClusters];
		Double[] s = new Double[nbClusters];
		boolean[] r= new boolean[nb_vectors];
		
		ArrayList<DataVector> vectors = new ArrayList<DataVector>();
		double lastError= Double.MAX_VALUE;
		double currentError = Double.MAX_VALUE /2 ; 
		for (DataCluster c : clusters) {
			vectors.addAll(c);
		}
		
		//initialisation des lowerBound et upperBound
		for(DataVector x : vectors) {
			r[x.getArrayId()] = false;
			Double minDistance = Double.MAX_VALUE;

			for(DataCluster c : clusters) {
				lowerBound[x.getArrayId()][c.getArrayId()] = new Double(0);
				Double candidateDistance = distance(x,c.getCentroid());
				lowerBound[x.getArrayId()][c.getArrayId()] = candidateDistance;
				if(candidateDistance < minDistance) {
					minDistance = candidateDistance;
				}
			}
			upperBound[x.getArrayId()] = minDistance;
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
				s[i]=  minDistance;
			}
			
			lastError = currentError;
			currentError = 0;
			for (DataCluster c : clusters) {
				c.clear(); // on vide le cluster pour pouvoir y ajouter ensuite ses nouveaux membres
			}
			for (DataVector vect : vectors) {
				if (upperBound[vect.getArrayId()] <= s[whosMyCluster.get(vect)]) {
					continue;
				}
				DataCluster bestCandidate = clusters.get(whosMyCluster.get(vect));
				for(DataCluster candidate : clusters) {
					if(candidate == clusters.get(whosMyCluster.get(vect))) {
						continue;
					}
					if (upperBound[vect.getArrayId()] > lowerBound[vect.getArrayId()][candidate.getArrayId()] && upperBound[vect.getArrayId()] > 1/2 * distanceInterCentroid[whosMyCluster.get(vect)][candidate.getArrayId()]) {
						Double dxcx = new Double(0);
						if(r[vect.getArrayId()]) {
							dxcx = distance(vect,clusters.get(whosMyCluster.get(vect)).getCentroid());
							lowerBound[vect.getArrayId()][clusters.get(whosMyCluster.get(vect)).getArrayId()] =  dxcx;
							r[vect.getArrayId()] = false;
						} else {
							dxcx = upperBound[vect.getArrayId()];
						}
						if(dxcx > lowerBound[vect.getArrayId()][candidate.getArrayId()] || dxcx > 1/2 * distanceInterCentroid[whosMyCluster.get(vect)][candidate.getArrayId()]) {
							Double dxc = distance(vect, candidate.getCentroid());
							lowerBound[vect.getArrayId()][candidate.getArrayId()] =  dxc;
							if(dxc < dxcx) {
								bestCandidate = candidate;
							}
						}	
					}
				}
				bestCandidate.add(vect);
				whosMyCluster.put(vect, bestCandidate.getArrayId());
			}
			Hashtable<Integer, DataVector> lastCentroid= new Hashtable<Integer, DataVector>();
			for (DataCluster c : clusters) {
				lastCentroid.put(c.getArrayId(),(DataVector) c.getCentroid().clone());
				
				c.updateCentroid();
			}
			//maj des lowerBound et upperBound
			for(DataVector x : vectors) {
				for (DataCluster c : clusters) {
					Double deplacement = distance(lastCentroid.get(c.getArrayId()),c.getCentroid() );
					lowerBound[x.getArrayId()][c.getArrayId()] = Math.max(0,lowerBound[x.getArrayId()][c.getArrayId()] - deplacement );
					lowerBound[x.getArrayId()][c.getArrayId()] = 0.0;
					if(c.getArrayId() == whosMyCluster.get(x)) {
						upperBound[x.getArrayId()] =  upperBound[x.getArrayId()] + deplacement ;
						r[x.getArrayId()] = true;
					}
				}
			}
			
			
		}
	}

//  quelque chose utilise cette aprtie du code ? cest vraiment DEPRECATED :)	
//	@Override
//	Data input() {
//		Interprete.readClusters(null); // TODO : quelle requete faut il mettre pour reccupérer les bons clusters ? fautil une requete ?
//		return null;
//	}
//
//	@Override
//	void output(Data d) {
//		try {
//			Interprete.writeClusters(clusters);
//		} catch (RecoException e) {
//			System.out.println("Fail sur l'écriture");
//			e.printStackTrace();
//		}
//
//	}

	private DataVector centroid(DataCluster c) {
		c.updateCentroid();
		return  c.getCentroid();
	}
	
	
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
