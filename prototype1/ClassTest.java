

import java.util.Date;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//test de la fonction DataUserNode2DB
		
		System.out.print("creating user..");
		Date t = new Date();
		
		//on regarde l'user:
		ObjectId userId = new ObjectId("ab033f4d90ad4d1416000000");
		//il a 276 upages, 2 amis, pas de recommendeurs
		
		DataUserNode friend = new DataUserNode(new ObjectId("ab033f4d90ad4d1418000000"));
		
		DataUserNode lambda = new DataUserNode(userId);
		System.out.println(lambda.getId());
		
		
		lambda.addFriend(new DataUserRelation(friend));
		

		DataUPage page = new DataUPage(userId, lambda.getId(), 0.5, "www.cette_page_n_existe_pas.fr");
		//on rajoute une upage
		lambda.addUPage(page);
		
		
		System.out.println("[done]");
		System.out.println(lambda.toString());
		
		DBCollection users = Interprete.db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_id",userId);
		DBObject something = users.findOne(query);
		
		
		System.out.print("test de DataUserNode2DB...");
		Interprete.DataUserNode2db(lambda);
		DataUserNode lambda2 = Interprete.db2DataUserNodeSimple(userId);
		System.out.println(something.toString());
		System.out.println(lambda2.toString());
		
		System.out.println("test completed successfully");
		
//		
//		Site s = new Site();
//		try {
//			System.out.println(s.requestReco(new Request("recommandation/ab033f4d90ad4d1408000000/www.google.com")));
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
	}

}
