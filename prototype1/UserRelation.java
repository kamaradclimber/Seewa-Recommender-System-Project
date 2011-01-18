import java.util.ArrayList;
import java.util.Hashtable;


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

		public boolean updateProbability(DataUserNode owner) //crée la proba, renvoie true si valeur a été modifiée
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
