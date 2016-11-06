package tel_ran.currencies.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class CurrenciesConcoleAppl {

	static BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args) {
		
		CurrencyController curMethods = new CurrencyController();
		String str;
		String res;
		
		while (true) {
			try {
				str = getQuery();
				String[] params = str.split(" ");
				
				Method method = CurrencyController.class.getMethod(params[0], String.class);
				res = (String) method.invoke(curMethods,str);

				System.out.println(res);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

	}

	private static String getQuery() throws IOException {
		System.out.println("please write query");
		return console.readLine();
	}
}
