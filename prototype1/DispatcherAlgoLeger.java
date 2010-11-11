import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;


public class DispatcherAlgoLeger {
<<<<<<< HEAD
private List<AlgoLeger> algos; //il faudrait initialiser cette liste lors de la crátion pour utiliser les algos disponibles dans le "dossier" qui les contient
private Hashtable<AlgoLeger, Request> algos_to_do; // permet de stocker la liste des algos dans lesquels on vq dispatcher des requetes

=======
private List<AlgoLeger> algos;
	
public DispatcherAlgoLeger() { // TODO : pour le moment je mets la liste des algos utilsiés en dur mais il faudrait initialiser cette liste lors de la crátion pour utiliser les algos disponibles dans le "dossier" qui les contient
	this.algos = new ArrayList<AlgoLeger>();
	this.algos.add(new AlgoLegerCluster());
}

	@SuppressWarnings("unused")
>>>>>>> 3cb1ac7da2ab62e6c9c6f27a5049602f24d84637
	public List<Recommendation> dispatches(Request req) {
		//analyse de la requete
		//....[code]
		
<<<<<<< HEAD
		if (req.getTypeOfRequest()=="USER") {
			Request reqBis = new Request(req.get().substring(5));
			algos_to_do.put(new AlgoLegerUserCluster(), reqBis);
		}
		
		//on execute tous les algos avec la requete qui convient et on rassemble les recommendations 
		List<Recommendation> recos = new ArrayList<Recommendation>();
		for(AlgoLeger algo : algos_to_do.keySet()) {
			recos.add(algo.answers(algos_to_do.get(algo)));
=======
		//pseudo code de ce quil faut ecrire :
//		parcourir la liste des algos disponibles 
//		des quon trouve celui qui correspond a la bonne requete
//		on lutiliser pour faire l'appel
		List<Recommendation> recos = new ArrayList<Recommendation>();
		
		for (AlgoLeger algo : algos) {
			Request reqbis = req ;//TODO : pour le moment c'est une copie mais il faudrait épurer la requete initiale pour qu'elle ne contienne que ce qu'il faut
			recos.add(algo.answers(req));
>>>>>>> 3cb1ac7da2ab62e6c9c6f27a5049602f24d84637
		}
		return recos;
	}
}
