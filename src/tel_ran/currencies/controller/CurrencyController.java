package tel_ran.currencies.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.bson.Document;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import tel_ran.currencies.model.dao.CurrenciesMongo;
import tel_ran.currencies.model.entiteis.Currency;

public class CurrencyController {
	
	static RestTemplate restTemplate = new RestTemplate();
	static String url = "http://api.fixer.io/latest";
	static Currency currency;
	static CurrenciesMongo currMongo;
	
	public CurrencyController(){
		currMongo = new CurrenciesMongo("mongodb://root:12345@ds143737.mlab.com:43737/", "fixer_currencies");
		currency = restTemplate.getForObject(url, Currency.class);
	}
	

	public String getStatistics(String str) {
		String[] params = str.split(" ");
		StringBuilder res = new StringBuilder();
		Double avgOfYear;
		String[] p = { "", "", "", params[1] };
		double avgOfMon;
		for (int year = 2004; year < 2017; year++) {
			avgOfYear = (double) 0;
			int succ = 12;
			p[1] = year + "";
			for (int month = 0; month < 12; month++) {
				avgOfMon = 0;
				p[2] = month + "";
				String ps = p[0] + " " + p[1] + " " + p[2] + " " + p[3];
				String avgOfMonth = getAvgOfMonth(ps);
				try {
					avgOfMon = Double.parseDouble(avgOfMonth);
				} catch (Exception e) {
					succ--;
				}
				avgOfYear += avgOfMon;
			}
			res.append(year).append(" ").append(avgOfYear / succ).append('\n');
		}
		return res.toString();
	}

	public String getAvgOfYear(String str) {
		String[] params = str.split(" ");
		StringBuilder res = new StringBuilder();
		String[] p = { "", params[1], "", params[2] };
		for (int i = 0; i < 12; i++) {
			p[2] = i + "";
			String ps = p[0] + " " + p[1] + " " + p[2] + " " + p[3];
			res.append((i + 1) + "\t")
				.append(getAvgOfMonth(ps)).append('\n');
		}
		return res.toString();
	}

	public String getAvgOfMonth(String str) {
		String[] params = str.split(" ");
		String res;
		int year = Integer.parseInt(params[1]);
		int month = Integer.parseInt(params[2]);
		Document query = new Document();
		query.append("year", year);
		query.append("month", month);
		List<Currency> list;
		try {
			list = currMongo.findCurrencies(query);
			res = getAvg(list, params[3]).toString();
		} catch (Exception e) {
			res = e.getMessage();
		}
		return res;
	}

	private static Double getAvg(List<Currency> list, String key) {
		Double res = (double) 0;
		for (Currency cur : list) {
			res += cur.getRates().get(key);
		}
		return res / list.size();
	}

	public String getLatest(String str) {
		String[] params = str.split(" ");
		Calendar calendar = new GregorianCalendar();
		Document query;
		List<Currency> listRes = null;
		while (listRes == null && calendar.get(Calendar.YEAR) > 2015) {
			query = new Document("year", calendar.get(Calendar.YEAR)).append("month", calendar.get(Calendar.MONTH))
					.append("day", calendar.get(Calendar.DAY_OF_MONTH));
			listRes = currMongo.findCurrencies(query);
			calendar.add(Calendar.DAY_OF_YEAR, -1);
		}
		return (listRes != null) ? listRes.toString() : null;
	}

	public String createDatabase(String str) {
		String[] params = str.split(" ");
		List<Currency> listCurrencies = new ArrayList<>();
		Calendar calendar = new GregorianCalendar();
		Calendar calendarFrom = new GregorianCalendar(2006, 0, 1);
		StringBuilder url;
		String res = "successfully";

		while (calendar.compareTo(calendarFrom) > 0) {
			url = getUrl(calendar);
			try {
				currency = restTemplate.getForObject(url.toString(), Currency.class);
			} catch (RestClientException e) {
				res = url.toString() + "\n" + e.getMessage();
				break;
			}
			listCurrencies.add(currency);
			calendar.add(Calendar.DAY_OF_YEAR, -1);
		}
		System.out.println(listCurrencies.size() + " days add to list");
		try {
			currMongo.addCurrencies(listCurrencies);
		} catch (Exception e) {
			res = res + "\n" + e.getMessage();
		}
		return res;
	}

	private static StringBuilder getUrl(Calendar calendar) {
		return new StringBuilder("http://api.fixer.io/").append(calendar.get(Calendar.YEAR)).append("-")
				.append(formatNumber(calendar.get(Calendar.MONTH) + 1)).append("-")
				.append(formatNumber(calendar.get(Calendar.DAY_OF_MONTH)));
	}

	private static String formatNumber(int n) {
		return (n < 10) ? "0" + n : "" + n;
	}

	public String addLatest(String str) {
		String[] params = str.split(" ");
		currMongo.addCurrency(currency);
		return "successfully";
	}

	public Double convert(String str) {
		String[] params = str.split(" ");
		Double res = currency.getRates().get(params[1]) * Double.valueOf(params[3])
				/ currency.getRates().get(params[2]);
		return res;
	}

	public Double getCurrency(String str) {
		String[] params = str.split(" ");
		return currency.getRates().get(params[1]);
	}

	public String getCurrencies(String str) {
		String[] params = str.split(" ");
		return currency.getRates().keySet().toString();
	}

}
