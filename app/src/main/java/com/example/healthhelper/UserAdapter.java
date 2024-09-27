package com.example.healthhelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<AddUserToLocal.UserInfo> userList;
    private OnPatientAddedListener onPatientAddedListener;

    public interface OnPatientAddedListener {
        void onPatientAdded();  // Notify that a patient was added successfully
    }

    public UserAdapter(Context context, List<AddUserToLocal.UserInfo> userList, OnPatientAddedListener onPatientAddedListener) {
        this.context = context;
        this.userList = userList;
        this.onPatientAddedListener = onPatientAddedListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item2, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AddUserToLocal.UserInfo user = userList.get(position);
        holder.usernameTextView.setText(user.getUsername());
        holder.phoneTextView.setText(user.getMobile());

        // On Add button click
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("UserAdapter", "Add button clicked for: " + user.getUsername());

                // Get doctor's phone number from SharedPreferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                String doctorPhone = sharedPreferences.getString("mobile", null);

                if (doctorPhone != null) {
                    // Call the addPatient API
                    new AddPatientTask(user.getMobile(), doctorPhone, new AddPatientTask.AddPatientCallback() {
                        @Override
                        public void onPatientAdded(boolean success) {
                            if (success) {
                                // Notify the listener that the patient was added successfully
                                if (onPatientAddedListener != null) {
                                    onPatientAddedListener.onPatientAdded();
                                }
                                Toast.makeText(context, "Patient added successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, DoctorActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Add this line if starting from non-activity context
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Failed to add patient!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).execute();
                } else {
                    Toast.makeText(context, "Doctor's phone not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, phoneTextView;
        Button addButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            phoneTextView = itemView.findViewById(R.id.phone_text_view);
            addButton = itemView.findViewById(R.id.add_button);
        }
    }

    // AsyncTask to call addPatient API
    private static class AddPatientTask extends AsyncTask<Void, Void, Boolean> {
        private String patientPhone;
        private String doctorPhone;
        private AddPatientCallback callback;

        public interface AddPatientCallback {
            void onPatientAdded(boolean success);
        }

        public AddPatientTask(String patientPhone, String doctorPhone, AddPatientCallback callback) {
            this.patientPhone = patientPhone;
            this.doctorPhone = doctorPhone;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            try {
                // API URL to add patient
                URL url = new URL("http://10.0.2.2:3000/add-patient");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Create JSON object with the phone numbers
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("phonePatient", patientPhone);
                jsonInput.put("phoneDoctor", doctorPhone);

                // Write JSON data to the output stream
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get the response code
                int responseCode = urlConnection.getResponseCode();
                Log.d("AddPatientTask", "Response Code: " + responseCode);

                // Read the response
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.d("AddPatientTask", "Response: " + response.toString());
                    return true; // Successfully added patient
                } else {
                    // Log the response if not OK
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.e("AddPatientTask", "Failed to add patient: " + response.toString());
                    return false; // Failed to add patient
                }
            } catch (Exception e) {
                Log.e("AddPatientTask", "Error: " + e.getMessage(), e);
                return false; // Exception occurred
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect(); // Always disconnect
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d("AddPatientTask", "Patient added success: " + success);
            if (callback != null) {
                callback.onPatientAdded(success);
            }
        }
    }
}
