package com.example.moolamoneyconverter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.example.moolamoneyconverter.GetConversionAsyncTask;
import com.example.moolamoneyconverter.adapters.CurrencyRecycleViewAdapter;
import com.example.moolamoneyconverter.utilities.JSONUtility;
import com.example.moolamoneyconverter.utilities.NetworkUtility;
import com.example.moolamoneyconverter.R;
import com.example.moolamoneyconverter.adapters.SliderAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;

public class MainActivity extends WearableActivity implements
        MenuItem.OnMenuItemClickListener,
        SliderAdapter.OpenSelectorActivityListener,
        GetConversionAsyncTask.SaveJSONToFileListener,
        CurrencyRecycleViewAdapter.SaveAmountListener,
        CurrencyRecycleViewAdapter.DeleteCurrencyListener{

    @Override
    public void deleteCurrency(String currencyCode) {
        //remove the selected currency and its corresponding amount from lists
        int index = favoriteCurrencies.indexOf(currencyCode);
        favoriteCurrencies.remove(currencyCode);
        if (convertedAmounts != null) {
            convertedAmounts.remove(index);
        }
        loadPager();
    }

    @Override
    public void saveAmount(BigDecimal amount) {
        //update vies with converted amounts
        convertedAmounts = JSONUtility.getConversions(favoriteCurrencies, amount);
        if (convertedAmounts != null) {
            baseAmount = convertedAmounts.remove(convertedAmounts.size() - 1).toPlainString();
        }
        loadPager();
    }

    public static final String FILE_NAME = "exchange_rates";
    public static final String TAG = "MainActivity";

    private File getFile() {
        return new File(getFilesDir(), FILE_NAME+".json");
    }



    static final int REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_BASE = 300;
    static final int REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_FAVORITES = 600;
    public static final String EXTRA_ID = "extra id";



    @Override
    public void saveJSON(String data) {
        if (data  != null) {
            try {//write currency conversion rates to json file
                FileWriter fileWriter = new FileWriter(getFile());
                fileWriter.write(data);
                fileWriter.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void openSelectorActivity(int id) {
        Intent intent = new Intent(this, CurrencySelectorActivity.class);
        intent.putExtra(EXTRA_ID, id);
        //open selector activity to retrieve base currency
        if (id == 1) {
            startActivityForResult(intent, REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_BASE);
        }else{
            //open selector to retrieve favorite currencies
            startActivityForResult(intent, REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_FAVORITES);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }



    public ArrayList<BigDecimal> convertedAmounts = null;
    public ArrayList<String> favoriteCurrencies = new ArrayList<>();
    public String baseCurrency = null;
    public String baseAmount = null;

    GetConversionAsyncTask getConversionAsyncTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ()

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetworkConnection();
    }

    //check for network connection and saved data before loading view pager
    private void checkNetworkConnection() {
        //if there is no network connection check for file
        if (!NetworkUtility.checkNetworkConnectivity(this)) {
            if (getFile().isFile()) {
                try {
                    //convert input stream to string
                    String jsonString = new String(Files.readAllBytes(getFile().toPath()));
                    if (!jsonString.isEmpty()) {
                        String base = JSONUtility.getJsonFromString(jsonString);
                        if (base != null) {
                            baseCurrency  = base;
                        }
                        loadPager();
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }
            }else{//if there is no connection or saved data, request network access
                NetworkUtility.requestNetworkAccess(this);
            }
        }else {//if so, load view pager
            loadPager();
        }
    }

    //pass currency data to view pager
    private void loadPager() {
        //if a base currency is selected, attempt to get updated conversion data
        if (baseCurrency != null)  {
            if (NetworkUtility.checkNetworkConnectivity(this)) {
                getConversionAsyncTask = new GetConversionAsyncTask(this);
                getConversionAsyncTask.execute(baseCurrency);
            }
        }
        ViewPager viewPager = findViewById(R.id.slideViewPager);
        SliderAdapter sliderAdapter = new SliderAdapter(this, favoriteCurrencies, baseCurrency, baseAmount, convertedAmounts);
        viewPager.setAdapter(sliderAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            switch (requestCode) {
                //new base currency saved
                case REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_BASE:
                    baseCurrency = data.getStringExtra(CurrencySelectorActivity.EXTRA_BASE_CURRENCY);
                    //clear conversions when base is changed
                    convertedAmounts = null;
                    baseAmount = null;
                    loadPager();
                    break;
                    //new fave currencies saved
                case REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_FAVORITES:
                    favoriteCurrencies = data.getStringArrayListExtra(CurrencySelectorActivity.EXTRA_FAVORITE_CURRENCIES);
                    if (favoriteCurrencies != null) {
                        loadPager();
                        //clear conversions when the favorites are changed
                        convertedAmounts = null;
                        baseAmount = null;
                    }
                    break;
            }
        }
    }
}
