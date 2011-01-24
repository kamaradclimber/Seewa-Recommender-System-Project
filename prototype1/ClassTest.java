import java.util.ArrayList;
import java.util.TreeMap;

import org.bson.types.ObjectId;

import com.mongodb.DBCollection;




public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataUserNode test = new DataUserNode(new ObjectId("4d3dcdbc6470dce220eb5a3e"));
		Interprete.DataUserNode2dbNew(test);
	}

}