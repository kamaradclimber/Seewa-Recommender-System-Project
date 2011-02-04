import org.bson.types.ObjectId;


public class Request {

	

	private ObjectId userId;
	private String url;
	private ObjectId category; //eventuellement pour affiner les recos.
	private TypeOfRequest type;
	
	public Request(ObjectId userId, String url, ObjectId category, TypeOfRequest type) {
		
		this.userId = userId;
		this.url = url;
		this.category = category;
		this.type = type;
	}
	
	public String toString() {
		return "userId: "+this.userId + " url:"+this.url + " (category: "+category+")";
	}
	
	public TypeOfRequest getTypeOfRequest() throws ExceptionRecoNotValid {
		return this.type;
	}
	

	public ObjectId getUserId() {
		return this.userId;
	}


	public String getUrl() {
		return this.url;
	}

	public ObjectId getCategory() {
		return this.category;
	}
	
}
