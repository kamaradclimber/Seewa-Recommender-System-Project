import java.util.ArrayList;
import java.util.Vector;


public class AlgoLegerCluster extends AlgoLeger {

	public Recommendation answers(Request req) {
		String username = "Bobby"; //req.username
		ArrayList<DataCluster> clusters = (new Interprete()).readClusters(new Request("user_clusters")); // on reccupère la liste des clusters
		DataVector user = (new Interprete()).readUTR(username); //get the UCR of the user asking for recommendation
		
		DataCluster bestChoice= null;
		Double bestDistance = Double.MAX_VALUE;
		for (DataCluster candidate: clusters) { // what is the best cluster for this UCR
			Double distance= AlgoLourdFlatClusterization.distance(candidate.getCentroid(), user);
			if (distance > bestDistance) {
				bestChoice   = candidate;
				bestDistance = distance;
			}
		}
		
		DataVector reco = bestChoice.getRandomElement();
		
		return new Recommendation(DataCluster.getUser(reco).name); // returns the name of a user in the correct cluster
	}
}
