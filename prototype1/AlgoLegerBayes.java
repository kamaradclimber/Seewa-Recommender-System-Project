import java.awt.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
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
		DataUserNode user;
		if (req.getUrl()=="test" ) 
		{
			//on cr�e les persos
			Date t = new Date();
			DataUserNode jeanMich = new DataUserNode( new ObjectId(t,1), null  , null);
			DataUserNode leGeek = new DataUserNode( new ObjectId(t,2), null , null);
			DataUserNode jeanJaures = new DataUserNode( new ObjectId(t,3), null  , null);
			
			ObjectId jeanMichId= jeanMich.getId();
			ObjectId leGeekId= leGeek.getId();
			ObjectId jeanJauresId= jeanJaures.getId();
			
			//g�n� des Upages
			//DataUPage jeanMichLeMonde= new DataUPage(new ObjectId(), 0.7, "www.lemonde.fr");
			DataUPage jeanMichLeFigaro= new DataUPage(new ObjectId(t,1),jeanMichId, 0.8, "www.lefigaro.fr");
			DataUPage jeanMichLEquipe= new DataUPage(new ObjectId(t,2),jeanMichId, 0.5, "www.l�quipe.fr");
			DataUPage jeanMichLinux= new DataUPage(new ObjectId(),jeanMichId, 0.1, "www.linux.org");
			
			DataUPage leGeekLinux= new DataUPage(new ObjectId(),leGeekId, 0.8, "www.linux.org");
			DataUPage leGeekTechCrunch= new DataUPage(new ObjectId(),leGeekId, 0.95, "www.techcrunch.com");
			DataUPage leGeekOpLib= new DataUPage(new ObjectId(),leGeekId, 0.6, "www.opinionlibre.fr");
			DataUPage leGeekLeMonde= new DataUPage(new ObjectId(),leGeekId, 0.01, "www.lemonde.fr");
			
			DataUPage jeanJauresLeMonde= new DataUPage(new ObjectId(),jeanJauresId, 0.5, "www.lemonde.fr");
			DataUPage jeanJauresLeFigaro= new DataUPage(new ObjectId(),jeanJauresId, 0.3, "www.lefigaro.fr");
			DataUPage jeanJauresLEquipe= new DataUPage(new ObjectId(),jeanJauresId, 0.6, "www.l�quipe.fr");
			DataUPage jeanJauresLHuma= new DataUPage(new ObjectId(),jeanJauresId, 0.9, "www.lhumanit�.fr");
			
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
			
			jeanMich.setUPages(jeanMichUPage);
			leGeek.setUPages(leGeekUPage);
			jeanJaures.setUPages(jeanJauresUPage);
			
			
			//on impl�mente les liens d'amiti�
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

			Interprete.DataUserNode2db(jeanMich);
			
			
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
		int nbResult=Math.min(nbReco, pages.size());
		
		for (int j=0; j<nbResult; j++) 
			bestReco.add(new Composite(null,null,-j));
		
		
		//on va ensuite calculer toutes les probabilités 
		for (ArrayList<AlgoLegerBayes.Composite> cc : pages.values()) { //il y a peut etre une optimisation a faire sur la facon dont on stocke et parcourt cette table de hashage
			for(Composite c :cc) {
				c.crossProbability =  c.crossProbability / c.user.uPageMean * c.page.pageRank;
				System.out.println("calcul proba pour la page"+ c.page.getUrl()+" : " + c.crossProbability);
				bestReco.add(c);
				bestReco.remove(bestReco.first());
			}
		}
		double sum=0;	
		for ( Composite comp : bestReco)
		{
			System.out.println(comp.crossProbability+" : " + comp.page);
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
		
		return new Recommendation(bestReco.last().toString());
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
			if (this == arg0) return 0;
			if (this.crossProbability < arg0.crossProbability) return -1;
			if (this.crossProbability > arg0.crossProbability) return 1;
			//same proba;
			if (this.page==null && arg0.page==null) return 0;
			if (this.page==null) return -1;
			if (arg0.page==null) return 1;
			return this.page.getUrl().compareTo(arg0.page.getUrl());
		}

		@Override
		public String toString() {
			if (user==null)
				return "Composite [Proba=" + crossProbability
				+ ", page=" + page + ", user=" + user+ "]";
			return "Composite [Proba=" + crossProbability
					+ ", page=" + page + ", user=" + user.getId() + "]";
		}

	}
	
	
	
}
