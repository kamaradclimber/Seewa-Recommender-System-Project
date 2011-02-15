import java.util.ArrayList;
import java.util.Hashtable;

import org.bson.types.ObjectId;


public class DataUserNode implements Data {
	private ObjectId id; //l'id qui est dans mongo
	private ArrayList<DataUserRelation> friends; //TODO : changer en recommenders
	private ArrayList<DataUPage> uPages;
	double uPageMean; //moyenne des page rank des UPages.

	
	public DataUserNode(ObjectId id) {
		this.id = id;
	}
	
	public DataUserNode(ObjectId id, ArrayList<DataUPage> dataupages) {
		this.id = id;
		this.uPages = dataupages;
		this.friends= new ArrayList<DataUserRelation>();
		uPageMean=0;
		for (DataUPage uPage:uPages)
		{
			uPageMean+= uPage.pageRank;
		}
		if (uPages.size()!=0)
			uPageMean= uPageMean/uPages.size();
		else uPageMean=1;
	}
	
	public DataUserNode(ArrayList<DataUserRelation> friend,ObjectId id) {
		this(id, new ArrayList<DataUPage>());
		this.friends = friend;
	}
	
	public DataUserNode(ObjectId id, ArrayList<DataUserRelation> friends, ArrayList<DataUPage> uPages)
	{
		this(id, uPages);
		this.friends=friends;
	}

	
	
	void setUPages(ArrayList<DataUPage> uPages) {
		this.uPages = uPages;
		uPageMean=0;
		for (DataUPage uPage:uPages)
		{
			uPageMean+= uPage.pageRank;
		}
		if (uPages.size()!=0)
			uPageMean= uPageMean/uPages.size();
	}

	public ArrayList<DataUserRelation> getRecommandeurs() {
		if (this.friends == null) {
			return new ArrayList<DataUserRelation>();
		}
		else return this.friends;
	}
	
	
	
	public ObjectId getMongoId() {
		return this.id;
	}
	
	public void setFriends(ArrayList<DataUserRelation> userrelations) {
		this.friends = userrelations;
	}
	
	public void addFriend(DataUserRelation relation)
	{
		if (this.friends==null) this.friends= new ArrayList<DataUserRelation>();
		this.friends.add(relation);
	}
	
	public void addUPage(DataUPage page)
	{
		if (this.uPages==null) this.uPages= new ArrayList<DataUPage>();
		this.uPages.add(page);
		uPageMean = (uPageMean*(uPages.size()-1) + page.getPR() ) / uPages.size();
	}
	
	public boolean updateProbabilities()
	{
		boolean change= false;
		for (DataUserRelation userR : friends)
		{
			change= userR.updateProbability(this) || change ; //attention à l'evaluation paresseuse !
		}
		
		uPageMean=0;
		for (DataUPage uPage:uPages)
		{
			uPageMean+= uPage.pageRank;
		}
		if (uPages.size()!=0)
			uPageMean= uPageMean/uPages.size();
		else uPageMean=1;
		return change;
	}
	
	public ArrayList<DataUPage> getUPages() {
		return this.uPages;
	}

	public ObjectId getId() {
		return this.getMongoId();
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	@Override
	public String toString() {
		String uPagesList = "";
		
		for (DataUPage uPage : uPages)
		{
			uPagesList += uPage.toString()+ "\n";
		}
		
		String friendsList = "";
		
		for (DataUserRelation friend : friends)
		{
			friendsList += friend+ "\n";
		}
		
		return "DataUserNode [id=" + id + " friends="
				+ friendsList + ", uPages=" + uPagesList + ", uPageMean=" + uPageMean
				+ "]";
	}


	
	
	
	
	


	



	

}
