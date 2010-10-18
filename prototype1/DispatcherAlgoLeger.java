import java.util.ArrayList;
import java.util.List;


public class DispatcherAlgoLeger {
private List<AlgoLeger> algos; //il faudrait initialiser cette liste lors de la crátion pour utiliser les algos disponibles dans le "dossier" qui les contient
	
	public List<Recommendation> dispatches(Request req) {
		//analyse de la requete
		//....[code]
		
		String algo = ""; //on a donc une nouvelle requete et on sait où (dans quel algo) elle doit aller
		Request reqbis = req ;//pour le moment c'est une copie mais il faudrait épurer la requete initiale pour qu'elle ne contienne que ce qu'il faut
		
		//pseudo code de ce quil faut ecrire :
//		parcourir la liste des algos disponibles 
//		des quon trouve celui qui correspond a la bonne requete
//		on lutiliser pour faire l'appel
		List<Recommendation> recos = new ArrayList<Recommendation>();
		recos.add(algos.get(0).answers(req));
		return recos;
	}
}
