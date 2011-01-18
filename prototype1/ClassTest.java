import java.util.ArrayList;
import java.util.Iterator;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;




public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Recommendation result;
		try {
			result= AlgoLegerBayes.getAlgo().answers(new Request(null, "test", null, null));
			System.out.println(result);
			
		} catch (ExceptionRecoNotValid e) {
			e.printStackTrace();
		}
		
		
		
		}
	}


