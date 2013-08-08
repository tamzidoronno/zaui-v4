package com.thundashop.core.common;

import com.google.gson.Gson;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Setting;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;


public class ExchangeConvert {
    private static HashMap<String, ExchangeRate> rates = new HashMap();
    
    public static double calculateExchangeRate(HashMap<String, Setting> storeSettings, double price) throws ErrorException {
        double rate = getExchangeRate(storeSettings);
        price *= rate;
        price *= 100;
        price = Math.round(price);
        price /= 100;
        return price;
    }
    
    private static double getExchangeRate(String from_currency, String to_currency) throws ErrorException {
        try {
            String key = from_currency + "." + to_currency;
            if(rates.containsKey(key)) {
                ExchangeRate rate = rates.get(key);
                if((System.currentTimeMillis() - rate.lastUpdated.getTime()) < 60000) {
                    return rate.rate;
                }
            }
            
            try {
                String data = readDataFromUrl("http://rate-exchange.appspot.com/currency?from="+from_currency+"&to="+to_currency);
                Gson gson = new Gson();
                ExchangeRate rate = gson.fromJson(data, ExchangeRate.class);
                rate.lastUpdated = new Date();
                System.out.println(data);
                rates.put(key, rate);
                return rates.get(key).rate;
            }catch(Exception e) {
                if(rates.containsKey(key)) {
                    return rates.get(key).rate;
                } else {
                    throw new ErrorException(1016);
                }
            }
        } catch (Exception e) {
        }
        return 1.0;
    }

    private static String readDataFromUrl(String url) throws IOException {
        URL oracle = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine;
        String result = "";
        while ((inputLine = in.readLine()) != null) {
            result += inputLine;
        }
        in.close();
        return result;
    }

    public static double getExchangeRate(HashMap<String, Setting> storeSettings) throws ErrorException {
        if (storeSettings.containsKey("currencycode") && storeSettings.containsKey("currencycode_base")) {
            Setting to = storeSettings.get("currencycode");
            Setting from = storeSettings.get("currencycode_base");
            if (to.value != null && to.value.trim().length() > 0 && from.value != null && from.value.trim().length() > 0) {
                return ExchangeConvert.getExchangeRate(from.value, to.value);
            }
        }
        return 1.0;
    }
}
class ExchangeRate {
    public String to;
    public String from;
    public Double rate;
    public Date lastUpdated;
}
