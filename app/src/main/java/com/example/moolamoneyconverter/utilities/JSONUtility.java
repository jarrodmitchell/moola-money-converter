package com.example.moolamoneyconverter.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class JSONUtility {

    private static final String TAG = "JSONUtility";
    private static JSONObject currencyJSON = null;

    public static String getJsonFromString (String json) {
        try {
            currencyJSON = new JSONObject(json);
            return currencyJSON.getString("base");
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //calcutate converted amount using conversion rates from the json object
    public static ArrayList<BigDecimal> getConversions (ArrayList<String> currencies, BigDecimal amount) {
        ArrayList<BigDecimal> convertedAmounts = new ArrayList<>();
        if (currencyJSON != null) {
            Log.d(TAG, "getConversions: " + currencyJSON.toString());
            try {
                JSONObject rates = currencyJSON.getJSONObject("rates");
                for (String currency: currencies) {
                    BigDecimal conversionRate = new BigDecimal(String.valueOf(rates.getDouble(currency)));
                    convertedAmounts.add(conversionRate.multiply(amount));
                }//save base amount
                convertedAmounts.add(amount);
                return convertedAmounts;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}