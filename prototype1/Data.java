
public abstract class Data {
//le format que je mets chaque fois qu�n fait transiter des donn�es
// mais on peut tout a fait mettre des types diff�rents...
	
	static Interprete interprete;
	
	abstract public void write();
	abstract public void initialize(Request r);
	

	static public void setInterprete(Interprete inter) {
		interprete = inter;
	}
}

