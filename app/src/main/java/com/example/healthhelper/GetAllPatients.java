package com.example.healthhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetAllPatients extends AsyncTask<Void, Void, List<AddUserToLocal.UserInfo>> {

    private UserResultCallback callback;
    private Context context; // Add context to retrieve SharedPreferences

    public interface UserResultCallback {
        void onResult(List<AddUserToLocal.UserInfo> users);
    }

    public GetAllPatients(Context context, UserResultCallback callback) {
        this.context = context; // Initialize context
        this.callback = callback;
    }

    @Override
    protected List<AddUserToLocal.UserInfo> doInBackground(Void... voids) {
        List<AddUserToLocal.UserInfo> patients = new ArrayList<>();

        // Retrieve the doctor's phone number from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        String phoneDoctor = sharedPreferences.getString("mobile", null); // Get mobile number

        if (phoneDoctor == null) {
            Log.e("GetAllPatients", "Doctor's phone number not found in SharedPreferences");
            return patients; // Return empty list if phone number is not found
        }

        try {
            // API URL to get all patients
            URL url = new URL("http://10.0.2.2:3000/get-all-patients");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST"); // Change to POST
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // Create JSON object with the doctor's phone number
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("phoneDoctor", phoneDoctor); // Send phoneDoctor in request body

            // Write the JSON data to the output stream
            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Parse the JSON response
                JSONObject responseObject = new JSONObject(response.toString());
                JSONArray jsonArray = responseObject.getJSONArray("patients");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String username = jsonObject.getString("name");
                    String mobile = jsonObject.getString("mobile");
                    patients.add(new AddUserToLocal.UserInfo(username, mobile)); // Assuming UserInfo constructor takes username and mobile
                }
            } else {
                Log.e("GetAllPatients", "Failed to get patients, Response code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e("GetAllPatients", "Error: " + e.getMessage(), e);
        }
        return patients;
    }

    @Override
    protected void onPostExecute(List<AddUserToLocal.UserInfo> users) {
        if (callback != null) {
            callback.onResult(users);
        }
    }
}
