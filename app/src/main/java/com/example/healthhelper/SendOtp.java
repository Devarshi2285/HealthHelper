package com.example.healthhelper;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendOtp extends AsyncTask<String, Void, String> {
    public interface SendOtpCallback {
        void onOtpSent(String otp);
    }

    private SendOtpCallback callback;

    public SendOtp(SendOtpCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String phone = params[0];
        String result = "";
        Log.d("SendOtp", "Sending OTP for phone number: " + phone);

        try {
            URL url = new URL("http://10.0.2.2:3000/send-otp");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            String jsonInputString = "{\"number\":\"" + phone + "\"}";

            Log.d("SendOtp", "JSON Input String: " + jsonInputString);
            try (OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream())) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = urlConnection.getResponseCode();
            Log.d("SendOtp", "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                result = response.toString();
                Log.d("SendOtp", "Response from server: " + result);
                String otp = extractOtpFromResponse(result);
                callback.onOtpSent(otp);
            } else {
                result = "Error: " + responseCode;
                Log.e("SendOtp", "Error response from server: " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "Exception: " + e.getMessage();
            Log.e("SendOtp", "Exception occurred: " + result);
        }

        return result;
    }

    private String extractOtpFromResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString("otp");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("SendOtp", "JSON parsing error: " + e.getMessage());
            return null;
        }
    }
}
