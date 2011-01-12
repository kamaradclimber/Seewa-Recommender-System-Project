
public class Site {
	
	public Recommendation requestReco(Request req) {
		try {
		Recommendeur reco = new Recommendeur();
		Recommendation recommendation =  reco.recommendMeSomeThing(req);
		return recommendation;
		} catch (ExceptionRecoNotValid e) {
			System.out.println("Erreur :"+e.getCode());
			return null;
		}
	}
}
