package com.example.moolamoneyconverter.utilities;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class JSONUtility {

    private static final String TAG = "JSONUtility";

    private static JSONObject currencyJSON = null;

    public static void getJsonFromString (String json) {
        try {
            currencyJSON = new JSONObject(json);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<BigDecimal> getConversions(ArrayList<String> currencies, BigDecimal amount) {
        ArrayList<BigDecimal> convertedAmounts = new ArrayList<>();
        if (currencyJSON != null) {
            Log.d(TAG, "getConversions: " + currencyJSON.toString());
            try {
                JSONObject rates = currencyJSON.getJSONObject("rates");
                for (String currency: currencies) {
                    BigDecimal conversionRate = new BigDecimal(String.valueOf(rates.getDouble(currency)));
                    convertedAmounts.add(conversionRate.multiply(amount));
                }
                return convertedAmounts;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}