
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
		
		
		BasicDBList recommendersMongo = (BasicDBList) user.get("recommenders");
		ArrayList<DataUserRelation> recommenders = new ArrayList<DataUserRelation>();
		
		//TODO : la suite peut ptet etre am�lior� en regroupant tout dans une requ�te
		
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
		
		return usernode;
	}
	
	
	
	static protected DataUserNode db2DataUserNodeSimple(ObjectId mongoID) {
		
		
		DBCollection upages = db.getCollection("upages");
		BasicDBObject query = new BasicDBObject("user",mongoID);
		DBCursor pageviewedbyuser = upages.find(query);

		ArrayList<DataUPage> userupages = new ArrayList<DataUPage>();
		
		/* Cr�ation des DataUPages */
		
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
	
	static protected void modifyFeedback(ObjectId recommender_id , ObjectId receiver_id , boolean feedback) {
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id", receiver_id);
		DBObject user = coll.findOne(query);
		
		BasicDBList recommenders = (BasicDBList) user.get("recommenders");
		BasicDBObject recommender = (BasicDBObject) recommenders.get(recommender_id.toString());
		
		if (feedback == true) {
			int posFeedback = (Integer) recommender.get("posFeedback");
			recommender.put("posFeedback",posFeedback+1);
		}
		
		else {
			int negFeedback = (Integer) recommender.get("negFeedback");
			recommender.put("negFeedback",negFeedback-1);
		}
	}
	
	
	
	static protected void DataUserNode2db(DataUserNode user) {
		
		DBCollection coll = db.getCollection("users");
		BasicDBObject newUser = new BasicDBObject();
		newUser.put("_id",user.getId());
		newUser.put("name","Francis");
		
		BasicDBObject recommenders = new BasicDBObject();
		
		for (DataUserRelation relation : user.getFriends()) {
			BasicDBObject recommender = new BasicDBObject();
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
		
		BasicDBObject keys = new BasicDBObject();
		keys.put("_id", 1);
		
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
	

	}