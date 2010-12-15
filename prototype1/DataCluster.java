import java.util.ArrayList;
import java.util.Hashtable;

import org.bson.types.ObjectId;


public class DataCluster extends ArrayList<DataVector> implements Data  {

	/**
	 * le truc qui suit permet d'enlever un warning génant : TOUNDERSTAND 
	 */
	private static final long serialVersionUID = 1L;
	private static int idCount;//variable permettant d'initialiser les id : verifie que tt les id sont différents.
	
	static { 
		idCount = 0;
	}
	
	DataVector centroid = new DataVector(false);
	Interprete interprete;
	private int id ;
	private ObjectId mongoID;
	
	public Integer getArrayId() { // l'id qui sert dans les tableaux
		return this.id;
	}
	
	public int hashCode() {
		//test pour essayer d'accélerer les requtees dans les hastables
		//est-ce encore utile ? FIXME
		return (int) this.id;
	}
	
	public DataCluster() {
		super();
		this.id=0;
//		id = idCount;
//		idCount++;
		System.out.println("Je suis un cluster crée à partir de rien, es-tu sur de vouloir faire ca ?");
	}
	
	public DataCluster(int id, DataVector centroid, ArrayList<DataVector> UTRs, ObjectId mongoID) {
		super();
		this.mongoID = mongoID;
		this.addAll(UTRs);
		this.centroid = centroid;
		this.id =id;
//		if (id!=0) {
//			this.id = id;
//			idCount = Math.max(idCount, id+1);
//		}else {
//			this.id=idCount;
//			idCount++;
//		}
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
		DataVector newCentroid = new DataVector(false);
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

	@Override
	public ObjectId getMongoId() { // l'id de mongo dans la table cluster
		return this.mongoID;
	}

	public void setId(int i) { //impose l'id au cluster (pour bien le mettre dans les tableaux
		if (i< idCount )
			System.out.println("tu es sur de vouloir specifier l'id ("+i + idCount +" ) ? Un autre Cluster a probablement déja cet id! Après tout tu dois savoir ce que tu fais. Modif effectuée.");

		this.id = i;
		idCount = Math.max(id+1,idCount);		
	}
}