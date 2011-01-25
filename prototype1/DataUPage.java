import org.bson.types.ObjectId;


public final class DataUPage implements Data {

	private ObjectId id;//userId?
	private ObjectId userId;
	

	double pageRank;
	private String url; //on stock pour connaitre les pages qui sont partag�es par 2 users
	//Time Stamp?
	
	private DataUPage(ObjectId id, ObjectId userId,double pageRank) {
		this.id = id;
		this.pageRank=pageRank;
		this.userId = userId;
	}
	
	
	public DataUPage( ObjectId id, ObjectId userId, double pageRank, String url)
	{
		this(id,userId, pageRank);
		this.url=url;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	
	@Override
	public String toString() {
		return "DataUPage [id=" + id + ", pageRank=" + pageRank + ", url="
				+ url + " user="+ userId+"]";
	}


	@Override
	public ObjectId getMongoId() {
		
		return this.id;
	}

	public double getPR() {
		
		return pageRank;
	}
	
	public ObjectId getUserId() {
		return userId;
	}


	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}
}
