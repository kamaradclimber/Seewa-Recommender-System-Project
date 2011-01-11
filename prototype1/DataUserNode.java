import java.util.ArrayList;

import org.bson.types.ObjectId;


public class DataUserNode implements Data {
	private ObjectId id; //l'id qui est dans mongo
	private String name;
	public ArrayList<UserRelation> friends;
	public ArrayList<DataUPage> uPages;
	double uPageMean; //moyenne des page rank des UPages.

	
	
	
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
	
	private class UserRelation{
		DataUserNode friend;
		double crossProbability; // P(A inter B) = proba that both like a page
		int posFeedback;
		int negFeedback;
		
	}

}
