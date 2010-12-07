import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AlgoLegerUserCluster extends AlgoLeger {

	ArrayList<DataCluster> clusters= new ArrayList<DataCluster>();
	
	public AlgoLegerUserCluster() throws RecoException
	{	
		
		clusters = Interprete.readClusters(); // on reccupère la liste des clusters TODO : remplacer par readLCustersCentroids
		
		/* Ce qui suit ne parait plus n�cessaire avec la gestion d'exceptions, � confirmer
		 * 
		 * 
		if (clusters==null) {
			//System.out.println("Le chargement des clusters depuis la base de données a échoué, je met une liste de cluster vide à la place");
			//clusters = new ArrayList<DataCluster>();
		}
		*/
	}
		
	public AlgoLegerUserCluster(ArrayList<DataCluster> clusters)
	{
		this.clusters=clusters;
	}
	
	public AlgoLegerUserCluster(AlgoLourdFlatClusterizationIneg anAlgo)
	{
		if (anAlgo.clusters==null)
			try {
				anAlgo.maj();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		this.clusters=anAlgo.clusters;
	}
	
	
	
	private DataCluster findCluster(DataVector user) throws RecoException
	{	
		
		DataCluster bestCandidate= null;
		double bestDistance = Double.MAX_VALUE;
		
		
		for (DataCluster candidate : clusters) { //on cherche la centroid la plus proche
			double dist = AlgoLourdFlatClusterizationIneg.squaredDistance(candidate.getCentroid(), user);
			if (dist< bestDistance) { //si on a trouvé une centroid plus proche on maj
				bestCandidate = candidate;
				bestDistance = dist;
			}
		}
		
		
		if (bestCandidate==null) {
			throw new RecoException(RecoException.ERR_NO_CLUSTER_ASSIGNED_TO_USER); 
		}
		
		
		
		return bestCandidate;
	}
	
	
	public DataUser findCloseUser(DataVector user) throws RecoException
	{
		
		DataCluster hisCluster = findCluster(user);
		DataUser choice= Interprete.getUser(hisCluster.getRandomElement());
		if (!hisCluster.contains(user)) hisCluster.add(user);//on ajoute après avoir choisit un élément pour éviter qu'on ne le recommande à lui-même
		Interprete.writeClusters(clusters); // THINK : maybe this is not optimal to write clusters in eqch reco process (le gqin est fqible en terme dinfo)
		return choice;
		
		
	}
	
	public Recommendation answers(Request req) throws RecoException
	{

		String username = req.get(); //req.username TODO regexp pour retrouver le nom de l'utilisateur dans la string de requete
		DataVector user;
		user = Interprete.readUTR(username);
		DataUser newBestBuddy = findCloseUser(user);
		return new Recommendation(newBestBuddy.getName());
		
		
		//get the UTR of the user asking for recommendation
		
		
	}
}
