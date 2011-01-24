
import java.net.UnknownHostException;
import java.util.ArrayList;

import java.util.List;

import org.bson.BasicBSONObject;
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
		
		
		BasicDBObject recommendersMongo = (BasicDBObject) user.get("recommenders");
		System.out.println(recommendersMongo);
		ArrayList<DataUserRelation> recommenders = new ArrayList<DataUserRelation>();
		
		//TODO : la suite peut ptet etre amï¿½liorï¿½ en regroupant tout dans une requï¿½te
		
		for (String recommender : recommendersMongo.keySet()) {
			//BasicDBObject recommender2 = (BasicDBObject) recommender;
			ObjectId _id = (ObjectId) ((BasicBSONObject) recommendersMongo.get(recommender)).get("_id");
			double crossProbability = (Double) ((BasicBSONObject) recommendersMongo.get(recommender)).get("crossProbability");
			int posFeedback = (Integer) ((BasicBSONObject) recommendersMongo.get(recommender)).get("posFeedback");
			int negFeedback = (Integer) ((BasicBSONObject) recommendersMongo.get(recommender)).get("negFeedback");
			
			DataUserNode usernode = db2DataUserNodeSimple(_id);
			DataUserRelation userrelation = new DataUserRelation(usernode,crossProbability,posFeedback,negFeedback);
			recommenders.add(userrelation);
		}
		
		DataUserNode usernode = db2DataUserNodeSimple(mongoID);
		usernode.setFriends(recommenders);
		
		System.out.println(recommenders.get(0).toString());
		
		return usernode;
	}
	
	
	
	static protected DataUserNode db2DataUserNodeSimple(ObjectId mongoID) {
		
		/*
		 * en attente de la rŽponse du bugtracker
		 * 
		DBCollection upages = db.getCollection("upages");
		BasicDBObject query = new BasicDBObject("user",mongoID);
		DBCursor pageviewedbyuser = upages.find();

		ArrayList<DataUPage> userupages = new ArrayList<DataUPage>();
		
		
		
		for(DBObject upage : pageviewedbyuser) {
		while (pageviewedbyuser.hasNext()) {
			DBObject upage = pageviewedbyuser.next();
			double pagerank = (Double) upage.get("pageRank");
			ObjectId id = (ObjectId) upage.get("_id");
			
			DataUPage dataupage = new DataUPage(id,pagerank);
			userupages.add(dataupage);
		}
		*/
		
		DataUPage jeanMichLeFigaro= new DataUPage(new ObjectId(), 0.8, "www.lefigaro.fr");
		DataUPage jeanMichLEquipe= new DataUPage(new ObjectId(), 0.5, "www.lï¿½quipe.fr");
		DataUPage jeanMichLinux= new DataUPage(new ObjectId(), 0.1, "www.linux.org");
		
		DataUPage leGeekLinux= new DataUPage(new ObjectId(), 0.8, "www.linux.org");
		DataUPage leGeekTechCrunch= new DataUPage(new ObjectId(), 0.95, "www.techcrunch.com");
		DataUPage leGeekOpLib= new DataUPage(new ObjectId(), 0.6, "www.opinionlibre.fr");
		DataUPage leGeekLeMonde= new DataUPage(new ObjectId(), 0.01, "www.lemonde.fr");
		
		DataUPage jeanJauresLeMonde= new DataUPage(new ObjectId(), 0.5, "www.lemonde.fr");
		DataUPage jeanJauresLeFigaro= new DataUPage(new ObjectId(), 0.3, "www.lefigaro.fr");
		DataUPage jeanJauresLEquipe= new DataUPage(new ObjectId(), 0.6, "www.lï¿½quipe.fr");
		DataUPage jeanJauresLHuma= new DataUPage(new ObjectId(), 0.9, "www.lhumanitï¿½.fr");
		
		ArrayList<DataUPage> userupages = new ArrayList<DataUPage>();
		
		ArrayList<DataUPage> jeanMichUPage= new ArrayList<DataUPage>();
		jeanMichUPage.add(jeanMichLinux);
		jeanMichUPage.add(jeanMichLeFigaro);
		//jeanMichUPage.add(jeanMichLeMonde);
		jeanMichUPage.add(jeanMichLEquipe);
		
		ArrayList<DataUPage> leGeekUPage= new ArrayList<DataUPage>();
		leGeekUPage.add(leGeekLinux);
		leGeekUPage.add(leGeekOpLib);
		leGeekUPage.add(leGeekTechCrunch);
		leGeekUPage.add(leGeekLeMonde);
		
		ArrayList<DataUPage> jeanJauresUPage= new ArrayList<DataUPage>();
		jeanJauresUPage.add(jeanJauresLeFigaro);
		jeanJauresUPage.add(jeanJauresLeMonde);
		jeanJauresUPage.add(jeanJauresLEquipe);
		jeanJauresUPage.add(jeanJauresLHuma);
		
		userupages.addAll(jeanJauresUPage);
		userupages.addAll(leGeekUPage);
		userupages.addAll(jeanMichUPage);
		
		DataUserNode usernode = new DataUserNode(mongoID,userupages);
		usernode.setName("Bob");
		return usernode;
	}
	
	static protected void modifyFeedback(ObjectId recommender_id , ObjectId receiver_id , boolean feedback) {
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id", receiver_id);
		DBObject user = coll.findOne(query);
		
		BasicDBObject recommenders = (BasicDBObject) user.get("recommenders");
		BasicDBObject recommender = (BasicDBObject) recommenders.get(recommender_id.toString());
		System.out.println(recommender);
		
		if (feedback == true) {
			int posFeedback = (Integer) recommender.get("posFeedback");
			System.out.println(posFeedback);
			recommender.put("posFeedback",posFeedback+1);
		}
		
		else {
			int negFeedback = (Integer) recommender.get("negFeedback");
			System.out.println(negFeedback);
			recommender.put("negFeedback",negFeedback+1);
		}
		recommenders.put(recommender_id.toString(), recommender);
		user.put("recommenders",recommenders);
		BasicDBObject query2 = new BasicDBObject("_id", receiver_id);
		coll.findAndModify(query2, user);
		
	}
	
	
	
	static protected void DataUserNode2db(DataUserNode user) {
		
		DBCollection coll = db.getCollection("users");
		BasicDBObject newUser = new BasicDBObject();
		newUser.put("_id",user.getId());
		newUser.put("name","Francis");
		
		BasicDBObject recommenders = new BasicDBObject();
		
		for (DataUserRelation relation : user.getFriends()) {
			BasicDBObject recommenderData = new BasicDBObject();
			recommenderData.put("_id", relation.getFriend().getId());
			recommenderData.put("crossProbability", relation.getCrossProbability());
			recommenderData.put("posFeedback", relation.getNegFeedback());
			recommenderData.put("negFeedback", relation.getPosFeedback());
			
			recommenders.put(relation.getFriend().getId().toString(),recommenderData);
		}
		
		newUser.put("recommenders",recommenders);
		
		BasicDBObject query = new BasicDBObject("_id", user.getId());
		coll.findAndModify(query, newUser);
		
	
	}



	public static ArrayList<ObjectId> getUserList() {
		DBCollection coll = db.getCollection("users");
		
		
		DBCursor cursor = coll.find(new BasicDBObject(), keys);
		ArrayList<ObjectId> results = new ArrayList<ObjectId>();
		DBObject user= null;
		while(cursor.hasNext()) {
			user = cursor.next();
			System.out.println(user);
			results.add((ObjectId)user.get("_id") );
		}
		return results;
			
	
	}



	public static void setCrossProbability(ObjectId user_Id, ObjectId recommander_id,
			double crossProbability) {

		DBCollection coll = db.getCollection("users");
		

		BasicDBObject fields = new BasicDBObject();
		fields.put("recommenders", 1);
		
		BasicDBObject query = new BasicDBObject();
		query.put("_id", user_Id);
		
		BasicDBObject user = (BasicDBObject)coll.findOne(query,fields);
		System.out.println(user);
		
		((BasicDBObject)((BasicDBObject)user.get("recommenders")).get(recommander_id.toString())).put("crossProbability",crossProbability);
		
		System.out.println(user);
		coll.findAndModify(query, user);
		
	}
}
	
