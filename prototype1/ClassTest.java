<<<<<<< HEAD
import java.util.Date;

=======
>>>>>>> a8fde2aa2a174690a3aab6c6fcda5f0f086b8e80
import org.bson.types.ObjectId;


public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		
		Date t = new Date();
		//DataUserNode2DB
		DataUserNode lambda = new DataUserNode(new ObjectId(t, 1));
		System.out.println(lambda.getId());
		
		DataUserNode friend = new DataUserNode(new ObjectId("ab033f4d90ad4d1418000000"));
		lambda.addFriend(new DataUserRelation(friend));
		
		DataUPage page = new DataUPage(new ObjectId(t, 2), lambda.getId(), 0.5, "www.cette_page_n_existe_pas.fr");
		
		lambda.addUPage(page);
		
		lambda.toString();

		Site s = new Site();
		try {
			s.requestReco(new Request("recommandation/ab033f4d90ad4d1408000000/www.google.com"));
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

}
