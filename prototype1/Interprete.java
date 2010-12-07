
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
	
	static Mongo mongo = new Mongo( "138.195.76.136", 27017 );
	static DB db = mongo.getDB( "test" );
		
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
			throw new RecoException(RecoException.ERR_DB_READING_USER);
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
		
	
	static public void writeUTR(Object id, DataVector utr) {
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

	public static DataUser getUser(DataVector utr) {
		//renvoie l'utilisateur qui correspond à l'UTR passé en argument
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id",utr.getUserId()); //preparation de la query
		BasicDBObject user = (BasicDBObject) users.findOne(query);
		assert (utr.getId() == (Integer) user.get("_id"));
		return new DataUser(user.get("name").toString(), utr, (Integer) utr.getUserId());
	}
	
//	public static DataUser getUser(DataVector vect) {
//		
//		return new DataUser(usersByUCR.get(vect), vect);
//	}


	
	public static boolean updateUTR(){
		//recalculate the UTR of all users using Map-Reduce (twice)
		
		DBCollection upages = db.getCollection("upages");
		
		String map = "function()" +
		"{" +
			"this.themes.forEach(" +
				"function(z){" +
					"emit( {user: this.id , theme: z.name} , [this.pageRank , 1]  );" +
				"});" +
		"};";
		// Upages-> {userId-theme} , {pageRank-nb}
		
		String reduce = "function( key , values )" +
		"{" +
			"var sumPR = 0;"+
			"var nbPages = 0;"+
			"for (var i=0; i<values.length;i++){"+
				"sumPR += values[i][0];"+ //somme pageRank
				"nbPages += values[i][1];}"+ // nb pages
			"return [sumPR, nbPages];" +
		"};";
		// {userId-theme}, {somme(pageRank)-nb}
		
		upages.mapReduce(map, reduce, /*collection de result*/ "temporaryutr", /*query*/null);
		DBCollection temporaryutr = db.getCollection("temporaryutr");
		
		map = "function()" +
		"{" +
			"var utr = this.value[0]/this.value[1];" + // utr= somme(pageRank)/nb pages
					"emit( this.user , {this.theme : utr}  );" +
				"});" +
		"};";
		
		
		reduce = "function( key , values )" +
		"{" +
			"var utrSet={};"+
			"for (var i=0; i<values.length;i++){"+
				"for(var name in values[i]){"+
					"utrSet.push({name: values[i].get(name)});}} "+
			"return utrSet;" +
		"};";
		
		DBCursor results= temporaryutr.mapReduce(map, reduce, /*coll de result*/ null, /*query*/null).results();
		
		// on place les utr dans users
		DBCollection users = db.getCollection("users");
		DBObject aResult;
		BasicDBObject query = new BasicDBObject();
		BasicDBObject field = new BasicDBObject("UTR", 1);
		BasicDBObject anUTR;
		while (results.hasNext())
		{
			aResult = results.next();
			query.put("_id", aResult.get("_id"));
			
			anUTR= (BasicDBObject) aResult.get("value");
			anUTR.put("user", aResult.get("_id"));
			users.findAndModify(query, field, null, false ,anUTR, false, true); 
			query.clear();
		}
		
		return true;
	}
	
	public static boolean updateUTR(int userId){
		//recalculate the UTR of the User with '_id'= userId using Map-Reduce
		DBCollection upages = db.getCollection("upages");
		
		String map = "function()" +
		"{" +
			"this.themes.forEach(" +
				"function(z){" +
					"emit( z.name , {PR: this.pageRank , nb: 1}  );" +
				"});" +
		"};";
		// Upages-> theme , {pageRank-nb}
		
		String reduce = "function( key , values )" +
		"{" +
			"var sumPR = 0;"+
			"var nbPages = 0;"+
			"for (var i=0; i<values.length;i++){"+
				"sumPR += values[i].get('PR');"+ //somme pageRank
				"nbPages += values[i].get('nb');}"+ // nb pages
			"return {PR : sumPR, nb : nbPages};" +
		"};";
		// theme {somme(pageRank)-nb}
		
		DBCursor results = upages.mapReduce(map, reduce, /*collection de result*/ null, /*query*/query).results();
		
		DBObject aResult;
		
		
		BasicDBObject anUTR = new BasicDBObject("user", userId);
		BasicDBObject theme_val;
		while (results.hasNext()) // on rajoute tous les thèmes avec leur valeur d'UTR
		{
			aResult = results.next();
			theme_val = (BasicDBObject) aResult.get("value");
			double utrvalue = theme_val.getDouble("PR")/ theme_val.getInt("nb");
			anUTR.put((String) theme_val.get("_id"), utrvalue);// {theme,value}
		}
		
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("id", userId);
		BasicDBObject field = new BasicDBObject("UTR", 1);
		users.findAndModify(query, field, null, false ,anUTR, false, true); 
		return true;
	}
}
