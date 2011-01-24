import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bson.types.ObjectId;


public final class AlgoLegerBayes extends AlgoLeger {
//test
	private static AlgoLegerBayes singleton;
	
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

	@Override
	public Recommendation answers(Request req) throws ExceptionRecoNotValid {
		DataUserNode user;
		if (req.getUrl()=="test" ) 
		{
			//gï¿½nï¿½ des Upages
			//DataUPage jeanMichLeMonde= new DataUPage(new ObjectId(), 0.7, "www.lemonde.fr");
			DataUPage jeanMichLeFigaro= new DataUPage(new ObjectId(), 0.8, "www.lefigaro.fr");
			DataUPage jeanMichLEquipe= new DataUPage(new ObjectId(), 0.5, "www.lï¿½quipe.fr");
			DataUPage jeanMichLinux= new DataUPage(new ObjectId(), 0.1, "www.linux.org");
			
			DataUPage leGeekLinux= new DataUPage(new ObjectId(), 0.8, "www.linux.org");
			DataUPage leGeekTechCrunch= new DataUPage(new ObjectId(), 0.95, "www.techcrunch.com");
			DataUPage leGeekOpLib= new DataUPage(new ObjectId(), 0.6, "www.opinionlibre.fr");
			DataUPage leGeekLeMonde= new DataUPage(new ObjectId(), 0.01, "www.lemonde.fr");
			
			DataUPage jeanJauresLeMonde= new DataUPage(new ObjectId(), 0.5, "www.lemonde.fr");
			DataUPage jeanJauresLeFigaro= new DataUPage(new ObjectId(), 0.3, "www.lefigaro.fr");
			DataUPage jeanJauresLEquipe= new DataUPage(new ObjectId(), 0.6, "www.lï¿½quipe.fr");
			DataUPage jeanJauresLHuma= new DataUPage(new ObjectId(), 0.9, "www.lhumanitï¿½.fr");
			
			ArrayList<DataUPage> jeanMichUPage= new ArrayList<DataUPage>();
			jeanMichUPage.add(jeanMichLinux);
			jeanMichUPage.add(jeanMichLeFigaro);
			//jeanMichUPage.add(jeanMichLeMonde);
			jeanMichUPage.add(jeanMichLEquipe);
			
			ArrayList<DataUPage> leGeekUPage= new ArrayList<DataUPage>();
			leGeekUPage.add(leGeekLinux);
			leGeekUPage.add(leGeekOpLib);
			leGeekUPage.add(leGeekTechCrunch);
			leGeekUPage.add(leGeekLeMonde);
			
			ArrayList<DataUPage> jeanJauresUPage= new ArrayList<DataUPage>();
			jeanJauresUPage.add(jeanJauresLeFigaro);
			jeanJauresUPage.add(jeanJauresLeMonde);
			jeanJauresUPage.add(jeanJauresLEquipe);
			jeanJauresUPage.add(jeanJauresLHuma);
			
			//on crï¿½e les persos
			DataUserNode jeanMich = new DataUserNode("jeanMich", new ObjectId(), new ArrayList<DataUserRelation>()  , jeanMichUPage);
			DataUserNode leGeek = new DataUserNode("leGeek", new ObjectId(), new ArrayList<DataUserRelation>()  , leGeekUPage);
			DataUserNode jeanJaures = new DataUserNode("JeanJaures", new ObjectId(), new ArrayList<DataUserRelation>()  , jeanJauresUPage);
			//on implï¿½mente les liens d'amitiï¿½
			DataUserRelation jmfriends = new DataUserRelation(leGeek);
			DataUserRelation lgfriends = new DataUserRelation(jeanMich);
			
			ArrayList<DataUserRelation> jmUserRelations = new ArrayList<DataUserRelation>();
			jmUserRelations.add(jmfriends);
			jmUserRelations.add(new DataUserRelation(jeanJaures));
			jeanMich.setFriends(jmUserRelations);
			ArrayList<DataUserRelation> lgUserRelations = new ArrayList<DataUserRelation>();
			lgUserRelations.add(lgfriends);
			leGeek.setFriends(lgUserRelations);
			
			//on calcule les probas 
			jeanMich.updateProbabilities();
			leGeek.updateProbabilities();
			jeanJaures.updateProbabilities();
			
			
			user = jeanMich;
		}
		else {
			user = Interprete.db2DataUserNodeHard(req.getUserId()); // on reccupere l'utilisateur qui fait sa requete
		}
		
		HashMap<String, ArrayList<Composite>> pages = new HashMap<String, ArrayList<Composite>>(); //on associe url-> avec un object qui contient un user et sa upage
		for (DataUserRelation edge : user.getFriends()) {
			for (DataUPage page: edge.friend.getUPages()) { //on parcourt toutes les pages des recommendeurs
				if (pages.containsKey(page.getUrl())) { 
					// pour les stocker en hashant grace a l'url de la page
					pages.get(page.getUrl()).add(new Composite(edge.friend, page, edge.crossProbability));
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
		
		TreeSet<Composite> bestReco = new TreeSet<Composite>(); //on stocke les trois meilleurs proba  
		int nbReco=Math.min(10, pages.size());
		
		//System.out.println(pages.size() + "/"+ nbReco);
		
		// we initialize the tree with nbReco different values (need to or would erase each other)
		for (int j=0; j<nbReco; j++) bestReco.add(new Composite(null,null,-j));
		
		//on va ensuite calculer toutes les probabilitÃ©s 
		for (ArrayList<AlgoLegerBayes.Composite> cc : pages.values()) { //il y a peut etre une optimisation a faire sur la facon dont on stocke et parcourt cette table de hashage
			for(Composite c :cc) {
				c.crossProbability =  c.crossProbability / c.user.uPageMean * c.page.pageRank;
				//System.out.println( c.page.getUrl()+" : " + c.crossProbability);
				bestReco.add(c);//TODO : IMPORTANT si deux pages ont la même proba, on les écrase!!!
				bestReco.remove(bestReco.first());
			}
		}
//		
//		TreeMap<Double,String> bestsReco = new TreeMap<Double,String>(); //on stocke les trois meilleurs proba  
//		for(int i=0;i<10;i++){bestsReco.put((double)0, "");}
//		//on initialise Ã  3 meilleures reco, nombre qu'on maintient ensuite
//		//TODO : en faite non, on écrase toujours la même clé.
//		
//		//on va ensuite calculer toutes les probabilitÃ©s 
//		for (ArrayList<AlgoLegerBayes.Composite> cc : pages.values()) { //il y a peut etre une optimisation a faire sur la facon dont on stocke et parcourt cette table de hashage
//			for(Composite c :cc) {
//				double proba =  c.crossProbability / c.user.uPageMean * c.page.pageRank;
//				System.out.println( c.page.getUrl()+" : " + proba);
//				bestsReco.put(proba, c.page.getUrl());//TODO : IMPORTANT si deux pages ont la même proba, on les écrase!!!
//				bestsReco.remove(bestsReco.firstKey()); //on maintient seulement 3 meilleures
//			}
//		}
		//System.out.println(bestReco.size());
		
		//This part is used to pick a recommendation randomly among the nbReco best results.
		//TODO: à mettre dans le dispatcher à mon avis, quite à lui passer une liste de reco - score?
		double sum=0;	
		for ( Composite comp : bestReco)
		{
			//System.out.println(comp.crossProbability+" : " + comp.page);
			sum += comp.crossProbability;
		}
		double var= Math.random() * sum;
		Iterator<Composite> it = bestReco.iterator();
		Composite current= it.next();
		double bestProb = current.crossProbability;
		var-=bestProb;
		while (it.hasNext() && var>0 )
		{
			current=it.next();
			bestProb = current.crossProbability;
			var-=bestProb;
		}		
		
		
		/*//TODO: a supprimer
		for ( Composite comp : bestReco)
		{
			System.out.println(comp.crossProbability/sum*100+"%: " + comp.page);
		}*/
		
		return new Recommendation(current.page.getUrl());
	}
	
	
	public class Composite implements Comparable<Composite> {
		DataUserNode user;
		DataUPage page;
		double crossProbability;
		
		public Composite(DataUserNode user, DataUPage page , double proba) {
			this.user=user;
			this.page =page;
			this.crossProbability = proba; //la proba P(A inter B)
		}

		@Override
		public boolean equals(Object obj) {
			try {
				Composite c = (Composite) obj;
				return ( this.page.getMongoId()==c.page.getMongoId() && this.user.getMongoId()==c.user.getMongoId());
			
			}catch(Exception e){ return false;}
			
		}

		
		public int compareTo(Composite arg0) {
			if (this.crossProbability < arg0.crossProbability) return -1;
			if (this.crossProbability > arg0.crossProbability) return 1;
			//same proba;
			System.out.println(this.crossProbability);
			System.out.println(arg0.crossProbability);
			if (this.page==null && arg0.page==null) return 0;
			assert (this.page !=null && arg0.page!=null);
			return this.page.getUrl().compareTo(arg0.page.getUrl());
		}

		@Override
		public String toString() {
			return "Composite [user=" + user + "\n, page=" + page
					+ "\n, crossProbability=" + crossProbability + "]";
		}
		
		

//		private AlgoLegerBayes getOuterType() {
//			return AlgoLegerBayes.this;
//		}
	}
	
	
	
}
