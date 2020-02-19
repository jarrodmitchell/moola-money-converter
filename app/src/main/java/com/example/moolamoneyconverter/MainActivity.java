package com.example.moolamoneyconverter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private ConnectivityManager connectivityManager;
    GetConversionAsyncTasks getConversionAsyncTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityManager =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        getConversionAsyncTasks = new GetConversionAsyncTasks();
        getConversionAsyncTasks.execute();

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();
    }

    private void checkNetworkConnectivity() {
        int MIN_BANDWIDTH_KBPS = 320;
        Network activeNetwork = connectivityManager.getActiveNetwork();

        if (activeNetwork != null) {
            int bandwidth =
                    connectivityManager.getNetworkCapabilities(activeNetwork)
                            .getLinkDownstreamBandwidthKbps();

            if (bandwidth < MIN_BANDWIDTH_KBPS) {
                // Request a high-bandwidth network
            } else {
                // You already are on a high-bandwidth network, so start your network request
            }
        } else {
            // No active network

        }
    }

    private void getConversionData() {

    }

    private class GetConversionAsyncTasks extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            String url = "https://api.exchangeratesapi.io/latest?base=USD";

            try{
                URL urlObj = new URL(url);
                HttpsURLConnection urlConnection = (HttpsURLConnection) urlObj.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[300];
                for (int i; 0 < (i = inputStream.read(bytes));) {
                    byteArrayOutputStream.write(bytes, 0, i);
                }
                inputStream.close();
                byteArrayOutputStream.close();
                String data = byteArrayOutputStream.toString("UTF-8");
                Log.d("data: ", data);

                urlConnection.disconnect();

            }catch (IOException e) {
                e.printStackTrace();
            }finally {

            }

            return null;
        }
    }
}
