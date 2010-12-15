import java.util.ArrayList;

import org.bson.types.ObjectId;


public class AlgoLegerUserCluster extends AlgoLeger {

	ArrayList<DataCluster> clusters= new ArrayList<DataCluster>();
	
	public AlgoLegerUserCluster() throws ExceptionRecoNotValid
	{	
		
		clusters = Interprete.readClusters(); //TODO remplacer par clusterCentroids une fois que ca marchera
	}
		
	public AlgoLegerUserCluster(ArrayList<DataCluster> clusters)
	{
		this.clusters=clusters;
	}
	
	
	
	
	private DataCluster findCluster(DataVector user) throws ExceptionRecoNotValid
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
			throw new ExceptionRecoNotValid(ExceptionRecoNotValid.ERR_NO_CLUSTER_ASSIGNED_TO_USER); 
		}
		
		
		
		return bestCandidate;
	}
	
	
	public DataUser findCloseUser(DataVector user) throws ExceptionRecoNotValid
	{
		
		DataCluster hisCluster = findCluster(user);
		DataVector newBuddy = hisCluster.getRandomElement();
		DataUser choice= Interprete.getUser(newBuddy);
		if (!hisCluster.contains(user)) hisCluster.add(user);//on ajoute après avoir choisit un élément pour éviter qu'on ne le recommande à lui-même
		//Interprete.writeClusters(clusters); //TODO est-ce qu'on le fait ?  // THINK : maybe this is not optimal to write clusters in eqch reco process (le gqin est fqible en terme dinfo)
		return choice;
		
		
	}
	
	public Recommendation answers(Request req) throws ExceptionRecoNotValid
	{

		ObjectId userId = req.getUser(); //req.username TODO regexp pour retrouver le nom de l'utilisateur dans la string de requete
		DataVector user;
		user = Interprete.readUTR(userId);
		DataUser newBestBuddy = findCloseUser(user);
		return new Recommendation(newBestBuddy.getName());
		
		
		//get the UTR of the user asking for recommendation
		
		
	}
}
