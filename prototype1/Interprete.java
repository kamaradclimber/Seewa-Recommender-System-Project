
import java.net.UnknownHostException;
import java.util.ArrayList;

import java.util.List;

import org.bson.types.ObjectId;


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
			db = mongo.getDB( "seewa1" );
			System.out.println("[done]");
		}
		catch (UnknownHostException ex) {
			ExceptionRecoNotValid erreur = new ExceptionRecoNotValid(ExceptionRecoNotValid.ERR_CONNECTION_DB);
			System.out.println("Erreur :"+erreur.getCode());
			System.exit(1);
		}
	}
	
	static protected DataUserNode db2DataUserNodeHard(ObjectId mongoID) {
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id",mongoID);
		DBObject user = coll.findOne(query);
		
		
		BasicDBList recommendersMongo = (BasicDBList) user.get("recommenders");
		ArrayList<DataUserRelation> recommenders = new ArrayList<DataUserRelation>();
		
		//TODO : la suite peut ptet etre amélioré en regroupant tout dans une requête
		
		for (Object recommender : recommendersMongo) {
			BasicDBObject recommender2 = (BasicDBObject) recommender;
			ObjectId _id = (ObjectId) recommender2.get("_id");
			double crossProbability = (Double) recommender2.get("crossProbability");
			int posFeedback = (Integer) recommender2.get("posFeedback");
			int negFeedback = (Integer) recommender2.get("negFeedback");
			
			DataUserNode usernode = db2DataUserNodeSimple(_id);
			DataUserRelation userrelation = new DataUserRelation(usernode,crossProbability,posFeedback,negFeedback);
			recommenders.add(userrelation);
		}
		
		DataUserNode usernode = db2DataUserNodeSimple(mongoID);
		usernode.setFriends(recommenders);
		
		return usernode;
	}
	
	static protected DataUserNode db2DataUserNodeSimple(ObjectId mongoID) {
		
		
		DBCollection upages = db.getCollection("upages");
		BasicDBObject query = new BasicDBObject("user",mongoID);
		DBCursor pageviewedbyuser = upages.find(query);

		ArrayList<DataUPage> userupages = new ArrayList<DataUPage>();
		
		/* Création des DataUPages */
		
		while (pageviewedbyuser.hasNext()) {
			DBObject upage = pageviewedbyuser.next();
			double pagerank = (Double) upage.get("pageRank");
			ObjectId id = (ObjectId) upage.get("_id");
			
			DataUPage dataupage = new DataUPage(id,pagerank);
			userupages.add(dataupage);
		}
		
		DataUserNode usernode = new DataUserNode(mongoID,userupages);
		return usernode;
	}
	
	
	static protected void DataUserNode2db(DataUserNode user) {
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id",user.getId());
		DBObject usermongo = coll.findOne(query);
		BasicDBList recommendersMongo = (BasicDBList) usermongo.get("recommenders");
		
		for (Object recommender : recommendersMongo) {
			BasicDBObject recommender2 = (BasicDBObject) recommender;
					
		}
			
	
	}
	

	static protected DataVector db2DataVector(DBObject obj, Integer arrayId, ObjectId mongoID) {
		// prend un dbobject pour en creer un datavector
		DataVector vector = null;
		if (arrayId != null ) {
			vector = new DataVector(arrayId, mongoID);
		} else if (mongoID != null) {
			{
				vector = new DataVector(0, mongoID);
			}
		} else {
			vector = new DataVector(false);	
		}
		for (String key : obj.keySet() ) {
			vector.put(key, new Float( (Double)obj.get(key))); 
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


	static public ArrayList<DataCluster> readClustersCentroids() throws ExceptionRecoNotValid {
		System.out.println("HEHO T SUR QUE CA AMRCHE COMME FONCTION");
		//this function should be used only for getting centroid (for research use only)
		// Un cluster en base de donnÔøΩe est stockÔøΩ avec un champ centroid
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
			ObjectId mongoID   =  (ObjectId) cluster.get("_id");
			centroid = Interprete.db2DataVector(cent, null,null);
			
//			//TODO : on doit aussi charger les userId pour trouver une personne a recommender!
//			//=>fait. On ne fait pas d'appel supplÔøΩmentaire ÔøΩ la BDD.
//			BasicDBList utrs = (BasicDBList) cluster.get("utr_ids");
//			ArrayList<DataVector> utrList =  new ArrayList<DataVector>();
//			
//			//Load user but without the data (only id)
//			for (ObjectId arrayId : utrs) {
//				
//				DataVector utr = new DataVector(0, arrayId);
//				utrList.add(utr);
//			}
			
			clusters.add(new DataCluster(0, centroid, new ArrayList<DataVector>(), mongoID));
		}
		return clusters;
		}
		catch (MongoException ex) {
			throw new ExceptionRecoNotValid(ExceptionRecoNotValid.ERR_DB_READING_CLUSTER);
		}
	}



	static public ArrayList<DataCluster> readClusters() throws ExceptionRecoNotValid {
		// Un cluster en base de donnÔøΩe est stockÔøΩ avec un champ centroid et des liens vers les UTR
		try {
		DBCollection clusterCollection = db.getCollection("clusters");
		DBCollection userCollection = db.getCollection("users");
		DBCursor cursor = clusterCollection.find();
		ArrayList<DataCluster> clusters = new ArrayList<DataCluster>();

		DBObject cluster= null;
		while(cursor.hasNext()) {
			cluster = cursor.next();
			//init of the centroid :
			DataVector centroid = new DataVector(false);
			DBObject cent = (DBObject) cluster.get("centroid");
			ObjectId id   =  (ObjectId) cluster.get("_id");
			centroid = Interprete.db2DataVector(cent, null,null);
			
			//add the UTR vectors :
			BasicDBList usrs = (BasicDBList) cluster.get("usr_ids");
			ArrayList<DataVector> utrList =  new ArrayList<DataVector>();
			BasicDBObject query = new BasicDBObject();
			ObjectId mongoId;
			for (Object mongoId_ : usrs) {
				mongoId = (ObjectId) mongoId_;
				query.put("_id",(ObjectId) mongoId);
				BasicDBObject user = (BasicDBObject) userCollection.findOne(query /*,new BasicDBObject("utr",1)*/); //on reccupere seulement le champ utr
				BasicDBObject utr = (BasicDBObject) user.get("utr");
				ObjectId mongoID =(ObjectId) user.get("_id");
				utrList.add(Interprete.db2DataVector((DBObject) utr.get("utrs"), null, mongoID));
				query.clear();
			}
			clusters.add(new DataCluster(-1, centroid, utrList, id));
			utrList.clear();
		}
		return clusters;
		}
		catch (MongoException ex) {
			throw new ExceptionRecoNotValid(ExceptionRecoNotValid.ERR_DB_READING_CLUSTER);
		}
	} 

	public static boolean writeClusters(ArrayList<DataCluster> clusters) throws ExceptionRecoNotValid {
		//renvoie j'ai rÔøΩussi ou pas 
		//Interprete.clusters=clusters; // a quoi sert cette ligne ? FIXME
		//=> cf ligne 13 static: les donnÔøΩes ÔøΩtaient enrgistrÔøΩes dans la classe. En effet, cela ne sert plus a rien
		try {
		DBCollection clusterCollection = db.getCollection("clusters");
		for (DataCluster cluster : clusters) {
			BasicDBObject query = new BasicDBObject("_id",cluster.getMongoId());
			BasicDBObject clusterr = new BasicDBObject();
			clusterr.put("centroid", dataVector2db(cluster.getCentroid())); //on rajoute la centroid
			BasicDBList idVector = new BasicDBList();
			for (DataVector utr : cluster) {
				idVector.add(utr.getMongoId()); //on ajoute l'id du user pour l'identifier 
			}
			clusterr.put("usr_ids", idVector);
			clusterCollection.update(query, clusterr ,true,false);
			
			}


			return true;
		}
		catch (MongoException ex) {
			throw new ExceptionRecoNotValid(ExceptionRecoNotValid.ERR_WRITING_CLUSTER);
		}
	}

	public static DataVector readUTR(ObjectId mongoID) throws ExceptionRecoNotValid { //TODO mettre un type un peu plus prÔøΩcis pour l ÔøΩd
		//renvoie l'UTR d'un user ÔøΩ partir d'un id de l'user
		try {
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id",mongoID); //preparation de la query
		DBObject user = users.findOne(query,new BasicDBObject("utr",1));
		DBObject utr =  (DBObject) ((BasicDBObject) user.get("utr")).get("utrs"); //on caste TODO : faire un try..catch pour eviter les pblemes
		
		return Interprete.db2DataVector(utr, null, mongoID); //this data matters so on lui passe l'id qui va bien
		}
		catch (MongoException ex) {
			throw new ExceptionRecoNotValid(ExceptionRecoNotValid.ERR_DB_READING_UTR);
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

	
	public static DataUser getUser(DataVector utr) throws ExceptionRecoNotValid{
		try {
		//renvoie l'utilisateur qui correspond √† l'UTR pass√© en argument
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id",utr.getMongoId()); //preparation de la query
		BasicDBObject fields = new BasicDBObject("_id", 1);
		fields.put("name", 1);
		
		BasicDBObject user = (BasicDBObject) users.findOne(query, fields);
		
		assert (utr.getMongoId() == user.get("_id"));
		return new DataUser( user.get("name").toString(), utr, utr.getMongoId() );
		
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			System.out.println("Heho ! ya une erreur, de toute facon il va y avoir un pointeur null exception dici peu");
			return null;
		}
	}
	

	public static boolean updateUTR2() {
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject(); 
		BasicDBObject fields = new BasicDBObject("_id", 1); // on ne veut que l'id
		DBCursor results =  (DBCursor)users.find(query, fields);
		
		DBObject aResult; //stockage temporaire de lelement en cours dans la boucle
		
		while (results.hasNext()) // on rajoute tous les thËmes avec leur valeur d'UTR
		{
			aResult = results.next();		
			ObjectId id  = (ObjectId) aResult.get("_id");
			if (!Interprete.updateUTR(id)) { 
				return false; // il faudrait lever une exception non ?
			}
		}
		return true;
	}

	public static boolean updateUTR(){
		//recalculate the UTR of all users using Map-Reduce (twice)
		
		DBCollection upages = db.getCollection("upages");
		
		String map2 = "function()" +
		"{" +
		"'var that = this;"+
			"this.themes.forEach(" +
				"function(z){" +
					"emit(hfgjerhgjer, {that.user , z.name ,PR : that.pageRank ,nb : 1}  );" +
				"});" +
		"};";
		// Upages-> {userId-theme} , {pageRank-nb}
		
		String reduce2 = "function( key , values )" +
		"{" +
			"var sumPR = 0;"+
			"var nbPages = 0;"+
			"for (var i=0; i<values.length;i++){"+
				"sumPR += values[i]['PR'];"+ //somme pageRank
				"nbPages += values[i]['nb'];}"+ // nb pages
			"return {PR: sumPR, nb :nbPages};" +
		"};";
		// {userId-theme}, {somme(pageRank)-nb}
		
		String map = "function()" +
		"{" +
			"var that = this;"+
			"this.themes.forEach(" +
				"function(z){" +
					"emit( that.user+'qwerty'+z.name,{user: that.user, theme:z.name, PR: that.pageRank  , nb: 1}  );" +
				"});" +
		"};";
		/*this.pageRank*/
		// Upages-> theme , {pageRank-nb}
		
		String reduce = "function( key , values )" +
		"{" +
			"var sumPR = 0;"+
			"var nbPages = 0;"+
			"var user;"+
			"var theme;"+
			"for (var i=0; i<values.length;i++){"+
				"user = values[i]['user'];" +
				"theme = values[i]['theme'];"+
				"sumPR += values[i]['PR'];"+ //somme pageRank
				"nbPages += values[i]['nb'];}"+ // nb pages
			"return {user: user,theme :theme  ,PR : sumPR, nb : nbPages};" +
		"};";
		
		upages.mapReduce(map, reduce, /*collection de result*/ "temporaryutr", /*query*/null);
		System.out.println("premier mapreduce fini");
		DBCollection temporaryutr = db.getCollection("temporaryutr");
		

		map ="function() {" +
				"emit(" +
				"this.value['user'], {nb :this.value['nb']}" +
				");" +
				"}";
		reduce = "function(key,values) {" +
				"var nbThemeAll = 0;" +
				"for (var i=0; i<values.length;i++) {" +
					"nbThemeAll += values[i]['nb'];" +
					"}" +
				"return {nb : nbThemeAll};" +
				"}";
		// on a calcule lenombre total doccurence de theme parmi les updages pour chaque utilisateur
		temporaryutr.mapReduce(map, reduce, /*collection de result*/ "temporaryutr2", /*query*/null);	
		System.out.println("Second mapreduce fini");
		DBCollection temporaryutr2 = db.getCollection("temporaryutr2");
		
//		map = "function()" +
//		"{" +
//			"var userId = this.value['user'];" +
////			"var nb=  db.temporary2.findOne({_id: userId}).value['nb'];" +
//			"var nb=1;" +
//			"var utr = this.value['PR']/ nb;" + // utr= somme(pageRank)/nb pages
//					"emit( this.user , {this.theme : utr}  );" +
//				"});" +
//		"};";
		
		map ="function() {" +
		"emit(" +
		"this.value['user'], {this.value['theme'] :this.value['nb']}" +
		");" +
		"}";
		reduce = "function(key,values) {" +
		"var utrs = {};" +
		"for (var i=0; i<values.length;i++) {" +
		"for (themeName in values[i]){" +
			"utrs[themeName]= values[i][name];" +
			"}" +
			"}" +
		"return utrs;" +
		"}";
		
		DBCursor results= temporaryutr.mapReduce(map, reduce, /*coll de result*/ null, /*query*/null).results();
		System.out.println("bon on a depasse le troisemem faut verifier maitenant que les resultats corespondent a ce qunon attendait");
		// on place les utr dans users
		DBCollection users = db.getCollection("users");
		DBObject aResult;
		BasicDBObject query = new BasicDBObject();
		BasicDBObject field = new BasicDBObject("utr", 1);
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
	
	public static boolean updateUTR(ObjectId userId) {
		//recalculate the UTR of the User with '_id'= userId using Map-Reduce
		DBCollection upages = db.getCollection("upages");
		
		String map = "function()" +
		"{" +
			"var that = this;"+
			"this.themes.forEach(" +
				"function(z){" +
					"emit( z.name , {PR: that.pageRank  , nb: 1}  );" +
				"});" +
		"};";
		/*this.pageRank*/
		// Upages-> theme , {pageRank-nb}
		
		String reduce = "function( key , values )" +
		"{" +
			"var sumPR = 0;"+
			"var nbPages = 0;"+
			"for (var i=0; i<values.length;i++){"+
				"sumPR += values[i]['PR'];"+ //somme pageRank
				"nbPages += values[i]['nb'];}"+ // nb pages
			"return {PR : sumPR, nb : nbPages};" +
		"};";
		// theme {somme(pageRank)-nb}
		BasicDBObject query = new BasicDBObject("user", userId);
		DBCursor results = upages.mapReduce(map, reduce, /*collection de result*/ null, /*query*/query).results();
		
		DBObject aResult; //stockage temporaire de lelement en cours dans la boucle
	
		BasicDBObject anUTR = new BasicDBObject("user", userId);
		BasicDBObject resultatReduce; // objet temporaire contenant theme et valeur

		anUTR.put("utrs", new BasicDBObject()); // on rajoute une entre UTR
		int nbThemeAll =0;
		while (results.hasNext()) // on rajoute tous les thÔøΩmes avec leur valeur d'UTR
		{
			aResult = results.next();
//			System.out.println(aResult);		
			resultatReduce = (BasicDBObject) aResult.get("value");
			double utrvalue = resultatReduce.getDouble("PR"); /// resultatReduce.getInt("nb");
			nbThemeAll += resultatReduce.getInt("nb");
			((BasicDBObject)anUTR.get("utrs") ).put( (String)aResult.get("_id"), utrvalue   );// {theme,value}
		}
		BasicDBObject utrs = (BasicDBObject) anUTR.get("utrs");
		for (String themeName: utrs.keySet()) {
			utrs.put(themeName, utrs.getDouble(themeName) / nbThemeAll);
		}
		anUTR.put("utrs", utrs);
		
		DBCollection users = db.getCollection("users");
		query.clear();
		
		query.put("_id", userId );

		BasicDBObject update= new BasicDBObject("$set",new BasicDBObject("utr",anUTR));
		
		users.update(query, update); 
		return true;
	}
}
