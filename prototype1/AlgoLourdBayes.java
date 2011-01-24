
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.bson.types.ObjectId;

import com.mongodb.*;



public class AlgoLourdBayes extends AlgoLourd {

	
	public void maj() throws Exception {
		//on met a jour les compteurs de feedback positif et negatifs
		ArrayList<DataFeedBack> toBeUpdated = Interprete.getToDos();
		HashSet<ObjectId> feedbackers = new HashSet<ObjectId>();
		
		for (DataFeedBack feedback : toBeUpdated) {
			ObjectId recoGiver = feedback.recoGiver();
			ObjectId recoReceiver = feedback.recoReceiver();
			feedbackers.add(recoReceiver);
			Interprete.modifyFeedback(recoGiver, recoReceiver, feedback.clicked()); //true means that the feedback was positive
			}
		
		//on va ensuite supprimer des listes damis les recommandeurs trop mauvais que l'on va remplacer par un user au hasard
		ArrayList<ObjectId> userList = Interprete.getUserList();
		for (ObjectId guy : feedbackers) {
			DataUserNode user = Interprete.db2DataUserNodeHard(guy);
			
			boolean hasChanged= false;
			for (DataUserRelation relation: user.getFriends()) {
				if (relation.posFeedback - relation.negFeedback < -3) {
					hasChanged = true;
					user.getFriends().remove(relation);
					int var = (int) Math.floor(Math.random()* userList.size());
					user.getFriends().add( new DataUserRelation(Interprete.db2DataUserNodeSimple(userList.get(var))));
					//We add a new friend relation created from a random user.
					//We don't need to calculate the proba as it will be done later.
				}
			}
			if (hasChanged) {Interprete.DataUserNode2db(user);}
		}
		
		//on va ensuite recalculer les crossProbabiltes
		for (ObjectId userId: userList) {
			DataUserNode user = Interprete.db2DataUserNodeHard(userId);
			user.updateProbabilities();
			for (DataUserRelation relation : user.getFriends()) {
				Interprete.setCrossProbability(userId, relation.friend.getId(), relation.crossProbability);
			}
		}
		
		
	}
	
}
