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
		
	try {
		Interprete.writeClusters(new ArrayList<DataCluster>());
	} catch (Exception e) {
		System.out.println("Ba oui ya un probleme, va falloir le gérer Johnny");
		e.printStackTrace();
	}
	if (false) {
	try {
		System.out.print("Test de lecture des clusters (centroid seulement)...");
		ArrayList<DataCluster>   clusters= Interprete.readClustersCentroids();
		if (clusters.size()==0) {
			System.out.println("[failed]");
			System.out.println("  -> La liste des lcusters est vide (peut etre qu'il n'y a rien dans la base de données...");
		} else {
			for (DataCluster c : clusters) {
				System.out.println("un cluster :");
				System.out.println(c);
			}
			System.out.println("[done]");
		}
	} catch (Exception e) {
		System.out.println("[failed]");
		e.printStackTrace();
	}
	}
	if (false) {
	try {
		System.out.print("Test de lecture des clusters...");
		ArrayList<DataCluster>   clusters= Interprete.readClusters();
		if (clusters.size()==0) {
			System.out.println("[failed]");
			System.out.println("  -> La liste des lcusters est vide (peut etre qu'il n'y a rien dans la base de données...");
		} else {
			for (DataCluster c : clusters) {
				System.out.println("un cluster :");
				System.out.println(c);
			}
			System.out.println("[done]");
		}
	} catch (Exception e) {
		System.out.println("[failed]");
		e.printStackTrace();
	}
	}
	try {
	System.out.print("Ecriture d'un cluster...");
	DataCluster c=new DataCluster(17, new DataVector(false), new ArrayList<DataVector>() , new ObjectId());
	c.add(Interprete.readUTR(new ObjectId("8dda084dd1ab871052000000")));
//	System.out.println("on commence le boiyulot serieux");
	c.updateCentroid();
	ArrayList<DataCluster> clist = new ArrayList<DataCluster>();
	clist.add(c);
	Interprete.writeClusters(clist);
	
	System.out.println("[done]");
	} catch (Exception e) {
		System.out.println("[failed]");
		e.printStackTrace();
		
	}

	try {
		System.out.print("Lecture d'une liste de cluster...");
	ArrayList<DataCluster>  list = Interprete.readClusters();
//	System.out.println(list);
		System.out.println("[done]");
		} catch (Exception e) {
			System.out.println("[failed]");
			e.printStackTrace();
		}
	
	
	System.out.print("Test de calcul d'un UTR (pour un user)...");
//	Interprete.updateUTR(new ObjectId("8dda084dd1ab871052000000"));
//	Interprete.updateUTR2();
	System.out.println("[done]");
	//Interprete.updateUTR();
	try {
		System.out.print("Test de recommendation.....");
	Site s= new Site();
	System.out.println(s.requestReco(new Request("USER 8dda084dd1ab871052000000")));
	System.out.println("[done]");
	} catch (Exception e) {
		System.out.println("[failed]");
		e.printStackTrace();
	}
	try {
		DBCollection clusters= Interprete.db.getCollection("clusters");
		clusters.drop();
		ArrayList<DataCluster> cc= new ArrayList<DataCluster>();
		for (int i=0; i<10; i++) {
			cc.add(new DataCluster(17, new DataVector(false), new ArrayList<DataVector>() , new ObjectId()));
		}
		Interprete.writeClusters(cc);
		
	DBCollection users = Interprete.db.getCollection("users");
	DBCursor cusr = users.find();
		ArrayList<DataVector> userss= new ArrayList<DataVector>();
		while(cusr.hasNext()) {
			BasicDBObject user = (BasicDBObject) cusr.next();
			BasicDBObject tmp =(BasicDBObject) ((BasicDBObject) user.get("utr")).get("utrs");
			userss.add(Interprete.db2DataVector(tmp ,null,(ObjectId)user.get("_id")));
		}

		AlgoLourdFlatClusterizationIneg algo = new AlgoLourdFlatClusterizationIneg(userss);
		algo.maj();
	} catch (Exception e) {
		e.printStackTrace();
	}
	
		}
	}


