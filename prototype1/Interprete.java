
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;


public class Interprete {

	//static ArrayList<DataCluster> clusters=null;
	//static Hashtable<String, DataVector> usersByNames= new Hashtable<String, DataVector>();
	//static Hashtable<DataVector, String> usersByUCR = new Hashtable<DataVector, String>();
	

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

	static private DataVector db2DataVector(DBObject obj, Integer id) {
		// prend un dbobject pour en creer un datavector
		DataVector vector = null;
		if (id != null ) {
			vector = new DataVector(id);	
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
	
	
	 static public ArrayList<DataCluster> readClustersCentroids(Request request) {
		 //this function should be used only for getting centroid (for research use only)
		 // Un cluster en base de donnée est stocké avec un champ centroid
		 //cette fonction est un peu optmisee pour la recherche quand on a besoin seulement des centroids
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
			centroid = Interprete.db2DataVector(cent, null);
			clusters.add(new DataCluster(id, centroid, new ArrayList<DataVector>()));
		}
		return clusters;
	}
	
	 static public ArrayList<DataCluster> readClusters(Request request) {
		 // Un cluster en base de donnée est stocké avec un champ centroid et des liens vers les UTR
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
			centroid = Interprete.db2DataVector(cent, null);
			
			BasicDBList utrs = (BasicDBList) cluster.get("utr_ids");
			ArrayList<DataVector> utrss =  new ArrayList<DataVector>();
			for (Object user_id : utrs) {
				BasicDBObject query = new BasicDBObject();
				query.put("_id", user_id);
				DBObject user = users.findOne(query, new BasicDBObject("utr",1)); //on reccupere seulemnent le champ utr
				DBObject userr = (DBObject) user.get("utr");
				utrss.add(Interprete.db2DataVector(userr, (Integer) user_id));
			}
			clusters.add(new DataCluster(id, centroid, utrss));
		}
		return clusters;
	} 
	 
	 
	public boolean writeClusters(ArrayList<DataCluster> clusters) {
		//renvoie j'ai réussi ou pas 
		//Interprete.clusters=clusters; // a quoi sert cette ligne ? FIXME
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
		
		
		return false;
	}

	public DataVector readUcr(Object id) { //TODO mettre un type un peu plus précis pour l íd
		//renvoie l'UTR d'un user à partir d'un id de l'user
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id",id); //preparation de la query
		DBObject user = users.findOne(query,new BasicDBObject("utr",1));
		DBObject utr =  (DBObject) user.get("utr"); //on caste TODO : faire un try..catch pour eviter les pblemes
		
		return Interprete.db2DataVector(utr, (Integer)id); //this data matters so on lui passe l'id qui va bien
	}
	
	static public void writeUcr(Object id, DataVector utr) {
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
