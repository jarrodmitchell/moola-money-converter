package com.example.moolamoneyconverter;

import android.os.AsyncTask;
import android.util.Log;

import com.example.moolamoneyconverter.utilities.JSONUtility;
import com.example.moolamoneyconverter.utilities.NetworkUtility;

import java.io.IOException;
import java.net.URL;

//get exchange rates and save them to json file
    public class GetConversionAsyncTask extends AsyncTask<String,Void,Void> {

        private static final String TAG = "GetConversionAsyncTask";

        public interface SaveJSONToFileListener{
            void saveJSON(String data);
        }

        private SaveJSONToFileListener saveJSONToFileListener;
        JSONUtility jsonUtility = new JSONUtility();

        public GetConversionAsyncTask(SaveJSONToFileListener listener) {
            saveJSONToFileListener = listener;
        }

        @Override
        protected Void doInBackground(String[] strings) {
            String url = "https://api.exchangeratesapi.io/latest?base=" + strings[0];

            try{//get data from url
                URL urlObj = new URL(url);
                String data = NetworkUtility.getConversionData(urlObj);
                Log.d(TAG, "doInBackground: get conversionn data");
                if (data != null) {
                    Log.d(TAG, "doInBackground: save json");
                    saveJSONToFileListener.saveJSON(data);
                }

            }catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
