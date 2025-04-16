package acme.constraints;

import java.util.Set;

public class ValidCurrencies {
	
	final static Set<String> VALID_CURRENCIES = Set.of("USD", "EUR", "GBP");
	
	public static boolean isValidCurrency(String currency) {
		return VALID_CURRENCIES.contains(currency);
	}

}
