import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



public class Interprete {

	static ArrayList<DataCluster> clusters=null;
	static Hashtable<String, DataVector> usersByNames= new Hashtable<String, DataVector>();
	static Hashtable<DataVector, String> usersByUCR = new Hashtable<DataVector, String>();
	
	public void write(String table, String column, String data) {
		//UPDATE table SET value=data WHERE name=column
	}
	
	public List<String> read(Request r) {
		//SELECT ....
		return new ArrayList<String>();
	}

	static public ArrayList<DataCluster> readClusters(Request request) {
		
		return clusters;
	}
	
	static public boolean writeClusters(ArrayList<DataCluster> clusters) {
		Interprete.clusters=clusters;
		return false;
	}

	static public DataVector readUcr(String username) {
		
		return usersByNames.get(username);
	}
	
	static public void writeUcr(String username, DataVector ucr) {
		
		
		usersByNames.put(username,ucr);
		usersByUCR.put(ucr, username);
	}
	
//	public static DataUser getUser(DataVector vect) {
//		
//		return new DataUser(usersByUCR.get(vect), vect);
//	}

	protected static JSONArray post(String serverAdress,Integer serverPort, JSONObject obj ) throws Exception {
		String toBeSent = obj.toString();
		
		OutputStreamWriter writer = null;
		   BufferedReader reader = null;
		   String response = null;
		   try {
		     //encodage des paramètres de la requête
		      String donnees = URLEncoder.encode(toBeSent, "UTF-8");

		      //création de la connection
		      URL url = new URL("http://"+serverAdress+":"+serverPort);
		      URLConnection conn = url.openConnection();
		      conn.setDoOutput(true);
		      
		      //envoi de la requête
		      writer = new OutputStreamWriter(conn.getOutputStream());
		      writer.write(donnees);
		      writer.flush();

		      //lecture de la réponse
		      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		      String ligne;
		      while ((ligne = reader.readLine()) != null) {
		         response +=ligne;
		      }
		   }catch (Exception e) {
		      e.printStackTrace();
		   }finally{
		      try{writer.close();}catch(Exception e){}
		      try{reader.close();}catch(Exception e){}
		   }
		
			 Object objet=JSONValue.parse(response);
			  JSONArray array=(JSONArray)objet;
			return array;
	}
	protected static JSONArray get(String serverAdress,Integer serverPort, String path, JSONObject json ) throws Exception {

		OutputStreamWriter writer = null;
		   BufferedReader reader = null;
		   String response = "";
		   try {

		      //création de la connection
		      URL url = new URL("http://"+serverAdress+":"+serverPort+path+"?req="+json.toString());
		      System.out.println(url.toString());
		      URLConnection conn = url.openConnection();
		      conn.setDoOutput(true);
		      
		      //envoi de la requête
		      writer = new OutputStreamWriter(conn.getOutputStream());
		      writer.flush();

		      //lecture de la réponse
		      System.out.println("Debut de la reponse");
		      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		      String ligne = null;
		      while ((ligne = reader.readLine()) != null) {
		         response = response +ligne;
		      }
		   }catch (Exception e) {
		      e.printStackTrace();
		   }finally{
		      try{writer.close();}catch(Exception e){}
		      try{reader.close();}catch(Exception e){}
		   }

			Object objet=JSONValue.parse(response);
			  JSONArray array=(JSONArray)objet;
			return array;
	}
	
	protected static JSONArray get(String serverAdress,Integer serverPort, String path) throws Exception {

		OutputStreamWriter writer = null;
		   BufferedReader reader = null;
		   String response = "";
		   try {

		      //création de la connection
		      URL url = new URL("http://"+serverAdress+":"+serverPort+path);
		      System.out.println(url.toString());
		      URLConnection conn = url.openConnection();
		      conn.setDoOutput(true);
		      
		      //envoi de la requête
		      writer = new OutputStreamWriter(conn.getOutputStream());
		      writer.flush();

		      //lecture de la réponse
		      System.out.println("Debut de la reponse");
		      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		      String ligne = null;
		      while ((ligne = reader.readLine()) != null) {
		         response = response +ligne;
		      }
		   }catch (Exception e) {
		      e.printStackTrace();
		   }finally{
		      try{writer.close();}catch(Exception e){}
		      try{reader.close();}catch(Exception e){}
		   }

			Object objet=JSONValue.parse(response);
			  JSONArray array=(JSONArray)objet;
			return array;
	}
	
}
