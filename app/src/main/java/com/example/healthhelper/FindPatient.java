package com.example.healthhelper;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FindPatient extends AsyncTask<String, Void, List<AddUserToLocal.UserInfo>> {
    private static final String TAG = "FindUserTask";
    private UserResultCallback callback;

    public interface UserResultCallback {
        void onResult(List<AddUserToLocal.UserInfo> users);
    }

    public FindPatient(UserResultCallback callback) {
        this.callback = callback;
    }

    @Override
    protected List<AddUserToLocal.UserInfo> doInBackground(String... params) {
        String phoneNumber = params[0];
        List<AddUserToLocal.UserInfo> userList = new ArrayList<>();

        try {
            URL url = new URL("http://10.0.2.2:3000/search-patient");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("phone", phoneNumber);

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject responseObject = new JSONObject(response.toString());
                if (responseObject.has("user")) {
                    JSONObject userObject = responseObject.getJSONObject("user");
                    String username = userObject.getString("name");
                    String mobile = userObject.getString("mobile");
                    String designation = userObject.getString("designation");
                    userList.add(new AddUserToLocal.UserInfo(username, mobile, designation));
                }
            } else {
                Log.e(TAG, "No user found or error occurred");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error: " + e.getMessage());
        }

        return userList;
    }

    @Override
    protected void onPostExecute(List<AddUserToLocal.UserInfo> userList) {
        callback.onResult(userList);
    }
}
