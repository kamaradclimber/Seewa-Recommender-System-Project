
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import com.mongodb.*;


public class Interprete {

	//static ArrayList<DataCluster> clusters=null;
	//static Hashtable<String, DataVector> usersByNames= new Hashtable<String, DataVector>();
	//static Hashtable<DataVector, String> usersByUCR = new Hashtable<DataVector, String>();


	//DB connection

static DB db;

	static {
		try {
			Mongo mongo = new Mongo( "138.195.76.136", 27017 );
			DB db = mongo.getDB( "test" );
		}
		catch (UnknownHostException ex) {
			RecoException erreur = new RecoException(RecoException.ERR_CONNECTION_DB);
			System.out.println("Erreur :"+erreur.getCode());
			System.exit(1);
		}
	}
	
	public void write(String table, String column, String data) {
		//UPDATE table SET value=data WHERE name=column
	}

	public List<String> read(Request r) {
		//SELECT ....
		return new ArrayList<String>();
	}

	static private DataVector db2DataVector(DBObject obj, Integer id, Integer user_id) {
		// prend un dbobject pour en creer un datavector
		DataVector vector = null;
		if (id != null ) {
			vector = new DataVector(id, user_id);	
		} else {
			vector = new DataVector(false);	
		}
		for (String key : obj.keySet() ) {
			vector.put(key, (Float)obj.get(key)); 
		}
		return vector;

	}

	static private BasicDBObject dataVector2db(DataVector vect){
		BasicDBObject obj = new BasicDBObject();
		for (String key : vect.keySet()) {
			obj.put(key, vect.get(key));
		}
		return obj;
	}


	static public ArrayList<DataCluster> readClustersCentroids(Request request) throws RecoException {
		//this function should be used only for getting centroid (for research use only)
		// Un cluster en base de donnée est stocké avec un champ centroid
		//cette fonction est un peu optmisee pour la recherche quand on a besoin seulement des centroids
		try {
			DBCollection coll = db.getCollection("clusters");
			DBCursor cursor = coll.find();
			ArrayList<DataCluster> clusters = new ArrayList<DataCluster>();


			DBObject cluster= null;
			while(cursor.hasNext()) {
				cluster = cursor.next();
				//init de la centroid :
				DataVector centroid = new DataVector(false);
				DBObject cent = (DBObject) cluster.get("centroid");
				Integer id_   =  (Integer) cluster.get("_id");
				int id = (int) id_;
				centroid = Interprete.db2DataVector(cent, null,null);
				clusters.add(new DataCluster(id, centroid, new ArrayList<DataVector>()));
			}
			return clusters;
		}
		catch (MongoException ex) {
			throw new RecoException(RecoException.ERR_DB_READING_CLUSTER);
		}
	}



	static public ArrayList<DataCluster> readClusters() throws RecoException {
		// Un cluster en base de donnée est stocké avec un champ centroid et des liens vers les UTR
		try {
		DBCollection coll = db.getCollection("clusters");
		DBCollection users = db.getCollection("users");
		DBCursor cursor = coll.find();
		ArrayList<DataCluster> clusters = new ArrayList<DataCluster>();

		DBObject cluster= null;
		while(cursor.hasNext()) {
			cluster = cursor.next();
			//init de la centroid :
			DataVector centroid = new DataVector(false);
			DBObject cent = (DBObject) cluster.get("centroid");
			Integer id_   =  (Integer) cluster.get("_id");
			int id = (int) id_;
			centroid = Interprete.db2DataVector(cent, null,null);

			BasicDBList utrs = (BasicDBList) cluster.get("utr_ids");
			ArrayList<DataVector> utrss =  new ArrayList<DataVector>();
			for (Object user_id : utrs) {
				BasicDBObject query = new BasicDBObject();
				query.put("_id", user_id);
				DBObject user = users.findOne(query, new BasicDBObject("utr",1)); //on reccupere seulemnent le champ utr
				DBObject userr = (DBObject) user.get("utr");
				utrss.add(Interprete.db2DataVector(userr, (Integer) user_id,(Integer) user_id)); //FIXME il y  asurementn un probleme
			}
			clusters.add(new DataCluster(id, centroid, utrss));
		}
		return clusters;
		}
		catch (MongoException ex) {
			throw new RecoException(RecoException.ERR_DB_READING_CLUSTER);
		}
	} 

	public static boolean writeClusters(ArrayList<DataCluster> clusters) throws RecoException {
		//renvoie j'ai réussi ou pas 
		//Interprete.clusters=clusters; // a quoi sert cette ligne ? FIXME
		try {
			DBCollection users = db.getCollection("clusters");
			for (DataCluster cluster : clusters) {
				BasicDBObject query = new BasicDBObject("_id",cluster.getId());
				BasicDBObject clusterr = new BasicDBObject();
				clusterr.put("centroid", Interprete.dataVector2db(cluster.getCentroid())); //on rajoute la centroid
				BasicDBList vectors = new BasicDBList();
				for (DataVector utr : cluster) {
					vectors.add(utr.getUserId()); //on ajoute l'id du user pour l'identifier 
				}
				clusterr.put("utr_ids", vectors);
				users.update(query, clusterr ,true,false);

			}


			return true;
		}
		catch (MongoException ex) {
			throw new RecoException(RecoException.ERR_WRITING_CLUSTER);
		}
	}

	public static DataVector readUTR(Object id) throws RecoException { //TODO mettre un type un peu plus précis pour l íd
		//renvoie l'UTR d'un user à partir d'un id de l'user
		try {
			DBCollection users = db.getCollection("users");
			BasicDBObject query = new BasicDBObject("_id",id); //preparation de la query
			DBObject user = users.findOne(query,new BasicDBObject("utr",1));
			DBObject utr =  (DBObject) user.get("utr"); //on caste TODO : faire un try..catch pour eviter les pblemes

			return Interprete.db2DataVector(utr, (Integer)id,(Integer)id); //this data matters so on lui passe l'id qui va bien
		}
		catch (MongoException ex) {
			throw new RecoException(RecoException.ERR_DB_READING_USER);
		}
	}


	public static void writeUTR(Object id, DataVector utr) {
		//TODO : gestion de l'exception

		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id",id); //preparation de la query
		BasicDBObject user = new BasicDBObject();
		user.put("_id", id);
		user.put("utr", Interprete.dataVector2db(utr));

		users.update(query, user  ,true,false);
		//usersByNames.put(username,ucr);
		//usersByUCR.put(ucr, username);
	}

	//	public static DataUser getUser(DataVector vect) {
	//		
	//		return new DataUser(usersByUCR.get(vect), vect);
	//	}


}
