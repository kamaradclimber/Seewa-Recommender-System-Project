import java.util.ArrayList;


public class AlgoLegerUserCluster extends AlgoLeger {

	ArrayList<Cluster> clusters;
	
	public AlgoLegerUserCluster()
	{
		clusters = Interprete.readClusters(new Request("user_clusters")); // on reccupère la liste des clusters
	
	}
		
	public AlgoLegerUserCluster(ArrayList<Cluster> clusters)
	{
		this.clusters=clusters;
	}
	
	public AlgoLegerUserCluster(AlgoLourdFlatClusterization anAlgo)
	{
		if (anAlgo.clusters==null)
			try {
				anAlgo.maj();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		this.clusters=anAlgo.clusters;
	}
	
	
	
	private Cluster findCluster(DataVector user)
	{	
		
		Cluster bestCandidate= null;
		double bestDistance = Double.MAX_VALUE;
		for (Cluster candidate : clusters) { //on cherche la centroid la plus proche
			double dist = AlgoLourdFlatClusterization.squaredDistance(candidate.getCentroid(), user);
			if (dist< bestDistance) { //si on a trouvé une centroid plus proche on maj
				bestCandidate = candidate;
				bestDistance = dist;
			}
		}
		return bestCandidate;
	}
	
	public DataVector findCloseVector(DataVector user) //DataUser
	{
		Cluster hisCluster = findCluster(user);
		
		
		DataVector choice = hisCluster.getRandomElement();
		if (!hisCluster.contains(user)) hisCluster.add(user);//on ajoute après avoir coisit un élément pour éviter qu'on ne le recommande à lui-même
		return choice;
	}
	
	public DataUser findCloseUser(DataVector user) 
	{
		Cluster hisCluster = findCluster(user);
		
		DataUser choice= Interprete.getUser(hisCluster.getRandomElement());
		if (!hisCluster.contains(user)) hisCluster.add(user);//on ajoute après avoir coisit un élément pour éviter qu'on ne le recommande à lui-même
		return choice;
	}
	
	public Recommendation answers(Request req)
	{
		String username = req.get(); //req.username
		
		DataVector user = Interprete.readUcr(username); //get the UCR of the user asking for recommendation
		DataUser newBestBuddy = findCloseUser(user);
		return new Recommendation(newBestBuddy.name);
		
	}
}
