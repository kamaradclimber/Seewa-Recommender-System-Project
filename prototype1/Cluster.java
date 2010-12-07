import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class DataCluster extends ArrayList<DataVector> implements Data  {

	DataVector centroid = new DataVector(false);
	Interprete interprete;
	private int id = 0;
	
	public int getId() {
		return this.id;
	}
	
	public int hashCode() {
		//test pour essayer d'accélerer les requtees dans les hastables
		return this.id;
	}
	
	public DataCluster() {
		super();
	}
	
	public DataCluster(int id) {
		super();
		this.id = id;
	}
	
	public DataCluster(DataVector centroid, ArrayList<DataVector> UTRs) {
		super();
		this.addAll(UTRs);
		this.centroid = centroid;
		
	}
	
	public DataVector getCentroid() {
		return centroid;
	}

	public boolean equals(Object o) {
		try {
			DataCluster c = (DataCluster)o;
			return this.centroid.equals(c.centroid);
		} catch (ClassCastException ex) {
			return false;
		}
	}
	
	public void write() {
		//TODO faut-il une methode decriture les clusters, THINK : quelle stratégie d'enregistrement des données 
	//	interprete.write("clusters","centroid", centroid);
	// interprete.write la liste de tous les memebres du cluster
	}

	
	
	public void initialize(Request r) {
		List<String> t =interprete.read(r);
		//this.centroid =t.get(0);
		
	}
	
	public void singleUpdateCentroid(DataVector vect, Boolean removed) {
		// this function updates the centroid in case of single add or removed point
		// the bool removed is true if we have just removed the vector, false if have just added it
		//TODO : use this method where it could be useful :-) delete it otherwise
		int nbOfPointsBeforeAction = this.size();
		if (removed) {
			nbOfPointsBeforeAction--;
		} else {
			nbOfPointsBeforeAction++;
		}
		DataVector newCentroid = new DataVector();
		if (removed) {
			for(String theme : this.centroid.keySet()) {
				newCentroid.put(theme, (this.centroid.get(theme)* nbOfPointsBeforeAction - vect.get(theme))/(nbOfPointsBeforeAction -1));
			}
		} else {
			for(String theme : this.centroid.keySet()) {
				newCentroid.put(theme, (this.centroid.get(theme)* nbOfPointsBeforeAction + vect.get(theme))/(nbOfPointsBeforeAction +1));
			}
		}
		this.centroid = newCentroid;
	}
	
	
	public DataVector getRandomElement() {
		//get an element contained in the cluster
		
		int random = (int) Math.floor( Math.random() * this.size()) ; //entre 0 et size()-1
		
		return this.get(random);
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