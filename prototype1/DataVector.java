import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;




@SuppressWarnings("serial")
public class DataVector extends Hashtable<String, Double> implements Data  {
	String name;
	private int arrayId = 0;
	private ObjectId userID; //on stocke l'id de l'user qui est associé au vector, si on parle dun user sinon il est null
	//DONE: et si on mettait userID, ou juste "Objet user;"? c'est fait (greg)
	
	private static int IdCount =0;
	
	
	public DataVector(ObjectId userID ) {
		super();
		this.arrayId = IdCount;
		IdCount++;
		this.userID = userID; // ceci correspond a l'id de l'utilisateur auquel le data est eventuellement lié
		BasicDBObject query = new BasicDBObject("user",userID);
		
		DBCursor pageviewedbyuser = Interprete.upages.find(query);
		
		Hashtable<String, Integer> nbPagePerTheme = new Hashtable<String, Integer>();

		BasicDBList themes=null;
		double pageRank = 0;
		while (pageviewedbyuser.hasNext())
		{
			BasicDBObject upage = (BasicDBObject) pageviewedbyuser.next();
			try {
				  themes = (BasicDBList) upage.get("themes");
				  if (themes ==null)
					  themes=new BasicDBList();
			} catch (Exception e){
				themes=new BasicDBList();
				e.printStackTrace();
			}try {
				  pageRank =  Double.parseDouble(upage.get("pageRank").toString());
			} catch (Exception e){
				System.out.println("failed1 :"+upage.get("pageRank"));
				pageRank=0;
			}try{
				  name = (String ) upage.get("userName");
			} catch (Exception e){
				System.out.println("failed2 :"+upage.get("userName"));
				e.printStackTrace();
			}
			//System.out.println(upage);
			for (Object listItem:themes)
			{
				String themeName = ((BasicDBObject) listItem).getString("name");
				if (!this.containsKey(themeName)){
					this.put(themeName, pageRank);
					nbPagePerTheme.put(themeName, 1);
				}else{
					this.put(themeName, this.get(themeName)+pageRank);
					nbPagePerTheme.put(themeName, nbPagePerTheme.get(themeName)+1);
				}
			}
		}
	
		for (String theme : nbPagePerTheme.keySet())
		{
			this.put(theme, this.get(theme)/nbPagePerTheme.get(theme));
		}
		
		
	}
	
	
	
	public int getArrayId() {
		return this.arrayId;
	}
	
	public void setArrayId(int id) {
		this.arrayId = id;
	}
	
	public boolean equals(Object o) {
		try {
			DataVector v = (DataVector) o;
			for(String key : this.keySet()) {
				if (!v.containsKey(key) || v.get(key) != this.get(key))  {
					return false;
				}
			}
			return (v.size()==this.size());
		} catch (ClassCastException ex) {
			return false;
		}
	}



	
	public double getOrZero(String key) { //renvoie la valeur si la clé existe et zéro sinon
		if (!this.containsKey(key)) {
			return 0;
		}
		return this.get(key);
	}


	@Override
	public ObjectId getMongoId() {
		return this.userID;
	}



	@Override
	public String toString() {
		String tostring = "DataVector [arrayId=" + arrayId + ", name=" + name
				+ ", userID=" + userID + "]\n";
		TreeMap<Double, String> temp = new TreeMap<Double, String>();
		for (String theme : this.keySet())
		{
			temp.put(this.get(theme), theme );
		}
		
		for (Double pr: temp.tailMap(0.2).keySet())
			tostring += temp.get(pr) + " : " + pr +"\n";
		
		
		return tostring;
	}
	
}
