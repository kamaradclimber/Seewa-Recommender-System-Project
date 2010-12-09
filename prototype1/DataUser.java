import org.bson.types.ObjectId;


public class DataUser implements Data {

	private String name = "";
	private DataVector ucr; //on devrait ne se passer que des DataUser à la place  des DataVector (a reflechir) TODO
	private ObjectId id; //l'id qui est dans mongo
	
	
	public DataUser(String name, DataVector ucr, ObjectId id)
	{
		this.id = id;
		this.name=name;
		this.ucr=ucr;
	}

	public String getName() {
		return this.name;
	}
	
	@Override
	public ObjectId getMongoId() {
		return this.id;
	}

}
