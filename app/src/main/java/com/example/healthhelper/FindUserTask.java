package com.example.healthhelper;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FindUserTask extends AsyncTask<String, Void, Integer> {
    private static final String TAG = "FindUserTask";
    private static final String SERVER_URL = "http://10.0.2.2:3000/find-user"; // Adjust this as needed

    @Override
    protected Integer doInBackground(String... params) {
        String phoneNumber = "+91"+params[0];
        Integer responseCode = null;

        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // JSON object containing the phone number
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("phone", phoneNumber);

            // Write the data to the output stream
            try (OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream())) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            responseCode = urlConnection.getResponseCode();

            urlConnection.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "Error during HTTP request", e);
        }

        return responseCode;
    }
}
