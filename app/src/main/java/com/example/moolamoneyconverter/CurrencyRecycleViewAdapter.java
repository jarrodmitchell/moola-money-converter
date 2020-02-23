package com.example.moolamoneyconverter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CurrencyRecycleViewAdapter extends RecyclerView.Adapter {

    interface DipslaySaveButtonListener {
        void displaySaveButton();
        void hideSaveButton();
        void setBaseCurrency(String baseCurrency);
        void setFavoriteCurrencies(ArrayList<String> favoriteCurrencies);
    }

    private ArrayList<String> currencies;
    private String base;
    private ArrayList<String> favorites;
    private int id;
    private int index = -1;
    private DipslaySaveButtonListener dipslaySaveButtonListener;

    CurrencyRecycleViewAdapter(ArrayList<String> currencies, int id, Context context) {
        this.currencies = currencies;
        this.id = id;
        if (id == 2) {
            favorites = new ArrayList<>();
        }
        if (context instanceof DipslaySaveButtonListener) {
            dipslaySaveButtonListener = (DipslaySaveButtonListener) context;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_list_row, parent, false);

        view.setClickable(true);

        return new CurrencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final CurrencyViewHolder currencyHolder = (CurrencyViewHolder) holder;
        currencyHolder.setViewDetails(currencies.get(position));

        switch (id) {

            case 0:
                currencyHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        index  = position;
                        notifyDataSetChanged();
                    }
                });
                break;

            case 1:

                currencyHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String code = ((TextView)v.findViewById(R.id.textViewCurrencyCode)).getText().toString();
                        dipslaySaveButtonListener.setBaseCurrency(code);
                        dipslaySaveButtonListener.displaySaveButton();
                        index = position;
                        notifyDataSetChanged();
                    }
                });

                if (index == position) {
                    holder.itemView.setBackgroundColor(Color.BLUE);
                }else {
                    holder.itemView.setBackgroundColor(Color.BLACK);
                }
                break;

            case 2:

                currencyHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String code = ((TextView)v.findViewById(R.id.textViewCurrencyCode)).getText().toString();

                        if (!favorites.contains(code)) {
                            Log.d("code", code);
                            favorites.add(code);
                        }else {
                            favorites.remove(code);
                        }
                        notifyDataSetChanged();
                        for (String f: favorites)  {
                            Log.d("fav", f);
                        }

                        if (favorites.size() > 0) {
                            dipslaySaveButtonListener.displaySaveButton();
                            dipslaySaveButtonListener.setFavoriteCurrencies(favorites);
                        }else {
                            dipslaySaveButtonListener.hideSaveButton();
                        }
                    }
                });

                String code = ((TextView)holder.itemView.findViewById(R.id.textViewCurrencyCode)).getText().toString();

                if (favorites.contains(code)) {
                    holder.itemView.setBackgroundColor(Color.RED);
                }else{
                    holder.itemView.setBackgroundColor(Color.BLACK);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }
}

class CurrencyViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageViewFlag;
    private TextView textViewCurrencyCode;
    private TextView textViewConversionAmount;

    CurrencyViewHolder(@NonNull final View itemView) {
        super(itemView);
        imageViewFlag =  itemView.findViewById(R.id.imageViewFlag);
        textViewCurrencyCode = itemView.findViewById(R.id.textViewCurrencyCode);
        textViewConversionAmount =  itemView.findViewById(R.id.textViewConversionAmount);
    }

    void setViewDetails(String currencyCode)  {
        textViewCurrencyCode.setText(currencyCode);
    }
}
