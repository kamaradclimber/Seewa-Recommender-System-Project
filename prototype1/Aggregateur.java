import java.util.List;


public class Aggregateur {



	public Recommendation merges(List<Recommendation> recos) throws RecoException {
		if (!recos.isEmpty()) {
		return recos.get(0);
		}
		else throw new RecoException(RecoException.ERR_NO_USER_TO_RECOMMEND);
	}
}
