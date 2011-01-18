import org.bson.types.ObjectId;


public class Recommendation {

	String description="";
	String url;
	ObjectId userID;
	ObjectId category;
	
	ObjectId getCategory() {
		return category;
	}

	void setCategory(ObjectId category) {
		this.category = category;
	}

	String getDescription() {
		return description;
	}

	void setDescription(String description) {
		this.description = description;
	}

	String getUrl() {
		return url;
	}

	void setUrl(String url) {
		this.url = url;
	}

	ObjectId getUserID() {
		return userID;
	}

	void setUserID(ObjectId userID) {
		this.userID = userID;
	}

	public Recommendation(String name) {
		description = name;
		
	}
	
	public String toString()
	{
		return description;
	}

	public Recommendation() {
		//do nothing
	}

}
