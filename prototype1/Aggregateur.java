import java.util.List;


public class Aggregateur {



	public Recommendation merges(List<Recommendation> recos) throws ExceptionRecoNotValid {
		if (!recos.isEmpty()) {
		return recos.get(0);
		}
		else throw new ExceptionRecoNotValid(ExceptionRecoNotValid.ERR_NO_USER_TO_RECOMMEND);
	}
}
