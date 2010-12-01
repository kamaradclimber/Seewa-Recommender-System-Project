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

	
}
