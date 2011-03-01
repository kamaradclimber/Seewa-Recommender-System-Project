package recommender;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;

import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;


import com.mongodb.*;


public class Interprete {

static DB db;
static DBCollection users;
static DBCollection upages;
static DBCollection feedbacks;


	static {
		try {
			System.out.print("Ouverture de la base....");
			Mongo mongo = new Mongo( "127.0.0.1"  , 81 );
			db = mongo.getDB( "seewaAnon" );
			System.out.println("[done]");
			users = db.getCollection("users");
			upages = db.getCollection("upages");
			feedbacks = db.getCollection("feedback");
		}
		catch (UnknownHostException ex) {
			ExceptionRecoNotValid erreur = new ExceptionRecoNotValid(ExceptionRecoNotValid.ERR_CONNECTION_DB);
			System.out.println("Erreur :"+erreur.getCode());
			erreur.printStackTrace();
			System.exit(1);
		}
	}
	
	static protected DataUserNode db2DataUserNodeHard(ObjectId mongoID) {
		BasicDBObject query = new BasicDBObject("_id",mongoID);
		DBObject user = users.findOne(query);
		
		
		BasicDBObject recommendersMongo = (BasicDBObject) user.get("recommenders");
		
		DataUserNode usernode = db2DataUserNodeSimple(mongoID);
		ArrayList<DataUserRelation> recommenders = new ArrayList<DataUserRelation>();
		//gestion du cas où l útlisateur n'a pas de de recommenders : on fait semblant quelle est vide et on le signale pour dire qu'il ne peut pas avoir de recommdation
		if (recommendersMongo ==null) {
			//He must be a new user. We create him a new pool of recommenders
			usernode.initRecommenders();
			Interprete.DataUserNode2db(usernode);
		}
		else {
			
			//TODO : la suite peut ptet etre amélioré en regroupant tout dans une requête		
			for (String recommender : recommendersMongo.keySet()) {
				ObjectId _id = (ObjectId) ((BasicDBObject) recommendersMongo.get(recommender)).get("_id");
				double crossProbability = (Double) ((BasicDBObject) recommendersMongo.get(recommender)).get("crossProbability");
				int posFeedback = (Integer) ((BasicDBObject) recommendersMongo.get(recommender)).get("posFeedback");
				int negFeedback = (Integer) ((BasicDBObject) recommendersMongo.get(recommender)).get("negFeedback");
				
				DataUserNode friendnode = db2DataUserNodeSimple(_id);
				DataUserRelation userrelation = new DataUserRelation(friendnode,crossProbability,posFeedback,negFeedback);
				recommenders.add(userrelation);
			}
			usernode.setFriends(recommenders);
		}
		
			
		return usernode;
	}
	
	
	static protected BasicDBObject rustinePourCreerUnPoolInitialDeRecommender(ObjectId userId) {
		//la technique est simple : on va lui ajouter ses amis + un inconnu (au cas où il n'a pas d'ami)
		System.out.println("Deprecated ??");
		
		BasicDBObject query = new BasicDBObject("_id",userId);
		
		BasicDBObject recommendeurs = new BasicDBObject();
		BasicDBObject user = (BasicDBObject) users.findOne(query);
		BasicDBList friends = (BasicDBList) user.get("allFriends");
		
		if (friends ==null ) {
			//pas de bol il n'a meme pas dami
			friends = new BasicDBList();
			BasicDBObject thisOne= (BasicDBObject) users.findOne(new BasicDBObject());
			try { if (thisOne==null)
					throw new Exception("le monde est contre moi je ne peux vraiment rien faire");
				} catch (Exception e) {
					e.printStackTrace();
				}
			thisOne.put("id", thisOne.get("_id"));
			friends.add(thisOne);
		}
		
		for(Object f : friends) {
			BasicDBObject friend = (BasicDBObject) f;
			BasicDBObject flyweight = new BasicDBObject();
			flyweight.put("_id", friend.get("id"));
			flyweight.put("crossProbability", 0.5);
			flyweight.put("posFeedback", 0);
			flyweight.put("negFeedback", 3); //TODO rechanger cette valeur a 0 !
			recommendeurs.put(friend.get("id").toString(), flyweight);
		}
		
		user.put("recommenders", recommendeurs);
		//users.update(query, user); //bon cest pas super efficace
		return recommendeurs;

	}
	
	

	static protected DataUserNode db2DataUserNodeSimple(ObjectId userId) {
		
		BasicDBObject query = new BasicDBObject("user",userId);
		DBCursor pageviewedbyuser = upages.find(query);

		ArrayList<DataUPage> userupages = new ArrayList<DataUPage>();
		
		
		while (pageviewedbyuser.hasNext()) {
			DBObject upage = pageviewedbyuser.next();
			double pagerank;
			try {
				pagerank = (Double) upage.get("pageRank");
			}
			catch (Exception ex) {
				pagerank = 0; //Si le pagerank est � 0 ou n'existe pas, on le met � 0
			}
			ObjectId id = (ObjectId) upage.get("_id");
			String url =(String) upage.get("url");
			DataUPage dataupage = new DataUPage(id, userId, pagerank,url);
			userupages.add(dataupage);
		}
		
		DataUserNode usernode = new DataUserNode(userId,userupages);
		return usernode;
	}
	
