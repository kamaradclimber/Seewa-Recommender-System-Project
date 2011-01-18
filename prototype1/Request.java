import org.bson.types.ObjectId;


public class Request {


	private String request="";
	
	public Request(String req) {
		this.request = req;
	}
	
	public ObjectId getUser() {
		System.out.println("On fait bien de la reco d'utilisateur ?");
		return new ObjectId(this.request);
	}
	
	
	public String get() {
		return this.request;
	}
	
	public String getTypeOfRequest() throws ExceptionRecoNotValid {
		if (this.request.contains("USER")) {
			return "USER";
		} else if (this.request.contains("PAGE")) {
			return "PAGE";
		} else {
			throw new ExceptionRecoNotValid(ExceptionRecoNotValid.ERR_UNKNOWN_REQUEST);
		}
	}
	
	
}
