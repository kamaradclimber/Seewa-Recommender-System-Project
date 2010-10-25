import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class Cluster extends ArrayList<DataVector> implements Data  {

	DataVector centroid;
	Interprete interprete;
	
	
	public DataVector getCentroid() {
		return centroid;
	}

	
	public void write() {
		interprete.write("clusters","centroid", center);
	}

	
	
	public void initialize(Request r) {
		List<String> t =interprete.read(r);
		this.center =t.get(0);
		
	}
	
	//TODO ajouter une fonction de maj de la centroid si on ajoute/supprime un seul point pour eviter de tout recalculer
	
	public void updateCentroid() {
		this.centroid.clear();
		Hashtable<String, Integer> counters = new Hashtable<String,Integer>(); 
		for (DataVector vect : this) { //on parcourt tous les utilsiateurs contenus dans le cluster
			for (String key : vect.keySet()) { // on parcourt ensuite toutes les cat�gories/dimensions de chacun des utlisateurs
				if (!this.centroid.containsKey(key)) { // on cr�� ce quil faut
					this.centroid.put(key, (float)0);
					counters.put(key, 0);
				}
				this.centroid.put(key, vect.get(key)+ this.centroid.get(key));
				counters.put(key, counters.get(key)+1);
			}
		}
		for (String key : this.centroid.keySet()) { // on finit par diviser par le bon nombre  d'utilisateurs pour chaque cat�gorie
			this.centroid.put(key, this.centroid.get(key)/ counters.get(key));
		}
	}
}