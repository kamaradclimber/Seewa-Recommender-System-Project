import java.util.ArrayList;

import org.bson.types.ObjectId;




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
		}
		}