	static protected void modifyFeedback(ObjectId recommender_id , ObjectId receiver_id , boolean feedback) {
		BasicDBObject query = new BasicDBObject("_id", receiver_id);
		DBObject user = users.findOne(query);
		
		BasicDBObject recommenders = (BasicDBObject) user.get("recommenders");
		
		BasicDBObject recommender = (BasicDBObject) recommenders.get(recommender_id.toString());
		if (recommender ==null ) {
			System.out.println("on a un feedback qui parle dutlisateur qui ne sont pas en relation...cest moche (ou alors ce sont des données de test)");
			return;
		}
		
		if (feedback == true) {
			int posFeedback = (Integer) recommender.get("posFeedback");
			
			recommender.put("posFeedback",posFeedback+1);
		}
		
		else {
			int negFeedback = (Integer) recommender.get("negFeedback");
			
			recommender.put("negFeedback",negFeedback+1);
		}
		recommenders.put(recommender_id.toString(), recommender);
		user.put("recommenders",recommenders);
		
		users.update(query, user,true,false);
		
	}
	
	
	static protected void updateRecommendersInDb(ObjectId receiver_id, ArrayList<DataUserRelation> recoToAdd, ArrayList<DataUserRelation> recoToRemove) {
		BasicDBObject query = new BasicDBObject("_id", receiver_id);
		DBObject userMongo = users.findOne(query);
		
		if (userMongo == null) {
			System.out.println("You are creating a new user in the database... are you really sure ?");
			System.out.println("his Objectid is "+receiver_id);
			System.out.println("you should not ignore that warning, unless you are creating some random data");
		}
		
		BasicDBObject recommendersMongo = (BasicDBObject) userMongo.get("recommenders");
		for (DataUserRelation toRemove : recoToRemove) {
			recommendersMongo.remove(toRemove.getRecommender().getId().toString());
		}
		for (DataUserRelation toAdd : recoToAdd) {
			BasicDBObject newRecommender = new BasicDBObject();
			newRecommender.put("_id",toAdd.getRecommender().getId());
			recommendersMongo.put(toAdd.getRecommender().getId().toString(),newRecommender);
		}
		userMongo.put("recommenders", recommendersMongo);
		users.update(query, userMongo,true,false);
		
	}

	//fonction testée, marche correctement.
	static protected void DataUserNode2db(DataUserNode user) {
		//cette fonction ecrase tous les fields qui sont present dans lobjet user
		BasicDBObject query = new BasicDBObject("_id", user.getMongoId());
		DBObject userMongo = users.findOne(query);
		
		if (userMongo==null) {
			System.out.println("You are creating a new user in the database... are you really sure ?");
			System.out.println("his Objectid is "+user.getMongoId());
			System.out.println("you should not ignore that warning, unless you are creating some random data");
			userMongo = new BasicDBObject();
			userMongo.put("_id", user.getMongoId());
		}
		
		
		BasicDBObject updatedRecommenders = new BasicDBObject();

		for (DataUserRelation friend : user.getRecommandeurs()) {
			BasicDBObject recommender = new BasicDBObject();
			recommender.put("_id", friend.getRecommender().getMongoId());
			recommender.put("crossProbability", friend.getCrossProbability());
			recommender.put("posFeedback", friend.getPosFeedback());
			recommender.put("negFeedback", friend.getNegFeedback());
			updatedRecommenders.put(friend.getRecommender().getId().toString(), recommender);
		}
		if (! (updatedRecommenders.size()==0)) { 
			userMongo.put("recommenders", updatedRecommenders);
		}
		users.update(query, userMongo,true,false);
	}
	
	//OK
	public static ArrayList<ObjectId> getUserList() {
		BasicDBObject keys = new BasicDBObject("_id",1);
		
		DBCursor cursor = users.find(new BasicDBObject(), keys);
		ArrayList<ObjectId> results = new ArrayList<ObjectId>();
		DBObject user= null;
		while(cursor.hasNext()) {
			user = cursor.next();
			results.add((ObjectId)user.get("_id") );
		}
		return results;
			
	
	}



	public static void setCrossProbability(ObjectId user_Id, ObjectId recommander_id, double crossProbability) {

		BasicDBObject fields = new BasicDBObject();
		fields.put("recommenders", 1);
		
		BasicDBObject query = new BasicDBObject();
		query.put("_id", user_Id);
		
		BasicDBObject user = (BasicDBObject)users.findOne(query,fields);
		if (user==null) {System.out.println("Something went wrong ! le user est null (le programmeur aussi)");} //TODO lever une exception
		((BasicDBObject)((BasicDBObject)user.get("recommenders")).get(recommander_id.toString())).put("crossProbability",crossProbability);
		users.update(query, user,true,false);
		//TODO verifier quon ecrase pas les donnees
		
	}
	
