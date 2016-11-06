package tel_ran.currencies.model.entiteis;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class Currency {

	Date date;
	HashMap<String,Double> rates;
	int year;
	int month;
	int day;
		
	public Currency() {
		super();
	}

	public Currency(int year, int month, int day, HashMap<String, Double> rates) {
		super();
		this.rates = rates;
		this.year = year;
		this.month = month;
		this.day = day;
		this.date = new GregorianCalendar(year, month, day).getTime();
	}

	@Override
	public String toString() {
		return "Currency [date=" + date + ", rates=" + rates + "]";
	}

	public HashMap<String,Double> getRates(){
		return rates;
	}

	public Date getDate() {
		return date;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMounth(int mounth) {
		this.month = mounth;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

}
