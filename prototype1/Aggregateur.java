import java.util.Hashtable;
import java.util.List;


public class Aggregateur {



	public Recommendation merges(List<Recommendation> recos) throws ExceptionRecoNotValid {
		if (!recos.isEmpty()) {
			Hashtable<String, Integer> recoNumber = new Hashtable<String, Integer>();
			Hashtable<String, Recommendation> recoTable = new Hashtable<String, Recommendation>();
			for (Recommendation reco : recos)
			{
				if (!recoNumber.containsKey(reco.url)) recoNumber.put(reco.url, 0);
				recoNumber.put(reco.url, recoNumber.get(reco.url)+1);
				recoTable.put(reco.url, reco);
			}
			int max = -1;
			String best = null;
			for ( String url : recoNumber.keySet())
			{
				if (recoNumber.get(url)>max)
				{
					best = url;
					max = recoNumber.get(url);
				}
			}
			
		return recoTable.get(best);
		}
		else throw new ExceptionRecoNotValid(ExceptionRecoNotValid.ERR_NO_USER_TO_RECOMMEND);
	}

	public void addAlgo(AlgoLeger algoInstance) {
		//ne fait rien donc on pourrait peut etre la supprimer // TODO?
		
	}
}
