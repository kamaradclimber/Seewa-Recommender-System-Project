import java.util.List;


public class Recommendeur {

	public Recommendation recommendMeSomeThing(Request req) throws ExceptionRecoNotValid {
		DispatcherAlgoLeger dispatch = new DispatcherAlgoLeger();
		Aggregateur aggreg           = new Aggregateur();
		Verificateur verificateur    = new Verificateur();
		
		List<Recommendation> recos   = dispatch.dispatches(req); //on fait du pull des données
		Recommendation reco  =  aggreg.merges(recos);
		if (verificateur.verifies(reco)) {
			return reco;
		} else {
			throw new ExceptionRecoNotValid();
		}
	}

}
