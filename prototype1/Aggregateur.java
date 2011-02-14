import java.util.Hashtable;
import java.util.List;


public class Aggregateur {



	public Recommendation merges(List<Recommendation> recos) throws ExceptionRecoNotValid {
		if (!recos.isEmpty()) {
			Hashtable<String, Integer> recoNumber = new Hashtable<String, int>();
			Hashtable<String, Recommendation> recoTable = new Hashtable<String, Recommendation>();
			for (Recommendation reco : recos)
			{
				recoNumber.get(reco.url) ++;
				recoTable.put(reco.url, reco);
			}
			int max = -1;
			String best = null;
			for ( String url : reconumber.keys())
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
		// TODO Auto-generated method stub
		
	}
}
