import java.util.Hashtable;




@SuppressWarnings("serial")
public class DataVector extends Hashtable<String, Float> implements Data  {
	
	private int id = 0;
	
//	public DataVector() {
//		super();
//		//constructeur vide
//	}
	
	private Integer userId;
	
	public DataVector(int id, Integer userId) {
		super();
		this.id = id;
		this.userId = userId; // ceci correspond a l'id de l'utilisateur auquel le data est eventuellement lié
	}
	
	public DataVector(boolean vectorThatMatters) {
		//this constructor is means to build vectors that doesnt represent anything (not a user for instance) : a centroid is a good example 
		super();
		if (vectorThatMatters) System.out.println("bon faut appeler avec les bons arguments quand meme : on appelle ce constructeur que si le vecteur créé ne represente rien cf definition du constructeur");
		this.id = 0;
		this.userId = null;
	}
	
	public int getId() {
		return this.id;
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

	
	@Override
	public void write() {
		// THINK

	}

	
	public float getOrZero(String key) { //renvoie la valeur si la clé existe et zéro sinon
		if (!this.containsKey(key)) {
			return 0;
		}
		return this.get(key);
	}

	public Object getUserId() {
		return this.userId;
	}


//	public DataVector clone() {
//		return this.clone();
//	}
	
}
