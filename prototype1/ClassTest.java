import org.bson.types.ObjectId;


public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Site s = new Site();
		try {
			System.out.println(s.requestReco(new Request("recommandation/ab033f4d90ad4d1408000000/www.google.com")));
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

}
