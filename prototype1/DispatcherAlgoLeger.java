import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class DispatcherAlgoLeger {

	private Hashtable<AlgoLeger, Request> algos_to_do; // permet de stocker la liste des algos dans lesquels on vq dispatcher des requetes
	// TODO cette data structure ne nous permet pas d'utiliser deux fois le meme algo pour une requete

	
	public DispatcherAlgoLeger() {
		this.algos_to_do = new Hashtable<AlgoLeger, Request>();
	}
	

	public List<Recommendation> dispatch(Request req) throws ExceptionRecoNotValid, NoRecoHasBeenFound {


		if (req.getTypeOfRequest()==TypeOfRequest.RECOPAGE) {
			Request reqBis = req;
			if(algos_to_do==null || algos_to_do.size()==0)
				{
				System.out.println("On essaye avec un algoLegerBayes");
				algos_to_do.put(AlgoLegerBayes.getAlgo(), reqBis);
				}
			for (AlgoLeger algo : algos_to_do.keySet())
			{
				algos_to_do.put(algo, algo.transform(req));
			}
		}
		
				
		
		//on execute tous les algos avec la requete qui convient et on rassemble les recommendations 
		List<Recommendation> recos = new ArrayList<Recommendation>();
		for(AlgoLeger algo : algos_to_do.keySet()) {
			recos.add(algo.answers(algos_to_do.get(algo)));
		}
		return recos;
		}

	public void addAlgo(AlgoLeger algo) {
		// TODO Auto-generated method stub
		algos_to_do.put(algo, null);
	}
}

