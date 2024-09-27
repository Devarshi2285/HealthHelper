package com.example.healthhelper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterUserTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        String username = params[0];
        String phone = params[1];
        String designation=params[2];
        String result = "";

        try {
            // Set the URL of your Node.js endpoint
            URL url = new URL("http://10.0.2.2:3000/register-user");


            // Create and configure the connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true); // Allows POST data

            // JSON object containing the user data
            String jsonInputString = "{\"username\":\"" + username + "\", \"number\":\"" + phone + "\", \"designation\":\"" + designation + "\"}";

            // Write the data to the output stream
            try (OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream())) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code and read the response
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                result = response.toString(); // You can parse this JSON result as needed
            } else {
                result = "Error: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = "Exception: " + e.getMessage();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the response (success or failure)
        // For example, show a toast or update the UI
        Log.e("Server Response: ",result);
    }
}
