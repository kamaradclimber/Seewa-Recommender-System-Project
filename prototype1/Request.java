
public class Request {


	private String request="";
	
	public Request(String req) {
		this.request = req;
	}
	
	public String get() {
		return this.request;
	}
	
	public String getTypeOfRequest() throws RecoException {
		if (this.request.contains("USER")) {
			return "USER";
		} else if (this.request.contains("PAGE")) {
			return "PAGE";
		} else {
			throw new RecoException(RecoException.ERR_UNKNOWN_REQUEST);
		}
	}
	
	
}
