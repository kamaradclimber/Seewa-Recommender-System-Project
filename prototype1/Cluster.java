import java.util.List;


public class Cluster extends Data {

	String center;
	
	public void write() {
		interprete.write("clusters","center", center);
	}

	
	
	public void initialize(Request r) {
		List<String> t =interprete.read(r);
		this.center =t.get(0);
		
	}
}