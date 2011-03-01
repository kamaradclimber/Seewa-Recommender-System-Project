package recommender;
import org.bson.types.ObjectId;


public class Request {

	

	private ObjectId recoReceiver;
	private String url;
	private ObjectId category; //eventuellement pour affiner les recos.
	private TypeOfRequest type;
	private Boolean clicked;
	private ObjectId recoGiver;
	private ObjectId feedbackId;
	
//	public Request(ObjectId userId, String url, ObjectId category, TypeOfRequest type) {
//		
//		this.recoReceiver = userId;
//		this.url = url;
//		this.category = category;
//		this.type = type;
//	}
	
	
	public Request(String s) throws Exception {
		//construct a request based on the 3 differents type of requests
		String[] split = s.split("/");
		if (split[0].equals("recommandation")) {
			// syntax is recommandation/userObjectId/url 
			if (split.length <2) throw new Exception("Invalid size url");
			this.type   = TypeOfRequest.RECOPAGE;
			this.recoReceiver = new ObjectId(split[1]);

			
			this.url    = split[2]; //TODO il faudrait appender toute la fin du tableau car l'url a été aussi coupée sur les / donc google.com/iamthere aura perdu son iamthere
		} else if (split[0].equals("feedback")) {
			//syntax is feedback/recoGiver/recoReceiver/clicked
			this.type = TypeOfRequest.FEEDBACK;
			if (split.length !=4) throw new Exception("pas assez d'arguments pour ce type de requete ");
			this.recoGiver    = new ObjectId(split[1]);
			this.recoReceiver = new ObjectId(split[2]);
			this.clicked      = new Boolean(split[3]);
			
		} else if (split[0].equals("update")) {
			this.type = TypeOfRequest.MAJ;
		} else {
			throw new Exception("type non reconnu : "+split[0]);
		}
		
	}
	
	
	@Override
	public String toString() {
		return "Request [category=" + category + ", clicked=" + clicked
				+ ", recoGiver=" + recoGiver + ", recoReceiver=" + recoReceiver
				+ ", type=" + type + ", url=" + url + "]";
	}


	public TypeOfRequest getTypeOfRequest() throws ExceptionRecoNotValid {
		return this.type;
	}
	

	public ObjectId getUserId() {
		return this.recoReceiver;
	}


	public String getUrl() {
		return this.url;
	}

	public ObjectId getCategory() {
		return this.category;
	}
	
	public ObjectId getRecoGiver() {
		return this.recoGiver;
	}
	

	private ObjectId getRecoReceiver() {
		return this.recoReceiver;
	}
	
	public DataFeedBack toDataFeedBack() {
		DataFeedBack r= new DataFeedBack(this.feedbackId, this.clicked, this.getRecoGiver(), this.getRecoReceiver());
		return r;
	}


	
	
}
