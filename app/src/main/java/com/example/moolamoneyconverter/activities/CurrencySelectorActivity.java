package com.example.moolamoneyconverter.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moolamoneyconverter.adapters.CurrencyRecycleViewAdapter;
import com.example.moolamoneyconverter.R;

import java.util.ArrayList;
import java.util.Arrays;

public class CurrencySelectorActivity extends WearableActivity  implements CurrencyRecycleViewAdapter.DipslaySaveButtonListener {

    public static final String EXTRA_BASE_CURRENCY = "extra base currency";
    public static final String EXTRA_FAVORITE_CURRENCIES  =  "extra favorite currencies";

    @Override
    public void hideSaveButton() {
        buttonSave.setVisibility(View.GONE);
    }

    @Override
    public void displaySaveButton() {
        buttonSave.setVisibility(View.VISIBLE);
    }

    @Override
    public void setBaseCurrency(String baseCurrency) {
        this.currencyCode = baseCurrency;
    }

    @Override
    public void setFavoriteCurrencies(ArrayList<String> favoriteCurrencies) {
        this.selectedCurrencies = favoriteCurrencies;
    }

    int id = 0;
    ArrayList<String> currencyCodes = new ArrayList<String>(Arrays.asList(
            "CAD", "HKD", "ISK", "PHP", "DKK", "HUF", "CZK", "GBP", "RON", "SEK", "IDR",
            "INR", "BRL", "RUB", "HRK", "JPY", "THB", "CHF", "EUR", "MYR", "BGN", "TRY",
            "CNY", "NOK", "NZD", "ZAR", "USD", "MXN", "SGD", "AUD", "ILS", "KRW", "PLN", ""));

    Context context;
    String currencyCode;
    ArrayList<String> selectedCurrencies;
    RecyclerView recyclerViewCurrencies;
    Button buttonSave;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_selector);

        if (getIntent() != null) {
            id = getIntent().getIntExtra(MainActivity.EXTRA_ID, 0);
        }

        recyclerViewCurrencies = findViewById(R.id.recyclerViewPickBase);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerViewCurrencies.setLayoutManager(manager);
        CurrencyRecycleViewAdapter adapter = new CurrencyRecycleViewAdapter(currencyCodes, id, this, null);
        recyclerViewCurrencies.setAdapter(adapter);

        buttonSave = findViewById(R.id.buttonSaveBase);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
    }

    void closeActivity() {
        Intent returnIntent = new Intent();

        switch (id)  {
            case 1:
                //return selected base currency
                returnIntent.putExtra(EXTRA_BASE_CURRENCY, currencyCode);
            case  2:
                //return selected favorite currencies
                returnIntent.putExtra(EXTRA_FAVORITE_CURRENCIES, selectedCurrencies);
        }

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
