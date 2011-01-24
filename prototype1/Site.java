
public class Site {
	
	private Recommendation requestReco(Request req) {
		try {
		Recommendeur reco = new Recommendeur();
		Recommendation recommendation =  reco.recommendMeSomeThing(req);
		return recommendation;
		} catch (ExceptionRecoNotValid e) {
			System.out.println("Erreur :"+e.getCode());
			return null;
		}
	}
	
	private void maj(Request req) {
		try{
			AlgoLourdBayes b = new AlgoLourdBayes();
			b.maj();
		} catch (Exception e) {
			System.out.println("Erreur :"+e);
		}
	}
	
	private void getFeedback(Request req) {
		try{
			Interprete.setFeedback(req);
			
		} catch (Exception e) {
			System.out.println("Erreur : "+e);
		}
	}
	
}
