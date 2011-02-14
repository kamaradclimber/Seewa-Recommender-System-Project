import java.util.List;


public class Recommendeur {
	
	static private DispatcherAlgoLeger dispatch;
	static private Aggregateur aggreg;
	static private Verificateur verificateur;
	
	static{
		dispatch = new DispatcherAlgoLeger();
		aggreg = new Aggregateur();
		verificateur = new Verificateur();
	}
	
	public Recommendeur()
	{
		super();
	}
	
	public void addAlgo(String algo)
	{
		try{
			Class<AlgoLeger> c = (Class<AlgoLeger>) Class.forName(algo);
			AlgoLeger algoInstance= c.newInstance();

			dispatch.addAlgo(algoInstance);
			aggreg.addAlgo(algoInstance);
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
 
	public Recommendation recommendMeSomeThing(Request req) throws ExceptionRecoNotValid {

		List<Recommendation> recos;
		recos = dispatch.dispatch(req);
		System.out.println("bob");
		System.out.flush();
		Recommendation reco  =  aggreg.merges(recos);
		verificateur.verifies(reco);
		return reco;
		
	
	}

}
