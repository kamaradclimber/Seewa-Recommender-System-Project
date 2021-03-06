package recommender;

public class Site {
	
	public Recommendation requestReco(Request req) {
		try {
		Recommendeur reco = new Recommendeur();
		Recommendation recommendation =  reco.recommendMeSomeThing(req);
		//TODO est-ce que les recommandation scomportent une indication de lutilisateur qui a fait la reco ??? sinon on peut pas faire de feedback
		return recommendation;
		} catch (ExceptionRecoNotValid e) {
			System.out.println("Erreur :"+e.getCode());
			e.printStackTrace();
			return null;
		} catch (NoRecoHasBeenFound e) {
			return new Recommendation("no recommendation can be made for this user");
		}
	}
	
	public void maj(Request req) {
		// request an update for our system
		try{
			AlgoLourdBayes b = new AlgoLourdBayes();
			b.maj();
		} catch (Exception e) {
			System.out.println("Erreur :"+e);
		}
	}
	
	public void Feedback(Request req) {
		try{
			
			Interprete.setFeedBack(req.toDataFeedBack());
			
		} catch (Exception e) {
			System.out.println("Erreur : "+e);
		}
	}
	
	
	
}
