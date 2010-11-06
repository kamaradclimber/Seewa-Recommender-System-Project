import java.util.ArrayList;
import java.util.List;


public class Interprete {

	public void write(String table, String column, String data) {
		//UPDATE table SET value=data WHERE name=column
	}
	
	public List<String> read(Request r) {
		//SELECT ....
		return new ArrayList<String>();
	}

	public ArrayList<Cluster> readclusters(Request request) {
		// TODO aller cherche la liste des clusters
		return null;
	}

	public DataVector readUcr(String username) {
		// TODO aller chercher l'UCR d'un utilisateur
		return null;
	}
}
