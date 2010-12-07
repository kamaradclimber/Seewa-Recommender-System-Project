
import java.net.UnknownHostException;
import java.util.ArrayList;

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
			System.out.print("Ouverture de la base....");
			Mongo mongo = new Mongo( "138.195.76.136"  , 80 );
			db = mongo.getDB( "test" );
			System.out.println("[done]");
		}
		catch (UnknownHostException ex) {
			RecoException erreur = new RecoException(RecoException.ERR_CONNECTION_DB);
			System.out.println("Erreur :"+erreur.getCode());
			System.exit(1);
		}
	}
	

	static private DataVector db2DataVector(DBObject obj, Integer arrayId, String mongoID) {
		// prend un dbobject pour en creer un datavector
		DataVector vector = null;
		if (arrayId != null ) {
			vector = new DataVector(arrayId, mongoID);
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
		// Un cluster en base de donn�e est stock� avec un champ centroid
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
			String mongoID   =  (String) cluster.get("_id");
			centroid = Interprete.db2DataVector(cent, null,null);
			clusters.add(new DataCluster(null, centroid, new ArrayList<DataVector>(), mongoID));
		}
		return clusters;
		}
		catch (MongoException ex) {
			throw new RecoException(RecoException.ERR_DB_READING_CLUSTER);
		}
	}



	static public ArrayList<DataCluster> readClusters() throws RecoException {
		// Un cluster en base de donn�e est stock� avec un champ centroid et des liens vers les UTR
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
			String id   =  (String) cluster.get("_id");
			centroid = Interprete.db2DataVector(cent, null,null);

			BasicDBList utrs = (BasicDBList) cluster.get("utr_ids");
			ArrayList<DataVector> utrss =  new ArrayList<DataVector>();
			for (Object arrayId : utrs) {
				BasicDBObject query = new BasicDBObject();
				query.put("_id", arrayId);
				DBObject user = users.findOne(query, new BasicDBObject("utr",1)); //on reccupere seulemnent le champ utr
				DBObject userr = (DBObject) user.get("utr");
				String mongoID =(String) user.get("_id");
				utrss.add(Interprete.db2DataVector(userr, (Integer) arrayId, mongoID));
			}
			clusters.add(new DataCluster(null, centroid, utrss, id));
		}
		return clusters;
		}
		catch (MongoException ex) {
			throw new RecoException(RecoException.ERR_DB_READING_CLUSTER);
		}
	} 

	public static boolean writeClusters(ArrayList<DataCluster> clusters) throws RecoException {
		//renvoie j'ai r�ussi ou pas 
		//Interprete.clusters=clusters; // a quoi sert cette ligne ? FIXME
		try {
		DBCollection users = db.getCollection("clusters");
		for (DataCluster cluster : clusters) {
			BasicDBObject query = new BasicDBObject("_id",cluster.getArrayId());
			BasicDBObject clusterr = new BasicDBObject();
			clusterr.put("centroid", Interprete.dataVector2db(cluster.getCentroid())); //on rajoute la centroid
			BasicDBList vectors = new BasicDBList();
			for (DataVector utr : cluster) {
				vectors.add(utr.getMongoId()); //on ajoute l'id du user pour l'identifier 
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

	public static DataVector readUTR(String mongoID) throws RecoException { //TODO mettre un type un peu plus pr�cis pour l �d
		//renvoie l'UTR d'un user � partir d'un id de l'user
		try {
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id",mongoID); //preparation de la query
		DBObject user = users.findOne(query,new BasicDBObject("utr",1));
		DBObject utr =  (DBObject) user.get("utr"); //on caste TODO : faire un try..catch pour eviter les pblemes
		
		return Interprete.db2DataVector(utr, null, mongoID); //this data matters so on lui passe l'id qui va bien
		}
		catch (MongoException ex) {
			throw new RecoException(RecoException.ERR_DB_READING_UTR);
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

	public static DataUser getUser(DataVector utr) throws RecoException{
		try {
		//renvoie l'utilisateur qui correspond à l'UTR passé en argument
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id",utr.getMongoId()); //preparation de la query
		BasicDBObject user = (BasicDBObject) users.findOne(query);
		assert (utr.getArrayId() == (Integer) user.get("_id"));
		return new DataUser( user.get("name").toString(), utr, (String) utr.getMongoId());
		} catch (Exception e) {
			System.out.println("Heho ! ya une erreur, de toute facon il va y avoir un pointeur null exception dici peu");
			return null;
		}
	}
	



	
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
		while (results.hasNext()) // on rajoute tous les th�mes avec leur valeur d'UTR
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
