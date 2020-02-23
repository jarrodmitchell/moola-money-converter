package com.example.moolamoneyconverter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class SliderAdapter  extends PagerAdapter {

    interface OpenSelectorActivityListener {
        void openSelectorActivity(int id);
    }

    OpenSelectorActivityListener openSelectorActivityListener;
    private Context context;
    private ArrayList<String> currencies;
    private String base;

    SliderAdapter(Context context, ArrayList<String> currencies, String base) {
        this.context = context;
        this.currencies = currencies;
        this.base = base;
        if (context instanceof OpenSelectorActivityListener) {
            openSelectorActivityListener = (OpenSelectorActivityListener) context;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (layoutInflater != null) {
            switch (position) {
                case 0:
                    View view0 = layoutInflater.inflate(R.layout.list_slide_layout, container, false);
                    final RecyclerView recyclerViewCurrencies = view0.findViewById(R.id.recyclerViewCurrencies);
                    TextView textViewEmptyList = view0.findViewById(R.id.textViewEmptyCurrencyList);

                    if (currencies.size() > 0 && base != null) {
                        recyclerViewCurrencies.setVisibility(View.VISIBLE);
                        textViewEmptyList.setVisibility(View.GONE);
                        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
                        recyclerViewCurrencies.setLayoutManager(manager);
                        CurrencyRecycleViewAdapter adapter = new CurrencyRecycleViewAdapter(currencies, 0, context);
                        recyclerViewCurrencies.setAdapter(adapter);
                    }else{
                        recyclerViewCurrencies.setVisibility(View.GONE);
                        textViewEmptyList.setVisibility(View.VISIBLE);
                    }
                    container.addView(view0);
                    return view0;


                case 1:

                    View view1 = layoutInflater.inflate(R.layout.settings_slide_layout, container, false);

                    Button buttonBase = view1.findViewById(R.id.buttonSetBase);
                    buttonBase.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openSelectorActivityListener.openSelectorActivity(1);
                        }
                    });

                    Button buttonFavorites = view1.findViewById(R.id.buttonSetFavorites);
                    buttonFavorites.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openSelectorActivityListener.openSelectorActivity(2);
                        }
                    });
                    container.addView(view1);
                    return view1;
            }
        }
        return new View(context);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
