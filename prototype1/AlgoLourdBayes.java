
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.bson.types.ObjectId;

import com.mongodb.*;



public class AlgoLourdBayes extends AlgoLourd {

	
	public void maj() throws Exception {
		//on met a jour les compteurs de feedback positif et negatifs
		ArrayList<DataFeedBack> to_dos = Interprete.getFeedback();
		for (DataFeedBack feedback : to_dos) {
			ObjectId recoGiver = feedback.recoGiver();
			ObjectId recoReceiver = feedback.recoGiver();
			Interprete.modifyFeedback(recoGiver, recoReceiver, feedback.clicked()); //true means that the feedback was positive
			}
		
		//on va ensuite supprimer des listes damis les recommandeurs trop mauvais
			//pour cela on va commencer par etablir la liste des utilisateurs qui ont donne du feedback
		HashSet<ObjectId> feedbackers = new HashSet<ObjectId>();
		for (DataFeedBack feedback : to_dos) {
			feedbackers.add(feedback.recoReceiver());
		}
		for (ObjectId guy : feedbackers) {
			DataUserNode user = Interprete.db2DataUserNodeHard(guy);
			for (DataUserRelation relation: user.getFriends()) {
				if (relation.posFeedback - relation.negFeedback < -3) {
					Interprete.removeRecommandeur(guy, relation.friend.getId());
				}
			}
		}
		
		//on va ensuite recalculer les crossProbabiltes
		for (ObjectId userId: Interprete.getUserList()) {
			DataUserNode user = Interprete.db2DataUserNodeHard(userId);
			user.updateProbabilities();
			for (DataUserRelation relation : user.getFriends()) {
				Interprete.setCrossProbability(userId, relation.friend.getId(), relation.crossProbability);
			}
		}
		
		
	}
	
}
