import org.bson.types.ObjectId;


public final class DataUPage implements Data {

	private ObjectId id;//userId?
	double pageRank;
	private String url; //on stock pour connaitre les pages qui sont partag�es par 2 users
	//Time Stamp?
	
	
	public DataUPage( ObjectId id, double pageRank, String url)
	{
		this.id = id;
		this.pageRank=pageRank;
		this.url=url;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	
	@Override
	public ObjectId getMongoId() {
		
		return this.id;
	}

	public double getPR() {
		
		return pageRank;
	}
}
