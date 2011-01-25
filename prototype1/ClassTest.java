import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import org.bson.types.ObjectId;

import com.mongodb.DBCollection;




public class ClassTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		
		ArrayList<ObjectId> userIds = Interprete.getUserList();
		AlgoLourd a= new AlgoLourdBayes();
		Date d = new Date();
//		for (int i=0;i<1000;i++) {
//			Interprete.setFeedBack(new DataFeedBack(new ObjectId(d,i), false, userIds.get(i % 100), userIds.get(i/2 % 100)));
//		}
//		
//		for (int i=0;i<1000;i++) {
//			Interprete.setFeedBack(new DataFeedBack(new ObjectId(d,i), true, userIds.get(i % 30), userIds.get(i/3 % 57)));
//		}
		
		for(int i=0;i<1000;i++) {
			Interprete.setFeedBack(new DataFeedBack(new ObjectId(d,i), false , new ObjectId("4d3ed60f3a4534094c000000"), new ObjectId("4d3ed60f3a45340913000000")));
		}
		
		a.maj();
	}
}

