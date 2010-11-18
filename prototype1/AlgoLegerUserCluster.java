import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AlgoLegerUserCluster extends AlgoLeger {

	ArrayList<DataCluster> clusters= new ArrayList<DataCluster>();
	
	public AlgoLegerUserCluster()
	{
		clusters = Interprete.readClusters(new Request("user_clusters")); // on reccupère la liste des clusters
		if (clusters==null) {
			System.out.println("Le chargement des clusters depuis la base de données a échoué, je met une liste de cluster vide à la place");
			clusters = new ArrayList<DataCluster>();
		}
	
	}
		
	public AlgoLegerUserCluster(ArrayList<DataCluster> clusters)
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
	
	
	
	private DataCluster findCluster(DataVector user)
	{	
		
		DataCluster bestCandidate= null;
		double bestDistance = Double.MAX_VALUE;
		
		if (clusters==null || clusters.size()==0) {
			System.out.println("DEBUG : la liste des clusters est vide, le chargement à du échouer");
			System.out.println("DEBUG : je met donc un cluster factice pour que le chargement continue mais bon cest vraiment une rustine");
			DataCluster rustine = new DataCluster();
			DataVector t = new DataVector(false);
			t.put("theme_factice", new Float(0.1));
			rustine.add(t);
			rustine.updateCentroid();
			clusters.add(rustine);
			
		}
		
		for (DataCluster candidate : clusters) { //on cherche la centroid la plus proche
			double dist = AlgoLourdFlatClusterization.squaredDistance(candidate.getCentroid(), user);
			if (dist< bestDistance) { //si on a trouvé une centroid plus proche on maj
				bestCandidate = candidate;
				bestDistance = dist;
			}
		}
		if (bestCandidate==null) {
			System.out.println("DEBUG : le meilleur candidat n'a pas été trouvé");
		}
		return bestCandidate;
	}
	
	
	public DataUser findCloseUser(DataVector user) 
	{
		DataCluster hisCluster = findCluster(user);
		
		DataUser choice= Interprete.getUser(hisCluster.getRandomElement());
		if (!hisCluster.contains(user)) hisCluster.add(user);//on ajoute après avoir choisit un élément pour éviter qu'on ne le recommande à lui-même
		Interprete.writeClusters(clusters); // THINK : maybe this is not optimal to write clusters in eqch reco process (le gqin est fqible en terme dinfo)
		return choice;
	}
	
	public Recommendation answers(Request req)
	{

		String username = req.get(); //req.username TODO regexp pour retrouver le nom de l'utilisateur dans la string de requete
		
		DataVector user = Interprete.readUcr(username); //get the UCR of the user asking for recommendation
		if (user==null) {
			System.out.println("DEBUG : on a pas reccupéré un user valide (il est même null !), j'en met un factice pour que ca tourne, mais bon....FIXME !");
			user = new DataVector(false);
			user.put("theme_factice", new Float(0.5));
		}
		DataUser newBestBuddy = findCloseUser(user);
		return new Recommendation(newBestBuddy.name);
		
	}
}
