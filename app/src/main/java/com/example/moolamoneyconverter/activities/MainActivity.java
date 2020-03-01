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

public class MainActivity extends WearableActivity implements MenuItem.OnMenuItemClickListener, SliderAdapter.OpenSelectorActivityListener, GetConversionAsyncTask.SaveJSONToFileListener, CurrencyRecycleViewAdapter.SaveAmountListener {

    @Override
    public void saveAmount(BigDecimal amount) {
        convertedAmounts = JSONUtility.getConversions(favoriteCurrencies, amount);
        Log.d(TAG, "saveAmount: " + convertedAmounts.toString());
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
            try {
                FileWriter fileWriter = new FileWriter(getFile());
                Log.d(TAG, "saveJSON: " + data);

                Log.d(TAG, "saveJSON: " + getFile().getAbsolutePath());
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



    public ArrayList<BigDecimal> convertedAmounts = null;
    public ArrayList<String> favoriteCurrencies = new ArrayList<>();
    public String baseCurrency = "PHP";

    GetConversionAsyncTask getConversionAsyncTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetworkConnection();
    }

    private void checkNetworkConnection() {
        if (!NetworkUtility.checkNetworkConnectivity(this)) {
            if (getFile().isFile()) {
                try {
                    //convert input stream to string
                    String jsonString = new String(Files.readAllBytes(getFile().toPath()));
                    if (!jsonString.isEmpty()) {
                        JSONUtility.getJsonFromString(jsonString);
                        loadPager();
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                NetworkUtility.requestNetworkAccess(this);
            }
        }else {
            Log.d(TAG, "checkNetworkConnection: load pager");
            loadPager();
        }
    }

    private void loadPager() {
        if (baseCurrency != null)  {
            if (NetworkUtility.checkNetworkConnectivity(this)) {
                getConversionAsyncTask = new GetConversionAsyncTask(this);
                getConversionAsyncTask.execute(baseCurrency);
            }

            if (!favoriteCurrencies.contains(baseCurrency) && favoriteCurrencies.size() > 0) {

                favoriteCurrencies.remove(baseCurrency);

                String holder = favoriteCurrencies.get(0);

                favoriteCurrencies.set(0, baseCurrency);

                favoriteCurrencies.add(holder);
            }
        }
        ViewPager viewPager = findViewById(R.id.slideViewPager);
        LinearLayout dotLayout = findViewById(R.id.linearLayoutDot);
        SliderAdapter sliderAdapter = new SliderAdapter(this, favoriteCurrencies, baseCurrency, convertedAmounts);
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
                    if (baseCurrency != null) {
                        loadPager();
                    }
                    break;
                    //new fave currencies saved
                case REQUEST_OPEN_SELECTOR_ACTIVITY_FOR_FAVORITES:
                    favoriteCurrencies = data.getStringArrayListExtra(CurrencySelectorActivity.EXTRA_FAVORITE_CURRENCIES);
                    if (favoriteCurrencies != null) {
                        loadPager();
                        convertedAmounts = null;
                    }
                    break;
            }
        }
    }
}
