import java.util.ArrayList;

import org.bson.types.ObjectId;


public class DataUserNode implements Data {
	private ObjectId id; //l'id qui est dans mongo
	private String name;
	private ArrayList<UserRelation> friends; //TODO : changer en recommenders
	private ArrayList<DataUPage> uPages;
	double uPageMean; //moyenne des page rank des UPages.

	
	public DataUserNode(ObjectId id, ArrayList<DataUPage> dataupages) {
		this.id = id;
		this.uPages = dataupages;
	}
	
	public DataUserNode(String name, ObjectId id, ArrayList<UserRelation> friends, ArrayList<DataUPage> uPages)
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

	public String getName() {
		return this.name;
	}
	
	@Override
	public ObjectId getMongoId() {
		return this.id;
	}
	
	public void setFriends(ArrayList<UserRelation> userrelations) {
		this.friends = userrelations;
	}
	
	private class UserRelation{
		DataUserNode friend;
		double crossProbability; // P(A inter B) = proba that both like a page
		int posFeedback;
		int negFeedback;

		
	}

}
