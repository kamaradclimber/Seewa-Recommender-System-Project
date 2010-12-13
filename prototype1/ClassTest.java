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
	Interprete.updateUTR(new ObjectId("bbe0004dff77067352000000"));
	System.out.println("Fin du premie rtest");
	Interprete.updateUTR2();

		}
		}


