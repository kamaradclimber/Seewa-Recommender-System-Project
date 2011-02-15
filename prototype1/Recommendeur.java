import java.util.List;


public class Recommendeur {
	
	static private DispatcherAlgoLeger dispatch = new DispatcherAlgoLeger();
	static private Aggregateur aggreg         	= new Aggregateur();
	static private Verificateur verificateur	= new Verificateur();
	
	
	public void addAlgo(String algo)
	{
		try{
			Class<AlgoLeger> c = (Class<AlgoLeger>) Class.forName(algo);
			AlgoLeger algoInstance= c.newInstance();

			dispatch.addAlgo(algoInstance);
			aggreg.addAlgo(algoInstance);
			
		}catch (Exception e){
			System.out.println("Erreur lors de l'ajout d'un nouvel algorithme");
			e.printStackTrace();
		}
	}
 
	public Recommendation recommendMeSomeThing(Request req) throws ExceptionRecoNotValid {
		//dispatch des requetes aux algos, puis traitement, merge et vérification
		List<Recommendation> recos;
		recos = dispatch.dispatch(req);
		Recommendation reco  =  aggreg.merges(recos);
		verificateur.verifies(reco);
		return reco;
		
	
	}

}
