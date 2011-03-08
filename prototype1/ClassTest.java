//package recommender;

import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;


public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

				
		Site s = new Site();
		try {
			ArrayList<ObjectId> users = Interprete.getUserList();
			for (int i=0;i<users.size();i++) {
				Recommendation reco = s.requestReco(new Request("recommandation/"+users.get(i)+"/www.google.com"));
				System.out.println(reco);
				//s.Feedback(new Request("feedback/"+users.get(i)+"/"+users.get(i/2)+"/false"));
				BasicDBObject query = new BasicDBObject("url",reco.url);
				
				BasicDBObject pageviewedbyuser = (BasicDBObject) Interprete.upages.findOne(query);
				System.out.println("themes:"+ pageviewedbyuser.get("themes"));
				
				DataVector utr = new DataVector(users.get(i));
				System.out.println(utr);
			}
			//s.maj(new Request("update/"));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
