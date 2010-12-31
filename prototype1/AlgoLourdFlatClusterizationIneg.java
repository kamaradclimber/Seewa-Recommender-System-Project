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
	
	public AlgoLourdFlatClusterizationIneg(ArrayList<DataVector> newVectors) throws ExceptionRecoNotValid{
		System.out.print("Lecture des clusters dans la base de donn�es...");
		clusters = Interprete.readClusters();
		
//		for (int i=0; i<clusters.size(); i++) {
//			clusters.get(i).setId(i);
//		}
		
		System.out.print("...[done]\n");
		
		this.nbClusters = clusters.size(); 
		
		for (DataCluster c : clusters) { this.nb_vectors += c.size(); } //on compte ausi les vecteurs deja presents
		this.nb_vectors += newVectors.size();
		
//		for (int i=0; i<nbClusters; i++){
//			if (newVectors.isEmpty() && clusters.get(i).isEmpty()) throw new ExceptionRecoNotValid(ExceptionRecoNotValid.NO_CLUSTER);
//			DataCluster cluster = clusters.get(i);
//			cluster.setId(i); //on impose l'id pour que ca matche bien la position dans le tableau
//			if (cluster.isEmpty()) {
//				cluster.add(newVectors.get(0));
//				newVectors.remove(0);
//			}
//			cluster.updateCentroid(); // cette maj n'est pas forcement utile et un peu lourde je trouve alors qu'on pourrait peut etre la sortir de cette boucle et la faire a la fin FIXME
//		}
//		clusters.get(0).addAll(newVectors); // on met tous les nouveaux vecteurs dans un cluster au pif.
		
		for (int i=0; i<nbClusters; i++){
			if (newVectors.isEmpty() && clusters.get(i).isEmpty()) throw new ExceptionRecoNotValid(ExceptionRecoNotValid.NO_CLUSTER); //pas assez de vecteurs pour remplir les clusters
			DataCluster cluster = clusters.get(i);
			cluster.setId(i); //on impose l'id pour que ca matche bien la position dans le tableau
		}
		int cluster_to_be_served=0; // on va distribuer les nouveauix vecteurs commes des cartes a jouer
		for(DataVector vect : newVectors) {
			clusters.get(cluster_to_be_served).add(vect);
			cluster_to_be_served = (cluster_to_be_served + 1) % nbClusters;
			
		}
		
		for(DataCluster c : clusters) { // pour savoir o� sont les vecteurs
			for(DataVector vect :c) {
				
				whosMyCluster.put(vect, c.getArrayId());
			}
		}
	}
	
	@Override
	public void maj() throws ExceptionRecoNotValid {
		if (clusters.size()<1) {
			throw new ExceptionRecoNotValid(ExceptionRecoNotValid.NO_CLUSTER);
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
		int id=0;
		for (DataCluster c : clusters) {
			for(DataVector vect : c) {
				vect.setArrayId(id);
				id++;
			}
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

		while ((  lastError - currentError) / lastError > 0.0001 ) { //tant que ca bouge on continue la manoeuvre
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
			ArrayList<DataVector> preserved_vectors = new ArrayList<DataVector>();
			for (DataCluster c : clusters) {
				DataVector preserved_vector  = c.clear_preserve(); // on vide le cluster pour pouvoir y ajouter ensuite ses nouveaux membres // EDIT : on ne supprime pas tous les vecteurs pour éviter de se retrouver avec des clusters co:plètetmetn vides
				if (preserved_vector != null) {
					vectors.remove(preserved_vector); //ici on supprime les vecteurs preservés pour ne pas les rajouter ensuite dans les clusters (eviter le doublons), on les rerajoute juste après
					preserved_vectors.add(preserved_vector);
				}
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
			
			vectors.addAll(preserved_vectors); // voilà on rerajoute les vecteurs pour éviter de géner les autres opérations
			
			
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
		System.out.print("Ecriture des clusters dans la base de donn�es...");
		Interprete.writeClusters(clusters);
		System.out.println("[done]");
	}


	static public double squaredDistance(DataVector v1, DataVector v2) {
		HashSet<String> union = new HashSet<String>();
		union.addAll(v1.keySet());
		union.addAll(v2.keySet());// on a calcul� l'union des cl�s sur lesquelles on calcule la distance
		double dist=0;
		for (String key : union){
			dist += Math.pow((v1.getOrZero(key)- v2.getOrZero(key)), 2); // on prend la dif�rence ( les valeurs dont les cl�s sont non contenues dans un des deux vecteurs sont renvoy�es � z�ro
		}
		return dist;
	}
	
	static public double distance(DataVector v1, DataVector v2) {
		return Math.sqrt(squaredDistance(v1, v2));
	}
	
	private double distance2(DataVector v1, DataVector v2) { //on utilise un coefficient jaccard pour pond�rer la distance habituelle
		//on calcule les unions/intersections
		Set<String> union = v1.keySet();
		Set<String> intersection = v1.keySet();
		union.addAll(v2.keySet());
		intersection.retainAll(v2.keySet());
		
		double jaccard = intersection.size() / union.size()  ; //calcul de la distance de Jaccard
		
		double dist=0;
		for (String key : intersection){  // on calcule la distance uniquement sur les cl�s communes
			dist += Math.pow((v1.get(key)- v2.get(key)), 2); 
		}
		return (dist / (0.00001 + jaccard)); // on malusifie les jaccards un peu faiblards
	}
	
}
