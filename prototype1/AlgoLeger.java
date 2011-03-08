package recommender;


public abstract class AlgoLeger {
	
	public abstract Recommendation answers(Request req) throws ExceptionRecoNotValid, NoRecoHasBeenFound;
		//exemple pour un algo de recommendation
		//return new Recommendation();

	public Request transform(Request req) {
	//cette méthode permet de tranformer la requete générale en reuqet pour lalgo si besoin
		// crée une requete adaptée à l'algo
		return req;
	}
	
}
