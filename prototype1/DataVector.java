import java.util.Hashtable;




@SuppressWarnings("serial")
public class DataVector extends Hashtable<String, Float> implements Data  {

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


	@Override
	public void initialize(Request r) {
		// TODO Auto-generated method stub
		
	}

//	public DataVector clone() {
//		return this.clone();
//	}
	
}
