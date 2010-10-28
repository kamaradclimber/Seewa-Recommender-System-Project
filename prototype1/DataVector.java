import java.util.Hashtable;




@SuppressWarnings("serial")
public class DataVector extends Hashtable<String, Float> implements Data  {

	

	
	@Override
	public void write() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(Request r) {
		// TODO Auto-generated method stub

	}
	
	public float getOrZero(String key) { //renvoie la valeur si la clé existe et zéro sinon
		if (!this.containsKey(key)) {
			return 0;
		}
		return this.get(key);
	}

}
