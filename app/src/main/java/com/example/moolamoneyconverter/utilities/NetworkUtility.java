package com.example.moolamoneyconverter.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtility {

    //check whether internet is turned on
    public static boolean checkNetworkConnectivity(Context context) {
        int MIN_BANDWIDTH_KBPS = 320;
        ConnectivityManager connectivityManager =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();

            // No active network
            return activeNetwork != null;
        }
        return false;
    }

    //retrieve conversion data from api
    public static String getConversionData(URL urlObj) {

        try {

            HttpsURLConnection urlConnection = (HttpsURLConnection) urlObj.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            //convert input stream to string
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[300];
            for (int i; 0 < (i = inputStream.read(bytes));) {
                byteArrayOutputStream.write(bytes, 0, i);
            }
            inputStream.close();
            byteArrayOutputStream.close();

            String data = byteArrayOutputStream.toString("UTF-8");

            Log.d("data: ", data);
            JSONUtility.getJsonFromString(data);

            urlConnection.disconnect();
            return data;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //alert user that the app requires network access
    public static void requestNetworkAccess(Context context) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("No Internet Access!");
        alert.setMessage("Please enable internet to get conversion rates");
        alert.setCancelable(false);
        alert.setPositiveButton("OK", null);
        alert.show();
    }
}
