
public class Request {

<<<<<<< HEAD
	private String request="";
	
	public Request(String req) {
		this.request = req;
	}
	
	public String get() {
		return this.request;
	}
	
	public String getTypeOfRequest() throws Exception {
		if (this.request.contains("USER")) {
			return "USER";
		} else if (this.request.contains("PAGE")) {
			return "PAGE";
		} else {
			throw new Exception("Tu demandes une requete de type inconnu");
		}
	}
	
=======
	
	public Request(String s) {
		// construire ici une requeste à partir d'une string
	}
>>>>>>> 3cb1ac7da2ab62e6c9c6f27a5049602f24d84637
}
