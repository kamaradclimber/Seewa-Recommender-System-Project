
public class Site {
	
	public Recommendation requestReco(Request req) {
		Recommendeur reco = new Recommendeur();
		try {
			Recommendation recommendation =  reco.recommendMeSomeThing(req);
			return recommendation;
		} catch (ExceptionRecoNotValid e) {
			System.out.println("La recommendation nétait pas valide, que faire");
			e.printStackTrace();
			return null;
		}
	}
}