	public static void DataUPage2db(DataUPage p) {
		System.out.println("Attention : on ecrase les pages deja existantes ?");
		BasicDBObject o = new BasicDBObject();
		o.put("_id", p.getMongoId());
		o.put("pageRank", p.pageRank);
		o.put("url", p.getUrl());
		o.put("user", p.getUserId());
		DBObject obj = new BasicDBObject();
		obj.put("_id", p.getMongoId());
		
		upages.update(
				obj,
				(DBObject)o, 
				true, /* i want to create the object if it doesnt exist*/
				false /* do i want to update multiple items : no */
				);
	}



	public static ArrayList<DataFeedBack> getFeedback() {
		ArrayList<DataFeedBack> feedbackList = new ArrayList<DataFeedBack>();
		
		DBCursor cursor = feedbacks.find(new BasicDBObject());
		DBObject feedback= null;
		while(cursor.hasNext()) {
			feedback = cursor.next();
			ObjectId objectid = (ObjectId)feedback.get("_id");
			ObjectId recoGiver = (ObjectId)feedback.get("recoGiver");
			ObjectId recoReceiver = (ObjectId)feedback.get("recoReceiver");
			Boolean clicked = (Boolean)feedback.get("clicked");
			feedbackList.add(new DataFeedBack(objectid, clicked, recoGiver, recoReceiver));
		}
		feedbacks.drop();
		return feedbackList;
	}


	public static void setFeedBack(DataFeedBack f) {
		BasicDBObject o = new BasicDBObject();
		if (f.getMongoId()!=null)
			o.put("_id", f.getMongoId());
		o.put("recoGiver", f.recoGiver());
		o.put("recoReceiver", f.recoReceiver());
		o.put("clicked", f.clicked());
		
		feedbacks.insert(o);
		
	}
	
	public static void generateRandomBDD(int nbUser, int nbUPages, int nbPages)
	{
		ArrayList<DataUserNode> users = new ArrayList<DataUserNode>();
		Date t = new Date();
		for (int i=0; i<nbUser; i++) {
			String nom = "user"+i;
			users.add( new DataUserNode(new ObjectId(t, i),new ArrayList<DataUPage>()));
		}
		
		int var;
		for (int i=0; i<nbUser; i++){
			for (int j=0; j< 2 /*nbFriends*/ ; j++)
			{
				var= (int) Math.floor(Math.random()*(nbUser-1));//nbUser-1 possible!
				if (var>=i) var++;//pr eviter d'etre ami avec soi-meme
				users.get(i).addFriend(new DataUserRelation(users.get(var), 0, 0, 0));
			}
			DataUserNode2db(users.get(i));
		}
	
		ArrayList<String> urls = new ArrayList<String>();
		for (int i=0; i<nbPages; i++)
		{
			urls.add( "www.page"+i+".com");
		}

		//Create upages :
		
		int var1;
		int var2;
		t = new Date();
		for (int i = 0; i<nbUPages; i++) {
			var1 = (int) Math.floor(Math.random()*nbUser);
			var2 = (int) (Math.floor(Math.random()*Math.floor(nbPages)));
			System.out.println(var1+"/" +users.size()+" -- "+ var2 +"/" +urls.size());
			System.out.println(users.get(var1)+" // "+ urls.get(var2));
			DataUPage2db( new DataUPage(new ObjectId(t, i),users.get(var1).getId(), Math.random(),urls.get(var2)));
		}	

	}


	public static ArrayList<DataUserRelation> getSocialFriends(
			DataUserNode user) {
		DBObject query = new BasicDBObject("_id", user.getMongoId());
		BasicDBList friendList = (BasicDBList) ((BasicDBObject) users.findOne(query, new BasicDBObject("allFriends",1))).get("allFriends");
		ArrayList<DataUserRelation> socialFriends = new ArrayList<DataUserRelation>();
		if (friendList == null) { friendList = new BasicDBList();} // pour eviter le bug si allFriend nest pas defini dans ce user
		for (Object friendObject : friendList)
		{
			
			BasicDBObject friend = (BasicDBObject) friendObject;
			
			
			ObjectId tmp = (ObjectId) friend.get("_id");
			if (tmp ==null) {
				tmp =(ObjectId) friend.get("id");
				assert (tmp != null); // la ca serait vraiment du vice !
			}
			DataUserNode friendNode = db2DataUserNodeSimple( tmp);
			socialFriends.add(new DataUserRelation(friendNode, 0, 0, 0));
		}
		return socialFriends;
	}
	
}
	
