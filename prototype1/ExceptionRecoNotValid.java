
public class RecoException extends Exception {

	//On associe ˆ tous les types d'exception que nous pouvons avoir un code :
	
	static public final int ERR_UNKNOWN_REQUEST = 1;  
	static public final int ERR_DB_READING_USER = 2;
	static public final int ERR_DB_READING_CLUSTER = 3;
	static public final int ERR_NO_CLUSTER_ASSIGNED_TO_USER = 4;
	static public final int ERR_WRITING_CLUSTER = 5;
	static public final int ERR_NO_USER_TO_RECOMMEND = 6;
	static public final int ERR_VERIFICATEUR = 7;
	static public final int ERR_CONNECTION_DB = 8;
	
	
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
