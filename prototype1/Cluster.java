import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class Cluster extends ArrayList<DataVector> implements Data  {

	DataVector centroid = new DataVector();
	Interprete interprete;
	
	
	public DataVector getCentroid() {
		return centroid;
	}

	
	public void write() {
	//	interprete.write("clusters","centroid", centroid);
	// interprete.write la liste de tous les memebres du cluster
	}

	
	
	public void initialize(Request r) {
		List<String> t =interprete.read(r);
		//this.centroid =t.get(0);
		
	}
	
	//TODO ajouter une fonction de maj de la centroid si on ajoute/supprime un seul point pour eviter de tout recalculer
	
	public DataVector getRandomElement() {
		//get an element contained in the cluster
		
		int random = (int) Math.floor( Math.random() * this.size()) ; //entre 0 et size()-1
		
		return get(random);
	}
	

	
	public void updateCentroid() {
		this.centroid.clear();
		Hashtable<String, Integer> counters = new Hashtable<String,Integer>(); 
		for (DataVector vect : this) { //on parcourt tous les utilsiateurs contenus dans le cluster
			for (String key : vect.keySet()) { // on parcourt ensuite toutes les catégories/dimensions de chacun des utlisateurs
				if (!this.centroid.containsKey(key)) { // on créé ce quil faut
					this.centroid.put(key, (float)0);
					counters.put(key, 0);
				}
				this.centroid.put(key, vect.get(key)+ this.centroid.get(key));
				counters.put(key, counters.get(key)+1);
			}
		}
		for (String key : this.centroid.keySet()) { // on finit par diviser par le bon nombre  d'utilisateurs pour chaque catégorie
			this.centroid.put(key, this.centroid.get(key)/ counters.get(key));
		}
	}
}