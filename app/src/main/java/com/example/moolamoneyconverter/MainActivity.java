package com.example.moolamoneyconverter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends WearableActivity implements MenuItem.OnMenuItemClickListener, SliderAdapter.OpenSelectorActivityListener {


    static final int REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_BASE = 300;
    static final int REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_FAVORITES = 600;
    public static final String EXTRA_ID = "extra id";

    @Override
    public void openSelectorActivity(int id) {
        Intent intent = new Intent(this, CurrencySelectorActivity.class);
        intent.putExtra(EXTRA_ID, id);
        if (id == 1) {
            startActivityForResult(intent, REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_BASE);
        }else{
            startActivityForResult(intent, REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_FAVORITES);
        }
        }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }



    public ArrayList<String> favoriteCurrencies = new ArrayList<>();
    public String baseCurrency = null;

    private ConnectivityManager connectivityManager;
    GetConversionAsyncTasks getConversionAsyncTasks;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPager();

        connectivityManager =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        getConversionAsyncTasks = new GetConversionAsyncTasks();
//        getConversionAsyncTasks.execute();


        // Enables Always-on
        setAmbientEnabled();
    }

    private void loadPager() {
        if (baseCurrency != null && !favoriteCurrencies.contains(baseCurrency) && favoriteCurrencies.size() > 0) {
            String holder = favoriteCurrencies.get(0);
            favoriteCurrencies.add(holder);
            favoriteCurrencies.set(0, baseCurrency);
        }else {
            favoriteCurrencies.add(baseCurrency);
        }
        ViewPager viewPager = findViewById(R.id.slideViewPager);
        LinearLayout dotLayout = findViewById(R.id.linearLayoutDot);
        SliderAdapter sliderAdapter = new SliderAdapter(this, favoriteCurrencies, baseCurrency);
        viewPager.setAdapter(sliderAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_BASE:
                baseCurrency = data.getStringExtra(CurrencySelectorActivity.EXTRA_BASE_CURRENCY);
                if (baseCurrency != null) {
                    loadPager();
                }
                break;

            case REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_FAVORITES:
                favoriteCurrencies = data.getStringArrayListExtra(CurrencySelectorActivity.EXTRA_FAVORITE_CURRENCIES);
                if (favoriteCurrencies != null) {
                    loadPager();
                }
                break;
        }
    }

    private void checkNetworkConnectivity() {
        int MIN_BANDWIDTH_KBPS = 320;
        Network activeNetwork = connectivityManager.getActiveNetwork();

        if (activeNetwork != null) {
            int bandwidth =
                    Objects.requireNonNull(connectivityManager.getNetworkCapabilities(activeNetwork))
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

    private static class GetConversionAsyncTasks extends AsyncTask {

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
