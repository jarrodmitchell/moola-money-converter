package com.example.moolamoneyconverter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.moolamoneyconverter.R;
import com.example.moolamoneyconverter.adapters.CurrencyRecycleViewAdapter;
import com.example.moolamoneyconverter.utilities.NetworkUtility;

import java.math.BigDecimal;
import java.util.ArrayList;

public class SliderAdapter  extends PagerAdapter {

    public interface OpenSelectorActivityListener {
        void openSelectorActivity(int id);
    }

    private OpenSelectorActivityListener openSelectorActivityListener;
    private Context context;
    private ArrayList<String> currencies;
    private String base = null;
    private String baseAmount = null;
    private ArrayList<BigDecimal> convertedAmounts = null;

    public SliderAdapter(Context context, ArrayList<String> currencies, String base, String baseAmount, ArrayList<BigDecimal> convertedAmounts) {
        this.context = context;
        this.currencies = currencies;
        this.base = base;
        if (baseAmount != null) {
            this.baseAmount = baseAmount;
        }
        if (context instanceof OpenSelectorActivityListener) {
            openSelectorActivityListener = (OpenSelectorActivityListener) context;
        }
        if (convertedAmounts != null) {
            this.convertedAmounts = convertedAmounts;
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

                //inflate main screen with currency list
                case 0:

                    View view0 = layoutInflater.inflate(R.layout.list_slide_layout, container, false);
                    final RecyclerView recyclerViewCurrencies = view0.findViewById(R.id.recyclerViewCurrencies);
                    TextView textViewEmptyList = view0.findViewById(R.id.textViewEmptyCurrencyList);

                    final LinearLayout baseCurrencyLayout = view0.findViewById(R.id.linearLayoutBaseCurrency);
                    TextView textViewNoBase = view0.findViewById(R.id.textViewNoBaseCurrency);
                    TextView textViewBaseCurrencyCode = view0.findViewById(R.id.baseTextViewCurrencyCode);

                    //populate views if values are present
                    if (currencies.size() > 0 && base != null) {
                        ImageView imageViewFlag = view0.findViewById(R.id.baseImageViewFlag);
                        TextView textViewBaseAmount = view0.findViewById(R.id.baseTextViewConversionAmount);

                        if (baseAmount != null) {
                            textViewBaseAmount.setText(baseAmount);
                        }
                        baseCurrencyLayout.setVisibility(View.VISIBLE);
                        textViewNoBase.setVisibility(View.GONE);
                        textViewBaseCurrencyCode.setText(base);
                        if (base.equals("TRY")) {
                            base += "2";
                        }
                        imageViewFlag.setImageDrawable((ContextCompat.getDrawable(context, context.getResources().getIdentifier(base.toLowerCase(), "drawable", context.getPackageName()))));

                        recyclerViewCurrencies.setVisibility(View.VISIBLE);
                        textViewEmptyList.setVisibility(View.GONE);
                        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
                        recyclerViewCurrencies.setLayoutManager(manager);
                        CurrencyRecycleViewAdapter adapter = new CurrencyRecycleViewAdapter(currencies, 0, context, convertedAmounts);
                        recyclerViewCurrencies.setAdapter(adapter);

                    }
                    if (currencies.size() == 0) {
                        recyclerViewCurrencies.setVisibility(View.GONE);
                        textViewEmptyList.setVisibility(View.VISIBLE);
                    }
                    if (base == null) {
                        baseCurrencyLayout.setVisibility(View.GONE);
                        textViewNoBase.setVisibility(View.VISIBLE);
                    }else {
                        baseCurrencyLayout.setVisibility(View.VISIBLE);
                        textViewNoBase.setVisibility(View.GONE);
                        textViewBaseCurrencyCode.setText(base);
                    }
                    container.addView(view0);
                    return view0;


                    //inflate settings screen with buttons to select base or favorite currencies
                case 1:

                    View view1 = layoutInflater.inflate(R.layout.settings_slide_layout, container, false);

                    //Base currency can only be changed when there is a network connection
                    //when the buttons are tapped they will open the selector activity
                    Button buttonBase = view1.findViewById(R.id.buttonSetBase);
                    if (NetworkUtility.checkNetworkConnectivity(context)) {
                        buttonBase.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openSelectorActivityListener.openSelectorActivity(1);
                            }
                        });
                    }else {//inform user
                        buttonBase.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "Network Connection needed to change base currency", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

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
