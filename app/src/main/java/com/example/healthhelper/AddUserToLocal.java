package com.example.healthhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddUserToLocal extends AsyncTask<String, Void, AddUserToLocal.UserInfo> {
    private static final String TAG = "AddUserToLocal";
    private Context context;

    public AddUserToLocal(Context context) {
        this.context = context;
    }

    @Override
    protected UserInfo doInBackground(String... params) {
        String phoneNumber = params[0];
        UserInfo userInfo = null;

        try {
            // Open connection to the backend
            URL url = new URL("http://10.0.2.2:3000/find-user");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // Create JSON object with the phone number
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("phone", phoneNumber);

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
                String inputLine;

                // Read the response line by line
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                // Parse the response JSON
                JSONObject responseObject = new JSONObject(response.toString());
                if (responseObject.has("user")) {
                    JSONObject userObject = responseObject.getJSONObject("user");
                    String username = userObject.getString("name");
                    String mobile = userObject.getString("mobile");
                    String designation = userObject.getString("designation");

                    // Create UserInfo object
                    userInfo = new UserInfo(username, mobile, designation);
                    Log.d(TAG, "User info received: " + userInfo.getUsername() + ", " + userInfo.getMobile() + ", " + userInfo.getDesignation());
                } else {
                    Log.e(TAG, "No 'user' field in the response");
                }
            } else {
                Log.e(TAG, "HTTP error: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error: " + e.getMessage());
        }

        return userInfo;
    }

    @Override
    protected void onPostExecute(UserInfo userInfo) {
        if (userInfo != null) {
            // Save user data to SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", userInfo.getUsername());
            editor.putString("mobile", userInfo.getMobile());
            editor.putString("designation", userInfo.getDesignation());
            editor.apply();
            Log.d(TAG, "User data saved to SharedPreferences");
        } else {
            Log.e(TAG, "No user data to save");
        }
    }

    public static class UserInfo {
        private String username;
        private String mobile;
        private String designation;

        public UserInfo(String username, String mobile, String designation) {
            this.username = username;
            this.mobile = mobile;
            this.designation = designation;
        }

        public UserInfo(String username, String mobile) {
            this.username = username;
            this.mobile = mobile;
        }

        public String getUsername() {
            return username;
        }

        public String getMobile() {
            return mobile;
        }

        public String getDesignation() {
            return designation;
        }
    }
}

