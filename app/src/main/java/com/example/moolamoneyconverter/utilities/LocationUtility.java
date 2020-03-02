package com.example.moolamoneyconverter.utilities;

import android.content.Context;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

public class LocationUtility {

    public static Locale locale;

    //attempt to get current location currency
    public static String getLocation(Context context) {
        try{
            HashMap<String, String> countries = new HashMap<>();
            countries.put("CAN", "CAD");
            countries.put("HKG", "HKD");
            countries.put("ISL", "ISK");
            countries.put("PHL", "PHP");
            countries.put("DNK", "DKK");
            countries.put("HUN", "HUF");
            countries.put("CZE", "CZK");
            countries.put("GBR", "GBP");
            countries.put("ROU", "RON");
            countries.put("SWE", "SEK");
            countries.put("IDN", "IDR");
            countries.put("IND", "INR");
            countries.put("BRA", "BRL");
            countries.put("RUS", "RUB");
            countries.put("HRV", "HRK");
            countries.put("JPN", "JPY");
            countries.put("THA", "THB");
            countries.put("CHE", "CHF");
            countries.put("MYS", "MYR");
            countries.put("BGR", "BGN");
            countries.put("TUR", "TRY");
            countries.put("CHN", "CNY");
            countries.put("NOR", "NOK");
            countries.put("NZL", "NZD");
            countries.put("ZAF", "ZAR");
            countries.put("USA", "USD");
            countries.put("MEX", "MXN");
            countries.put("SGP", "SGD");
            countries.put("AUS", "AUD");
            countries.put("CXR", "AUD");
            countries.put("CCK", "AUD");
            countries.put("HMD", "AUD");
            countries.put("KIR", "AUD");
            countries.put("NRU", "AUD");
            countries.put("NFK", "AUD");
            countries.put("TUV", "AUD");
            countries.put("ISR", "ILS");
            countries.put("KOR", "KRW");
            countries.put("POL", "PLN");
            countries.put("DEU", "EUR");
            Locale locale = context.getResources().getConfiguration().getLocales().get(0);
            String country = locale.getISO3Country();

            if (countries.containsKey(country)) {
                return countries.get(country);
            }
            countries.clear();

        }catch (MissingResourceException e) {
            e.printStackTrace();
        }
        return null;
    }
}
