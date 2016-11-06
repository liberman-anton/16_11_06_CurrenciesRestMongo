package tel_ran.currencies.model.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;

import tel_ran.currencies.model.entiteis.Currency;
import tel_ran.databases.mongo.MongoConnection;

public class CurrenciesMongo {
	private static final String COLLECTION_NAME = "currencies";
	private MongoCollection<Document> currencies;

	public CurrenciesMongo(String uriStr, String databaseName) {
		MongoConnection mongoConnection = MongoConnection.getMongoConnection(uriStr, databaseName);
		currencies = mongoConnection.getDataBase().getCollection(COLLECTION_NAME);
	}
	
	private void setIndexes(){
		IndexOptions options = new IndexOptions();
		currencies.createIndex(new Document("year", -1), options.unique(false));
		currencies.createIndex(new Document("month", -1), options.unique(false));
	}

	public boolean addCurrencies(List<Currency> listCurrencies) {
		setIndexes();
		currencies.insertMany(getDocuments(listCurrencies));
		return true;
	}

	private List<Document> getDocuments(List<Currency> listCurrencies) {
		ArrayList<Document> res = new ArrayList<>();
		for (Currency currency : listCurrencies) {
			res.add(getDocument(currency));
		}
		return res;
	}

	public boolean addCurrency(Currency currency) {
		currencies.insertOne(getDocument(currency));
		return true;
	}

	private Document getDocument(Currency currency) {
		Document res = new Document();
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(currency.getDate());
		res.put("year", calendar.get(Calendar.YEAR));
		res.put("month", calendar.get(Calendar.MONTH));
		res.put("day", calendar.get(Calendar.DAY_OF_MONTH));
		
		HashMap<String,Double> rates = currency.getRates();
		rates.put("EUR", (double) 1);
		res.put("rates", rates);
		
		return res;
	}

	public List<Currency> findCurrencies(Document query){
		FindIterable<Document> resIterable = currencies.find(query);
		return getListOfCurrencies(resIterable);
	}

	private List<Currency> getListOfCurrencies(FindIterable<Document> resIterable) {
		ArrayList<Currency> res = new ArrayList<>();
		if(resIterable == null || !resIterable.iterator().hasNext())
			return null;
		for(Document document : resIterable)
			res.add(getCurrency(document));
		return res;
	}

	private Currency getCurrency(Document document) {
		ObjectMapper mapper = new ObjectMapper();
		int year = document.getInteger("year");
		int month = document.getInteger("month");
		int day = document.getInteger("day");
		HashMap<String, Double> rates = null;
		try {
			rates = mapper.readValue(mapper.writeValueAsString(document.get("rates")),
					new TypeReference<HashMap<String, Double>>() {});
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return new Currency(year,month,day,rates);
	}

	public String agr() {
		Document match = new Document();
		match.put("year", 2016);
		match.put("month", 10);

		Document groupFields = new Document( "_id", 0);
		groupFields.put("average", new Document( "$avg", "day"));
		Document group = new Document("$group", groupFields);
		List<Document> list = new ArrayList<>();
		list.add(match);
		list.add(group);
		
		AggregateIterable<Document> out = currencies.aggregate(list);
		
		return out.first().toString();
	}

}
