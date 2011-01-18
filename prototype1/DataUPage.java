import org.bson.types.ObjectId;


public final class DataUPage implements Data {

	private ObjectId id;//userId?
	double pageRank;
	private String url; //on stock pour connaitre les pages qui sont partagées par 2 users
	//Time Stamp?
	String url;
	
	public DataUPage( ObjectId id, double pageRank, String url)
	{
		this.id = id;
		this.pageRank=pageRank;
		this.url=url;
	}
	
	@Override
	public ObjectId getMongoId() {
		
		return this.id;
	}

	public String getURL() {
		
		return url;
	}

	public double getPR() {
		
		return pageRank;
	}
}
