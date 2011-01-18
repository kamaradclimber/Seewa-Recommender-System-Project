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
	
	public void setFriends(ArrayList<UserRelation> userrelations) {
		this.friends = userrelations;
	}
	
	public boolean updateProbabilities()
	{
		boolean change= false;
		for (UserRelation userR : friends)
		{
			change= change || userR.updateProbability(this);
		}
		
		return change;
	}
	
	public ArrayList<DataUPage> getUPages() {
		return this.uPages;
	}
	
	
	
	public class UserRelation{
		DataUserNode friend;
		double crossProbability; // P(A inter B) = proba that both like a page
		int posFeedback;
		int negFeedback;

		public UserRelation( DataUserNode friend, double crossProbability)
		{
			this.friend=friend;
			this.crossProbability=crossProbability;
			posFeedback=0;
			negFeedback=0;
		}
		
		public UserRelation( DataUserNode friend, double crossProbability, int posFeedback, int negFeedback)
		{
			this(friend, crossProbability);
			this.posFeedback=posFeedback;
			this.negFeedback=negFeedback;
		}
		
		public UserRelation( DataUserNode friend)
		{
			this.friend=friend;
			this.crossProbability=0;
			this.posFeedback=0;
			this.negFeedback=0;
		}
		
		public UserRelation( DataUserNode friend,int posFeedback, int negFeedback)
		{
			this( friend);
			this.posFeedback=posFeedback;
			this.negFeedback=negFeedback;
		}

		public boolean updateProbability(DataUserNode owner) //cr�e la proba, renvoie true si valeur a �t� modifi�e
		{
			ArrayList<DataUPage> friendUPages=friend.getUpages();
			ArrayList<DataUPage> ownerUPages=owner.getUpages();
			
			int nInter=0; //intersection
			double sum=0;
			
			Hashtable<String, DataUPage> url_PR = new Hashtable<String, DataUPage>();
			for (DataUPage myUPage: ownerUPages)
			{
				url_PR.put(myUPage.getURL(), myUPage);
			}
			
			for (DataUPage hisUPage: friendUPages)
			{
				if ( url_PR.containsKey(hisUPage.getURL()))
				{
					double myPR= url_PR.get(hisUPage.getURL()).getPR();
					double hisPR= hisUPage.getPR();
					//2*PRa*PRb / (PRa^2+PRb^2)
					sum+= 2*myPR*hisPR/ (Math.pow(myPR,2) + Math.pow(hisPR,2));
					nInter++;
				}
			}
			
			if (nInter==0) //division par :0
			{
				this.crossProbability=0;
				return false;
			}
			double oldValue= this.crossProbability;
			this.crossProbability = sum/nInter;
			return this.crossProbability == oldValue;
			}
	}



	

}
