
import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.mongodb.*;



public class AlgoLoursBayes extends AlgoLourd {

	
	public void maj() throws Exception {
		ArrayList<DataFeedBack> to_dos = Interprete.getToDos();
		for (DataFeedBack feedback : to_dos) {
			ObjectId recoGiver = feedback.recoGiver();
			ObjectId recoReceiver = feedback.recoGiver();
			Interprete.modifyFeedback(recoGiver, recoReceiver, feedback.clicked()); //true means that the feedback was positive
				
			}
		}
	}
	
}
