package tel_ran.databases.mongo;
import java.util.*;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
	static Map<String,MongoConnection> mongoConnections = new LinkedHashMap<>();
	MongoDatabase dataBase;
	MongoClient client;
	
	private MongoConnection(String uriStr, String database){
		MongoClientURI uri = new MongoClientURI(uriStr + database);
		client = new MongoClient(uri);
		dataBase = client.getDatabase(database);
	}
	synchronized public static MongoConnection getMongoConnection(String uriStr, String database){
		MongoConnection res = mongoConnections.get(database);
		if(res == null){
			res = new MongoConnection(uriStr,database);
			mongoConnections.put(database, res);
		}
		return res;
	}
	public MongoDatabase getDataBase(){
		return dataBase;
	}
}