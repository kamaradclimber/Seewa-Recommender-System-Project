import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;


public class DispatcherAlgoLeger {

private List<AlgoLeger> algos; //il faudrait initialiser cette liste lors de la cr�tion pour utiliser les algos disponibles dans le "dossier" qui les contient
private Hashtable<AlgoLeger, Request> algos_to_do; // permet de stocker la liste des algos dans lesquels on vq dispatcher des requetes


	
public DispatcherAlgoLeger() { // TODO : pour le moment je mets la liste des algos utilis�s en dur mais il faudrait initialiser cette liste lors de la cr�tion pour utiliser les algos disponibles dans le "dossier" qui les contient
	this.algos = new ArrayList<AlgoLeger>();
	this.algos.add(new AlgoLegerUserCluster());
}

	@SuppressWarnings("unused")

	public List<Recommendation> dispatches(Request req) {
		//analyse de la requete
		//....[code]
		

		
		try {
			if (req.getTypeOfRequest()=="USER") {
				Request reqBis = new Request(req.get().substring(5));
				algos_to_do.put(algos.get(0), reqBis);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//on execute tous les algos avec la requete qui convient et on rassemble les recommendations 
		List<Recommendation> recos = new ArrayList<Recommendation>();
		for(AlgoLeger algo : algos) {
			recos.add(algo.answers(algos_to_do.get(algo)));

		//pseudo code de ce quil faut ecrire :
//		parcourir la liste des algos disponibles 
//		des quon trouve celui qui correspond a la bonne requete
//		on l'utilise pour faire l'appel
			
		//comment� par Florent: C'est la m�me chose qu'au dessus?
		/*recos = new ArrayList<Recommendation>();
		
		for ( algo : algos) {
			Request reqbis = req ;//TODO : pour le moment c'est une copie mais il faudrait �purer la requete initiale pour qu'elle ne contienne que ce qu'il faut
			recos.add(algo.answers(req));
		}*/ 
		}
		return recos;
		}
}

