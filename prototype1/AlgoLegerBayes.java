import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.bson.types.ObjectId;


public final class AlgoLegerBayes extends AlgoLeger {
//test
	private static AlgoLegerBayes singleton;
	private static int nbReco=10; //number of result among which we chose a recommendation 
	
	private AlgoLegerBayes() {
		super();
		singleton= this ;
	}
	
	public static AlgoLeger getAlgo(){
		
		if (singleton==null)
		{
			return new AlgoLegerBayes();
		}
		
		return singleton;
	}

	
	
	public static int getNbReco() {
		return nbReco;
	}

	public static void setNbReco(int nbReco) {
		AlgoLegerBayes.nbReco = nbReco;
	}

	@Override
	public Recommendation answers(Request req) throws ExceptionRecoNotValid {
		DataUserNode user = Interprete.db2DataUserNodeHard(req.getUserId()); // on reccupere l'utilisateur qui fait sa requete
		
		HashMap<String, ArrayList<Composite>> pages = new HashMap<String, ArrayList<Composite>>(); //on associe url-> avec un object qui contient un user et sa upage
		for (DataUserRelation edge : user.getFriends()) {
			//on parcourt toutes les pages des recommendeurs
			for (DataUPage page: edge.friend.getUPages()) { 
				if (!pages.containsKey(page.getUrl()))
					pages.put(page.getUrl(), new ArrayList<Composite>() );
				// pour les stocker en utilisant  l'url de la page comme clé
				pages.get(page.getUrl()).add(new Composite(edge.friend, page, edge.crossProbability));
			}
		}
		
 		//on va supprimer toutes les pages qu'il a deja vu
		for (DataUPage page : user.getUPages()) {
			pages.remove(page.getUrl());
		}
		
		TreeSet<Composite> bestReco = new TreeSet<Composite>(); //on stocke les nbReco meilleurs proba  
		int nbResult=Math.min(nbReco, pages.size());
		
		//on initialize ensuite la structure avec des recommendations de valeurs négatives 
		for (int j=0; j<nbResult; j++) bestReco.add(new Composite(null,null,-j));
		
		
		//on va ensuite calculer toutes les probabilités 
		for (ArrayList<Composite> cc : pages.values()) { //il y a peut etre une optimisation a faire sur la facon dont on stocke et parcourt cette table de hashage
			for(Composite c :cc) {
				c.crossProbability =  c.crossProbability / c.user.uPageMean * c.page.pageRank;
				bestReco.add(c); //on ajoute la recommendation
				bestReco.remove(bestReco.first()); //et on supprime la pire recommendation
			}
		}
		double sum=0;	
		for ( Composite comp : bestReco)
		{

			sum += comp.crossProbability;
		}
		double var= Math.random() * sum;
		Iterator<Composite> probs = bestReco.iterator();
		double bestKey = probs.next().crossProbability; 
		while (probs.hasNext() && var-bestKey >0 )
		{
			var-=bestKey;
			bestKey = probs.next().crossProbability;
		}		
		
		return new Recommendation(bestReco.last());
	}
		
	
}
