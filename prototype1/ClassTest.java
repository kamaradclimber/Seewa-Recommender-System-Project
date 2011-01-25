import java.util.ArrayList;
import java.util.TreeMap;

import org.bson.types.ObjectId;

import com.mongodb.DBCollection;




public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

//		Recommendation result;
//		try {
//			TreeMap<String,Integer> probas= new TreeMap<String, Integer>();
//			int nbTest=10000;
//			for (int i=0; i<nbTest; i++)
//			{
//			result= AlgoLegerBayes.getAlgo().answers(new Request(null, "test", null, null));
//			if (probas.containsKey(result.description))
//			{
//				Integer prob= probas.get(result.description);
//				probas.put(result.description, prob+1);}
//			else
//				probas.put(result.description,1);
//			}
//			System.out.println("----resultat des courses------------------------------------");
//			for (String url: probas.keySet())
//				System.out.println(probas.get(url)*100.0/nbTest +"%  "+ url);
//			
//		} catch (ExceptionRecoNotValid e) {
//			e.printStackTrace();
//		}
//		
//
		
//		DataUPage jeanJauresLEquipe= new DataUPage(new ObjectId("4d3d8e855fe26bfccdb3f54d"),new ObjectId("8dda084dd1ab871052000000"), 0.8, "www.l�quipe.fr");
//		Interprete.DataUPage2db(jeanJauresLEquipe);
//		System.out.println(Interprete.db2DataUserNodeSimple(new ObjectId("8dda084dd1ab871053000000")));
//		
//		DataUserNode user2 = new DataUserNode(new ObjectId("8dda084dd1ab871053000000"));
//
//		DataUserRelation user2user1 = new DataUserRelation(user2,0.5,1,1);
//
//		ArrayList<DataUserRelation> userrelation = new ArrayList<DataUserRelation>();
//		userrelation.add(user2user1);
//		
//		DataUserNode user1 = new DataUserNode(userrelation,new ObjectId("8dda084dd1ab871050000000"));
//
//		Interprete.DataUserNode2db(user1);
//		
//		DataUserNode test = Interprete.db2DataUserNodeSimple(new ObjectId("8dda084dd1ab871050000000"));
//		System.out.println(test);
		
//		AlgoLeger b =AlgoLegerBayes.getAlgo();
//		try {
//			b.answers(new Request(null, "test", null, TypeOfRequest.RECOPAGE));
//		} catch (ExceptionRecoNotValid e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//System.out.println(Interprete.db2DataUserNodeSimple(new ObjectId("8dda084dd1ab871052000000")));
//		Interprete.setCrossProbability(new ObjectId("4d3da490c2ad6bfc01000000"),new ObjectId("4d3da490c2ad6bfc02000000"),0.3);
//		System.out.println();
		
		for (ObjectId o : Interprete.getUserList()) {
			DataUserNode u= Interprete.db2DataUserNodeSimple(o);
			System.out.println(u);
			System.out.println(u.getFriends());
			Interprete.DataUserNode2db(u);	
			DataUserNode v= Interprete.db2DataUserNodeSimple(o);
		}
	}
}


