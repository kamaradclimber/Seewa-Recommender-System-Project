import java.util.List;


public class Recommendeur {
 
	public Recommendation recommendMeSomeThing(Request req) throws ExceptionRecoNotValid {
		DispatcherAlgoLeger dispatch = new DispatcherAlgoLeger();
		Aggregateur aggreg           = new Aggregateur();
		Verificateur verificateur    = new Verificateur();
		
		List<Recommendation> recos;
		recos = dispatch.dispatches(req);
		Recommendation reco  =  aggreg.merges(recos);
		verificateur.verifies(reco);
		return reco;
		
	
	}

}
