import java.util.Hashtable;




@SuppressWarnings("serial")
public class DataVector extends Hashtable<String, Float> implements Data  {
	
	private int arrayId = 0;
	private Integer mongoID;
//	public DataVector() {
//		super();
//		//constructeur vide
//	}
	
	private Integer userId;
	
	public DataVector(int id, Integer mongoID) {
		super();
		this.arrayId = id;
		this.mongoID = userId; // ceci correspond a l'id de l'utilisateur auquel le data est eventuellement lié
	}
	
	public DataVector(boolean vectorThatMatters) {
		//this constructor is meant to build vectors that doesnt represent anything (not a user for instance) : a centroid is a good example 
		super();
		if (vectorThatMatters) System.out.println("bon faut appeler avec les bons arguments quand meme : on appelle ce constructeur que si le vecteur créé ne represente rien cf definition du constructeur");
		this.arrayId = 0;
		this.mongoID = null;
	}
	
	public int getArrayId() {
		return this.arrayId;
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
	public Integer getMongoId() {
		return this.mongoID;
	}


//	public DataVector clone() {
//		return this.clone();
//	}
	
}
