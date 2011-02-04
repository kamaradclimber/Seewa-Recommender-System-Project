
public abstract class AlgoLeger {
	
	public abstract Recommendation answers(Request req) throws ExceptionRecoNotValid;
		//exemple pour un algo de recommendation
		//return new Recommendation();

	public Request transform(Request req) {
		// crée une requete adaptée à l'algo
		return req;
	}
	
}
