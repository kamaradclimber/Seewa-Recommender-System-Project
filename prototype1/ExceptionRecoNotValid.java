
public class RecoException extends Exception {

	//On associe à tous les types d'exception que nous pouvons avoir un code :
	
	
	/* Exceptions concernant la requête de recommandation */
	
	static public final int ERR_UNKNOWN_REQUEST = 1;  
	static public final int ERR_DB_READING_USER = 2;
	static public final int ERR_DB_READING_CLUSTER = 3;
	static public final int ERR_NO_CLUSTER_ASSIGNED_TO_USER = 4;
	static public final int ERR_WRITING_CLUSTER = 5;
	static public final int ERR_NO_USER_TO_RECOMMEND = 6;
	static public final int ERR_VERIFICATEUR = 7;
	static public final int ERR_CONNECTION_DB = 8;
	static public final int ERR_DB_READING_UTR = 9;
	
	/* Exceptions concernant la mise à jour des clusters */
	
	static public final int NO_CLUSTER = 10;
	
	
	public int code;
	
	public RecoException()
	{
	}
	
	public RecoException(int aCode) {
		super();
		code = aCode;
	}
	
	public int getCode() {
		return code;
	}
	
}
