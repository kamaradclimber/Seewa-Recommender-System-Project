import java.util.ArrayList;
import java.util.Random;


public class ClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//		String[] categories = {"Tennis", "Linux","Fleurs","Chocolat","Tsonga","Kubrick","Chasse","Marathon","Art moderne","Randonnee","Magritte"};
		//		String[] categories = {"Pasinilinna","Borloo","Architecture","CPNT"};
		String[] categories = {"Pasinilinna"};
		Random r = new Random();
		ArrayList<DataVector> vecteurs = new ArrayList<DataVector>();
		for (int i =0; i<50000; i++) {
			DataVector vecteur = new DataVector();
			for (String str : categories) {
				vecteur.put(str, r.nextFloat()*10);
			}
			vecteurs.add(vecteur);
		}

		Site site= new Site();
		Request req= new Request("RECO USER FOR GREG");
		site.requestReco(req);

		//			Visualizer v= new Visualizer();
		//			v.visualizerCluster(algo.clusters.get(0));

	}



}
}


