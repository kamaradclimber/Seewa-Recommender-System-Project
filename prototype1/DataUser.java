
public class DataUser implements Data {

	private String name = "";
	private DataVector ucr;
	private Integer id; //l'id qui est dans mongo
	
	
	public DataUser(String name, DataVector ucr, Integer id)
	{
		this.id = id;
		this.name=name;
		this.ucr=ucr;
	}

	public String getName() {
		return this.name;
	}
	
	@Override
	public Integer getMongoId() {
		return this.id;
	}

}
