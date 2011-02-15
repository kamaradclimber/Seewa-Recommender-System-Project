
import java.util.ArrayList;
import java.util.HashSet;
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
		TreeSet<DataUserRelation> recommendeursPotentiels = new TreeSet<DataUserRelation>();
		for(DataUserRelation relation :u.getRecommandeurs()) {
			ObjectId id = relation.getRecommandeur().getMongoId();
			DataUserNode recommandeur = Interprete.db2DataUserNodeHard(id);
			//on prend tous les recommandeurs de ce recommandeur
			for (DataUserRelation rela : recommandeur.getRecommandeurs() ) {
				DataUserRelation relationNouvelle = new DataUserRelation(rela.getRecommandeur());
				relationNouvelle.updateProbability(u);
				recommendeursPotentiels.add(relationNouvelle); //on cree une nouvelle relation avec les recos potentiels
			}
		}
		//on a desormais la liste de tous les reco potentiels (recommandeur de recommandeur)
		
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
				+ " his id is "+bestRelationEver.recommandeur.getMongoId()
				+ " with crossProba="+bestRelationEver.crossProbability);
		return bestRelationEver;
	}
	
	private DataUserRelation getANewRandomRecommandeur(DataUserNode u)
	{
		ArrayList<ObjectId> userList = Interprete.getUserList();
		ObjectId userId= userList.get((int) Math.floor(Math.random()*userList.size()));//On en prend un au hasard
		DataUserRelation relation = new DataUserRelation(Interprete.db2DataUserNodeSimple(userId));
		relation.updateProbability(u);S
		return relation;
	}
	
}
