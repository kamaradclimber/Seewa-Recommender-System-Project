import org.bson.types.ObjectId;


public class BayesRequest extends Request {

	private ObjectId userId;
	private String url;
	private ObjectId category;
	//private ObjectId category; //eventuellement pour affiner les recos.
	
	
	public BayesRequest(String req) {
		super(req);
		//userId= this.getUser();
		//url= /*super.*/extractURL(req);
		//category = (ObjectId) Interprete.getPage(url).get('category');
	}
	 

	public ObjectId getUserId() {
		return userId;
	}


	public String getUrl() {
		return url;
	}

	public ObjectId getCategory() {
		return category;
	}
	
	

}
