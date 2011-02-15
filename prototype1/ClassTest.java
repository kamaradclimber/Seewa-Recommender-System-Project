
public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		
		Site s = new Site();
		try {
			s.maj(new Request("update/"));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
