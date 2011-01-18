import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.bson.types.ObjectId;


public class AlgoLegerBayes extends AlgoLeger {
//test
	public AlgoLegerBayes() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Recommendation answers(Request req) throws ExceptionRecoNotValid {
		DataUserNode user = Interprete.getUserNode(req.user); // on reccupere l'utilisateur qui fait sa requete
		
		HashMap<String, ArrayList<Composite>> pages = new HashMap<String, ArrayList<Composite>>(); //on associe url-> avec un object qui contient un user et sa upage
		for (UserRelation edge : user.friends) {
			for (DataUPage page: edge.friend.uPages) { //on parcourt toutes les pages des recommendeurs
				if (pages.containsKey(page.url)) {  // pour les stocker en hashant grace a l'url de la page
					ArrayList<Composite> copie =(ArrayList<Composite>) (pages.get(page.url)).clone(); //petit test pour savoir si les objets reccuperes avec get puis mdofiie sont effectivement modifies dans la hashmap
					(pages.get(page.url)).add(new Composite(edge.friend, page));
					copie.add(new Composite(edge.friend, page));
					if (copie!= pages.get(page.url)) {System.out.println("ca marche enfin on est pas oblige de faire la copie"); }
					pages.put(page.url,copie);
				} else {
					ArrayList<Composite> tmp= new ArrayList<Composite>();
					tmp.add(new Composite(edge.friend, page, edge.crossProbability));
					pages.put(page.url, tmp);
				}
			}
		}
		
 		//on va supprimer toutes les pages qu'il a deja vu
		for (DataUPage page : user.uPages) {
			pages.remove(page.url);
		}
		
		TreeMap<Double,String> bestsReco = new TreeMap<Double,String>(); //on stocke les trois meilleurs proba  
		for(int i=0;i<3;i++){bestsReco.put((double)0, "");} //on initialise à 3 meilleures reco, nombre qu'on maintient ensuite
		
		//on va ensuite calculer toutes les probabilités 
		for (ArrayList<Composite> c : pages.values()) { //il y a peut etre une optimisation a faire sur la facon dont on stocke et parcourt cette table de hashage
			
			double proba = c.crossProbability / c.user.uPageMean * c.page.pageRank;
			bestsReco.put(proba, c.page.url);
			bestsReco.remove(bestsReco.firstKey()); //on maintient au 3 meilleures
		}
		
		
		return new Recommendation(bestsReco.lastEntry().getValue());
	}
	
	
	public class Composite {
		DataUserNode user;
		DataUPage page;
		double crossProbabilitya;
		
		public Composite(DataUserNode user, DataUPage page , double proba) {
			this.user=user;
			this.page =page;
			this.crossProbability = proba; //la proba P(A inter B)
		}
	}

}
