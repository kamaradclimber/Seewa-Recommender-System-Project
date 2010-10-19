
public abstract class Data {
//le format que je mets chaque fois quón fait transiter des données
// mais on peut tout a fait mettre des types différents...
	
	static Interprete interprete;
	
	abstract public void write();
	abstract public void initialize(Request r);
	

	static public void setInterprete(Interprete inter) {
		interprete = inter;
	}
}

