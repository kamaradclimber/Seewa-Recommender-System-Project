import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.bson.types.ObjectId;


public class AlgoLegerBayes extends AlgoLeger {
//test
	public AlgoLegerBayes() {
		super();
	}

	@Override
	public Recommendation answers(Request req) throws ExceptionRecoNotValid {
		DataUserNode user = Interprete.getUserNode(req.getUser()); // on reccupere l'utilisateur qui fait sa requete
		
		HashMap<String, ArrayList<Composite>> pages = new HashMap<String, ArrayList<Composite>>(); //on associe url-> avec un object qui contient un user et sa upage
		for (DataUserRelation edge : user.getFriends()) {
			for (DataUPage page: edge.friend.getUPages()) { //on parcourt toutes les pages des recommendeurs
				if (pages.containsKey(page.getUrl())) {  // pour les stocker en hashant grace a l'url de la page
					ArrayList<Composite> copie =(ArrayList<Composite>) (pages.get(page.getUrl())).clone(); //petit test pour savoir si les objets reccuperes avec get puis mdofiie sont effectivement modifies dans la hashmap
					(pages.get(page.getUrl())).add(new Composite(edge.friend, page, page.pageRank));
					copie.add(new Composite(edge.friend, page, page.pageRank));
					if (copie!= pages.get(page.getUrl())) {System.out.println("ca marche enfin on est pas oblige de faire la copie"); }
					pages.put(page.getUrl(),copie);
				} else {
					ArrayList<Composite> tmp= new ArrayList<Composite>();
					tmp.add(new Composite(edge.friend, page, edge.crossProbability));
					pages.put(page.getUrl(), tmp);
				}
			}
		}
		
 		//on va supprimer toutes les pages qu'il a deja vu
		for (DataUPage page : user.getUPages()) {
			pages.remove(page.getUrl());
		}
		
		TreeMap<Double,String> bestsReco = new TreeMap<Double,String>(); //on stocke les trois meilleurs proba  
		for(int i=0;i<3;i++){bestsReco.put((double)0, "");} //on initialise à 3 meilleures reco, nombre qu'on maintient ensuite
		
		//on va ensuite calculer toutes les probabilités 
		for (ArrayList<AlgoLegerBayes.Composite> cc : pages.values()) { //il y a peut etre une optimisation a faire sur la facon dont on stocke et parcourt cette table de hashage
			for(Composite c :cc) {
				double proba =  c.crossProbability / c.user.uPageMean * c.page.pageRank;
				bestsReco.put(proba, c.page.getUrl());
				bestsReco.remove(bestsReco.firstKey()); //on maintient seulement 3 meilleures
			}
		}
		
		
		return new Recommendation(bestsReco.lastEntry().getValue());
	}
	
	
	public class Composite {
		DataUserNode user;
		DataUPage page;
		double crossProbability;
		
		public Composite(DataUserNode user, DataUPage page , double proba) {
			this.user=user;
			this.page =page;
			this.crossProbability = proba; //la proba P(A inter B)
		}
	}

}
