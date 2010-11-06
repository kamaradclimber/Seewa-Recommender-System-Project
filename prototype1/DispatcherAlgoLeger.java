import java.util.ArrayList;
import java.util.List;


public class DispatcherAlgoLeger {
private List<AlgoLeger> algos;
	
public DispatcherAlgoLeger() { // TODO : pour le moment je mets la liste des algos utilsiés en dur mais il faudrait initialiser cette liste lors de la crátion pour utiliser les algos disponibles dans le "dossier" qui les contient
	this.algos = new ArrayList<AlgoLeger>();
	this.algos.add(new AlgoLegerCluster());
}

	@SuppressWarnings("unused")
	public List<Recommendation> dispatches(Request req) {
		//analyse de la requete
		//....[code]
		
		//pseudo code de ce quil faut ecrire :
//		parcourir la liste des algos disponibles 
//		des quon trouve celui qui correspond a la bonne requete
//		on lutiliser pour faire l'appel
		List<Recommendation> recos = new ArrayList<Recommendation>();
		
		for (AlgoLeger algo : algos) {
			Request reqbis = req ;//TODO : pour le moment c'est une copie mais il faudrait épurer la requete initiale pour qu'elle ne contienne que ce qu'il faut
			recos.add(algo.answers(req));
		}
		return recos;
	}
}
