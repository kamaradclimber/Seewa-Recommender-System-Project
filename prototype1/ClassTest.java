package recommender;

import java.util.ArrayList;

import org.bson.types.ObjectId;


public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Site s = new Site();
		try {
			ArrayList<ObjectId> users = Interprete.getUserList();
			for (int i=0;i<users.size();i++) {
				System.out.println(s.requestReco(new Request("recommandation/"+users.get(i)+"/www.google.com")));
				//s.Feedback(new Request("feedback/"+users.get(i)+"/"+users.get(i/2)+"/false"));
			}
			s.maj(new Request("update/"));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
