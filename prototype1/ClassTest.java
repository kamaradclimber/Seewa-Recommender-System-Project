import java.util.ArrayList;
import java.util.Random;


public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String[] categories = {"Tennis", "Linux","Fleurs","Chocoloat","Tsonga","Kubrick","Chasse","Marathon","Art moderne","Randonnee","Magritte"};
		String[] categories = {"Pasinilinna","Borloo","Architecture","CPNT"};
		Random r = new Random();
		ArrayList<DataVector> vecteurs = new ArrayList<DataVector>();
		for (int i =0; i<50; i++) {
			DataVector vecteur = new DataVector();
			for (String str : categories) {
				vecteur.put(str, r.nextFloat()*10);
			}
			vecteurs.add(vecteur);
		}
		if (false) {
			try {
				AlgoLourdFlatClusterization algo = new AlgoLourdFlatClusterization(1, vecteurs);
				//			for (Cluster cluster : algo.clusters) {
				//				System.out.println("---");
				//				for (DataVector vecteur : cluster) {
				//					System.out.println(vecteur);
				//				}
				//				System.out.println("centroid : "+cluster.centroid);
				//			}
				long start = System.currentTimeMillis();
				algo.maj();
				long end = System.currentTimeMillis();
				System.out.println((end-start)/1000);
				for (Cluster cluster : algo.clusters) {
					System.out.println("---");
					for (DataVector vecteur : cluster) {
						System.out.println(vecteur);
					}
					System.out.println("centroid : "+cluster.centroid);
				}

				Visualizer v= new Visualizer();
				v.visualizerCluster(algo.clusters.get(0));

			} catch (Exception e) {
				e.printStackTrace();
			}


		}
		}

}
