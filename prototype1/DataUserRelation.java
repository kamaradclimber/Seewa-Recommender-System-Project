import java.util.ArrayList;
import java.util.Hashtable;


public class DataUserRelation{
		DataUserNode friend;
		double crossProbability; // P(A inter B) = proba that both like a page
		int posFeedback;
		int negFeedback;

		public DataUserRelation( DataUserNode friend, double crossProbability)
		{
			this.friend=friend;
			this.crossProbability=crossProbability;
			posFeedback=0;
			negFeedback=0;
		}
		
		public DataUserRelation( DataUserNode friend, double crossProbability, int posFeedback, int negFeedback)
		{
			this(friend, crossProbability);
			this.posFeedback=posFeedback;
			this.negFeedback=negFeedback;
		}
		
		public DataUserRelation( DataUserNode friend)
		{
			this.friend=friend;
			this.crossProbability=0;
			this.posFeedback=0;
			this.negFeedback=0;
		}
		
		public DataUserRelation( DataUserNode friend,int posFeedback, int negFeedback)
		{
			this( friend);
			this.posFeedback=posFeedback;
			this.negFeedback=negFeedback;
		}
		
		@Override
		public String toString() {
			return "DataUserRelation [friend=" + friend.getMongoId() + ", crossProbability="
					+ crossProbability + ", posFeedback=" + posFeedback
					+ ", negFeedback=" + negFeedback + "]";
		}

		public boolean updateProbability(DataUserNode owner) //cr�e la proba, renvoie true si valeur a �t� modifi�e
		{
			ArrayList<DataUPage> friendUPages=friend.getUPages();
			ArrayList<DataUPage> ownerUPages=owner.getUPages();
			
			int nInter=0; //intersection
			double sum=0;
			
			Hashtable<String, DataUPage> url_PR = new Hashtable<String, DataUPage>();
			for (DataUPage myUPage: ownerUPages)
			{
				url_PR.put(myUPage.getUrl(), myUPage);
			}
			
			for (DataUPage hisUPage: friendUPages)
			{
				if ( url_PR.containsKey(hisUPage.getUrl()))
				{
					double myPR= url_PR.get(hisUPage.getUrl()).getPR();
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

		public DataUserNode getFriend() {
			return friend;
		}

		public void setFriend(DataUserNode friend) {
			this.friend = friend;
		}

		public double getCrossProbability() {
			return crossProbability;
		}

		public void setCrossProbability(double crossProbability) {
			this.crossProbability = crossProbability;
		}

		public int getPosFeedback() {
			return posFeedback;
		}

		public void setPosFeedback(int posFeedback) {
			this.posFeedback = posFeedback;
		}

		public int getNegFeedback() {
			return negFeedback;
		}

		public void setNegFeedback(int negFeedback) {
			this.negFeedback = negFeedback;
		}
	}
