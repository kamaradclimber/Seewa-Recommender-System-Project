import java.util.ArrayList;
import java.util.Hashtable;

import org.bson.types.ObjectId;


public class DataUserNode implements Data {
	private ObjectId id; //l'id qui est dans mongo
	private String name;
	private ArrayList<DataUserRelation> friends; //TODO : changer en recommenders
	private ArrayList<DataUPage> uPages;
	double uPageMean; //moyenne des page rank des UPages.

	
	public DataUserNode(ObjectId id, ArrayList<DataUPage> dataupages) {
		this.id = id;
		this.uPages = dataupages;
	}
	
	public DataUserNode(String name, ObjectId id, ArrayList<DataUserRelation> friends, ArrayList<DataUPage> uPages)
	{
		this.id = id;
		this.name=name;
		this.friends=friends;
		this.uPages=uPages;
		double uPageMean=0;
		for (DataUPage uPage:uPages)
		{
			uPageMean+= uPage.pageRank;
		}
		uPageMean= uPageMean/uPages.size();
		
	}

	public ArrayList<DataUserRelation> getFriends() {
		return this.friends;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public ObjectId getMongoId() {
		return this.id;
	}
	
	public void setFriends(ArrayList<DataUserRelation> userrelations) {
		this.friends = userrelations;
	}
	
	public boolean updateProbabilities()
	{
		boolean change= false;
		for (DataUserRelation userR : friends)
		{
			change= change || userR.updateProbability(this);
		}
		
		return change;
	}
	
	public ArrayList<DataUPage> getUPages() {
		return this.uPages;
	}
	
	
	
	


	



	

}
