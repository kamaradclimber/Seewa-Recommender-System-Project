
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import com.mongodb.*;


public class Interprete {

	static ArrayList<DataCluster> clusters=null;
	static Hashtable<String, DataVector> usersByNames= new Hashtable<String, DataVector>();
	static Hashtable<DataVector, String> usersByUTR = new Hashtable<DataVector, String>();
	

	//DB connection
	static Mongo mongo = new Mongo( "138.195.76.136", 27017 );
	static DB db = mongo.getDB( "test" );

	public void write(String table, String column, String data) {
		//UPDATE table SET value=data WHERE name=column
	}
	
	public List<String> read(Request r) {
		//SELECT ....
		return new ArrayList<String>();
	}

	static private DataVector db2DataVector(DBObject obj) {
		// TODO faire la fonction qui prend un dbobject pour en creer un datavector
		
	}
	
	
	 static public ArrayList<DataCluster> readClusters(Request request) throws RecoException {
		 // Un cluster en base de donnée est stocké avec un champ centroid, et une liste des UTR
		try {
		DBCollection coll = db.getCollection("clusters");
		DBCursor cursor = coll.find();
		ArrayList<DataCluster> clusters = new ArrayList<DataCluster>();
		
		
		DBObject cluster= null;
		while(cursor.hasNext()) {
			cluster = cursor.next();
			//init de la centroid :
			DataVector centroid = new DataVector(false);
			
			centroid = Interprete.db2DataVector(cluster.get("centroid"));
			
		}
		return clusters;
		}
		catch (MongoException ex) {
			throw new RecoException(RecoException.ERR_DB_READING_CLUSTER);
		}
	}
	
	public static boolean writeClusters(ArrayList<DataCluster> clusters) throws RecoException {
		try {
		Interprete.clusters=clusters;
		return true;
		}
		catch (MongoException e){
			throw new RecoException(RecoException.ERR_WRITING_CLUSTER);
		}
	}

	public static DataVector readUTR(String username) throws RecoException {
		try {
		return usersByNames.get(username);
		}
		catch (MongoException ex) {
			throw new RecoException(RecoException.ERR_DB_READING_USER);
		}
	}
		
	
	static public void writeUcr(String username, DataVector utr) {
		
		
		usersByNames.put(username,utr);
		usersByUTR.put(utr, username);
	}
	
//	public static DataUser getUser(DataVector vect) {
//		
//		return new DataUser(usersByUCR.get(vect), vect);
//	}

	
}
