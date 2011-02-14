
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.bson.types.ObjectId;



public class AlgoLourdBayes extends AlgoLourd {

	
	public void maj() {
		//on met a jour les compteurs de feedback positif et negatifs
		System.out.print("maj des compteurs de feedback...");
		ArrayList<DataFeedBack> toBeUpdated = Interprete.getFeedback();
		System.out.print("("+ toBeUpdated.size() +")");
		HashSet<ObjectId> feedbackers = new HashSet<ObjectId>();
		
		for (DataFeedBack feedback : toBeUpdated) {
			ObjectId recoGiver = feedback.recoGiver();
			ObjectId recoReceiver = feedback.recoReceiver();
			feedbackers.add(recoReceiver);
			
			Interprete.modifyFeedback(recoGiver, recoReceiver, feedback.clicked()); //true means that the feedback was positive
			}
		System.out.println("[done]");
		
		
		
		//on va ensuite supprimer des listes damis les recommandeurs trop mauvais que l'on va remplacer par un user au hasard
		System.out.print("maj des listes d'ami...");
		ArrayList<ObjectId> userList = Interprete.getUserList();
		for (ObjectId guy : feedbackers) {
			DataUserNode user = Interprete.db2DataUserNodeHard(guy); //TODO : améliorer en ne passant que les users ajoutés ou removed
			
			boolean hasChanged= false;
			DataUserRelation relation;
			for (int i=0; i< user.getFriends().size();i++) {
				relation =  user.getFriends().get(i);
				if (relation.posFeedback - relation.negFeedback < -3) {
					hasChanged = true;

					//We add a new friend relation created from a random user.
					//We don't need to calculate the proba as it will be done later.
					user.getFriends().set(i,this.getANewFriend(user));
				}
			}
			if (hasChanged) {Interprete.DataUserNode2db(user);}
		}
		System.out.println("[done]");
		
		
		//on va ensuite recalculer les crossProbabiltes
		System.out.print("maj des crossProba...");
		for (ObjectId userId: userList) {
			DataUserNode user = Interprete.db2DataUserNodeHard(userId);
			user.updateProbabilities();
			for (DataUserRelation relation : user.getFriends()) {
				Interprete.setCrossProbability(userId, relation.friend.getId(), relation.crossProbability);
			}
		}
		System.out.println("[done]");
		
		
	}
	
	
	private DataUserRelation getANewFriend(DataUserNode u) { 
		// attention a la performance de cette fonction !
		TreeSet<DataUserRelation> recommendeursPotentiels = new TreeSet<DataUserRelation>();
		for(DataUserRelation relation :u.getFriends()) {
			ObjectId id = relation.getFriend().getMongoId();
			DataUserNode friend = Interprete.db2DataUserNodeHard(id);
			for (DataUserRelation rela : friend.getFriends() ) {
				DataUserRelation relationNouvelle = new DataUserRelation(rela.getFriend());
				relationNouvelle.updateProbability(u);
				recommendeursPotentiels.add(relationNouvelle); //on cree une nouvelle relation avec les recos potentiels
			}
		}
		//on a desormais la liste de tous les reco potentiels (amis damis)
		
		double sum=0;	
		for ( DataUserRelation comp : recommendeursPotentiels)
		{
			sum += comp.crossProbability;
		}
		double var= Math.random() * sum;
		Iterator<DataUserRelation> probs = recommendeursPotentiels.iterator();
		DataUserRelation bestRelationEver = probs.next();
		double bestKey = bestRelationEver.crossProbability; 
		while (probs.hasNext() && var-bestKey >0 )
		{
			var-=bestKey;
			bestRelationEver = probs.next();
			bestKey = bestRelationEver.crossProbability;
		}
		System.out.println("added a new recommendeur for"+ u.getMongoId()
				+ " his id is "+bestRelationEver.friend.getMongoId()
				+ " with crossProba="+bestRelationEver.crossProbability);
		return bestRelationEver;
	}
	
	
}
