import org.bson.types.ObjectId;


public final class DataUPage implements Data {

	private ObjectId id;
	double pageRank;
	//Time Stamp?
	
	public DataUPage( ObjectId id, double pageRank)
	{
		this.id = id;
		this.pageRank=pageRank;
	}
	
	@Override
	public ObjectId getMongoId() {
		
		return this.id;
	}

}
