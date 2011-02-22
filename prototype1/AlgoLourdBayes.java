
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.bson.types.ObjectId;

public class AlgoLourdBayes extends AlgoLourd {

	//mise à jour régulière des structures employées employées pour les reco
	public void maj() {
		//on met a jour les compteurs de feedback positif et negatifs
		System.out.print("maj des compteurs de feedback...");
		ArrayList<DataFeedBack> toBeUpdated = Interprete.getFeedback();
		System.out.print("("+ toBeUpdated.size() +" entries)");
		ArrayList<ObjectId> feedbackers = new ArrayList<ObjectId>();
		
		ObjectId recoGiver;
		ObjectId recoReceiver;
		for (DataFeedBack feedback : toBeUpdated) {
			recoGiver = feedback.recoGiver();
			recoReceiver = feedback.recoReceiver();
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
			for (int i=0; i< user.getRecommandeurs().size();i++) {
				relation =  user.getRecommandeurs().get(i);
				if (relation.posFeedback - relation.negFeedback < -3) {
					hasChanged = true;

					//We add a new friend relation created from a random user.
					//We don't need to calculate the proba as it will be done later.
					user.getRecommandeurs().set(i,this.getANewRecommandeur(user));
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
			for (DataUserRelation relation : user.getRecommandeurs()) {
				if (relation.getCrossProbability()==0) {
					//Le recommandeur ajouté n'est pas très bon : sa crossProba est de 0
					relation.setCrossProbability(0.01);
				}
				Interprete.setCrossProbability(userId, relation.recommandeur.getId(), relation.getCrossProbability());
			}
		}
		System.out.println("[done]");
		
		
	}
	
	//this function helps create a new friend for the user u
	private DataUserRelation getANewRecommandeur(DataUserNode u) { 
		// attention a la performance de cette fonction !
		TreeSet<DataUserRelation> potentialRecommenders = new TreeSet<DataUserRelation>();
		for(DataUserRelation relation :u.getRecommandeurs()) {
			ObjectId id = relation.getRecommender().getMongoId();
			DataUserNode recommender = Interprete.db2DataUserNodeHard(id);
			//on prend tous les recommandeurs de ce recommandeur
			for (DataUserRelation rela : recommender.getRecommandeurs() ) {
				DataUserRelation newRelation = new DataUserRelation(rela.getRecommender());
				newRelation.updateProbability(u);
				potentialRecommenders.add(newRelation); //on cree une nouvelle relation avec les recos potentiels
			}
		}
		//on a desormais la liste de tous les reco potentiels (recommandeur de recommandeur)
		
		if ( potentialRecommenders.size()==0)
		//He hasn't friends enough :( We add a random user. 
			return u.getANewRandomRecommender();
				
		//TODO: verifie-t-on qu'il n'y a pas de doublons quelquepart?
		//Random=>on sait pas ce qui sort
		
		double sum=0;	
		for ( DataUserRelation comp : potentialRecommenders)
		{
			sum += comp.crossProbability;
		}
		double var= Math.random() * sum;
		Iterator<DataUserRelation> probs = potentialRecommenders.iterator();
		DataUserRelation bestRelationEver = probs.next();
		double bestKey = bestRelationEver.crossProbability; 
		while (probs.hasNext() && var-bestKey >0 )
		{
			var-=bestKey;
			bestRelationEver = probs.next();
			bestKey = bestRelationEver.crossProbability;
		}
		System.out.println("added a new recommendeur for"+ u.getMongoId()
				+ " his id is "+bestRelationEver.recommandeur.getMongoId()
				+ " with crossProba="+bestRelationEver.crossProbability);
		return bestRelationEver;
	}
	
	
	
}
