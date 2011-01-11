import java.util.ArrayList;
import java.util.HashMap;

import org.bson.types.ObjectId;


public class AlgoLegerBayes extends AlgoLeger {

	public AlgoLegerBayes() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Recommendation answers(Request req) throws ExceptionRecoNotValid {
		DataUserNode user = Interprete.getUserNode(req.user); // on reccupere l'utilisateur qui fait sa requete
		
		//on va construire la liste des pages de ses amis
		
//		HashMap<ObjectId, HashMap<ObjectId,DataUPage>> pages =new HashMap<DataUserNode,HashMap<ObjectId, DataUPage>>(); //on met une relation friend-> pages de cet ami
//		for (UserRelation edge : user.friends) {
//			HashMap<ObjectId,DataUPage> pageOfThisUser = new HashMap<ObjectId, DataUPage>();
//			for (DataUPage page: edge.friend.pages) { pageOfThisUser.put(page.id, page); } //on ajoute toutes les pages et les hashe par leurs id pour les retrouver plus vite (la comparaison ensemblistes devrait etre plus rapide)
//			pages.put(edge.friend.id, pageOfThisUser);
//		} 
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
					tmp.add(new Composite(edge.friend, page));
					pages.put(page.url, tmp);
				}
			}
		}
		
		//on va supprimer toutes les pages qu'il a deja vu
		for (DataUPage page : user.uPages) {
			pages.remove(page.url);
		}
		
		//on va ensuite calculer toutes les probabilités 
		for (ArrayList<Composite> c : pages.values()) {
			
			
		}
		
		
		return null;
	}
	
	
	public class Composite {
		DataUserNode user;
		DataUPage page;
		
		public Composite(DataUserNode user, DataUPage page ) {
			this.user=user;
			this.page =page;
		}
	}

}
