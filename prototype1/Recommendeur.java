import java.util.List;


public class Recommendeur {
 
	public Recommendation recommendMeSomeThing(Request req) throws ExceptionRecoNotValid {
		DispatcherAlgoLeger dispatch = new DispatcherAlgoLeger();
		Aggregateur aggreg           = new Aggregateur();
		Verificateur verificateur    = new Verificateur();
		
		List<Recommendation> recos;
		try {
			recos = dispatch.dispatches(req);
			Recommendation reco  =  aggreg.merges(recos);
			if (verificateur.verifies(reco)) {
				return reco;
			} else {
				throw new ExceptionRecoNotValid();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ExceptionRecoNotValid();
		} //on fait du pull des données
		
	}

}
