import java.util.Hashtable;

import org.bson.types.ObjectId;




@SuppressWarnings("serial")
public class DataVector extends Hashtable<String, Float> implements Data  {
	
	private int arrayId = 0;
	private ObjectId userID; //on stocke l'id de l'user qui est associé au vector, si on parle dun user sinon il est null
	//DONE: et si on mettait userID, ou juste "Objet user;"? c'est fait (greg)
	
	public DataVector(int id, ObjectId userID) {
		super();
		this.arrayId = id;
		this.userID = userID; // ceci correspond a l'id de l'utilisateur auquel le data est eventuellement lié
	}
	
	public DataVector(boolean vectorThatMatters) {
		//this constructor is meant to build vectors that doesnt represent anything (not a user for instance) : a centroid is a good example 
		super();
		if (vectorThatMatters) System.out.println("bon faut appeler avec les bons arguments quand meme : on appelle ce constructeur que si le vecteur créé ne represente rien cf definition du constructeur");
		this.arrayId = 0;
		this.userID = null;
	}
	
	public int getArrayId() {
		return this.arrayId;
	}
	
	public void setArrayId(int id) {
		this.arrayId = id;
	}
	
	public boolean equals(Object o) {
		try {
			DataVector v = (DataVector) o;
			for(String key : this.keySet()) {
				if (!v.containsKey(key) || v.get(key) != this.get(key))  {
					return false;
				}
			}
			return (v.size()==this.size());
		} catch (ClassCastException ex) {
			return false;
		}
	}



	
	public float getOrZero(String key) { //renvoie la valeur si la clé existe et zéro sinon
		if (!this.containsKey(key)) {
			return 0;
		}
		return this.get(key);
	}


	@Override
	public ObjectId getMongoId() {
		return this.userID;
	}


//	public DataVector clone() {
//		return this.clone();
//	}
	
}
