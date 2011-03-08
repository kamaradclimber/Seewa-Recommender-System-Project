package recommender;
import java.util.ArrayList;
import java.util.Hashtable;


public class DataUserRelation implements Comparable<DataUserRelation>{
		DataUserNode recommandeur;
		double crossProbability; // P(A inter B) = proba that both like a page
		int posFeedback;
		int negFeedback;

		public DataUserRelation( DataUserNode recommandeur, double crossProbability)
		{
			this.recommandeur=recommandeur;
			this.crossProbability=crossProbability;
			posFeedback=0;
			negFeedback=0;
		}
		
		public DataUserRelation( DataUserNode recommandeur, double crossProbability, int posFeedback, int negFeedback)
		{
			this(recommandeur, crossProbability);
			this.posFeedback=posFeedback;
			this.negFeedback=negFeedback;
		}
		
		public DataUserRelation( DataUserNode recommandeur)
		{
			this.recommandeur=recommandeur;
			this.crossProbability=0;
			this.posFeedback=0;
			this.negFeedback=0;
		}
		
		public DataUserRelation( DataUserNode recommandeur,int posFeedback, int negFeedback)
		{
			this( recommandeur);
			this.posFeedback=posFeedback;
			this.negFeedback=negFeedback;
		}
		
		@Override
		public String toString() {
			return "DataUserRelation [friend=" + recommandeur.getMongoId() + ", crossProbability="
					+ crossProbability + ", posFeedback=" + posFeedback
					+ ", negFeedback=" + negFeedback + "]";
		}

		public boolean updateProbability(DataUserNode owner) //cr�e la proba, renvoie true si valeur a �t� modifi�e
		{
			ArrayList<DataUPage> recommandeurUPages=recommandeur.getUPages();
			ArrayList<DataUPage> ownerUPages=owner.getUPages();
			
			int nInter=0; //intersection
			double sum=0;
			
			Hashtable<String, DataUPage> url_PR = new Hashtable<String, DataUPage>();
			for (DataUPage myUPage: ownerUPages)
			{
				url_PR.put(myUPage.getUrl(), myUPage);
			}
			
			for (DataUPage hisUPage: recommandeurUPages)
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

		public DataUserNode getRecommender() {
			return recommandeur;
		}

		public void setFriend(DataUserNode recommandeur) {
			this.recommandeur = recommandeur;
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


		public int compareTo(DataUserRelation o) {
			//cette fonction de comparaison est utilisée pour trouver un nouveau recommendeur
			if (this==o) return 0;
			if (this.crossProbability > o.crossProbability) return -1;
			if (this.crossProbability < o.crossProbability) return 1;
			if (this.posFeedback > o.posFeedback) return -1;
			if (this.negFeedback < o.negFeedback) return 1;
			//Sinon on compare les Id.
			return this.recommandeur.getId().compareTo(o.recommandeur.getId());
			}
	}
