import java.util.ArrayList;
import java.util.Random;


public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String[] categories = {"Tennis", "Linux","Fleurs","Chocoloat","Tsonga","Kubrick","Chasse","Marathon","Art moderne","Randonnee","Magritte"};
		String[] categories = {"Tennis"};
		Random r = new Random();
		ArrayList<DataVector> vecteurs = new ArrayList<DataVector>();
		for (int i =0; i<10; i++) {
			DataVector vecteur = new DataVector();
			for (String str : categories) {
				vecteur.put(str, r.nextFloat()*10);
			}
			vecteurs.add(vecteur);
		}
		try {
			FlatClusterization algo = new FlatClusterization(2, vecteurs);
			for (Cluster cluster : algo.clusters) {
				System.out.println("---");
				for (DataVector vecteur : cluster) {
					System.out.println(vecteur);
				}
				System.out.println(cluster.centroid);
			}
			algo.maj();
			for (Cluster cluster : algo.clusters) {
				System.out.println("---");
				for (DataVector vecteur : cluster) {
					System.out.println(vecteur);
				}
				System.out.println(cluster.centroid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		}

}
