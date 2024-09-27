package com.example.healthhelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DoctorActivity extends AppCompatActivity {

    private EditText searchField;
    private ImageView searchIcon;
    private RecyclerView userListRecyclerView;
    private PatientListAdapter patientListAdapter; // Adapter for initial list of patients
    private UserAdapter userAdapter; // Adapter for search results
    private List<AddUserToLocal.UserInfo> userList = new ArrayList<>(); // Original patient list
    private List<AddUserToLocal.UserInfo> searchResults = new ArrayList<>(); // Search results

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        searchField = findViewById(R.id.search_field);
        searchIcon = findViewById(R.id.search_icon);
        userListRecyclerView = findViewById(R.id.user_list);

        // Set up RecyclerView with PatientListAdapter for the initial patient list
        patientListAdapter = new PatientListAdapter(this, userList);
        userListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userListRecyclerView.setAdapter(patientListAdapter);

        // Load all patients when the activity is created
        loadAllPatients();

        // Set onClickListener for the search icon
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchField.getText().toString();
                if (!TextUtils.isEmpty(searchQuery)) {
                    searchPatient(searchQuery);
                } else {
                    Toast.makeText(DoctorActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Function to load all patients
    private void loadAllPatients() {
        // Call your API or service to load all patients connected to the doctor
        AsyncTask<Void, Void, List<AddUserToLocal.UserInfo>> getAllPatients = new GetAllPatients(this, new GetAllPatients.UserResultCallback() {
            @Override
            public void onResult(List<AddUserToLocal.UserInfo> users) {
                if (users != null && !users.isEmpty()) {
                    userList.clear();
                    userList.addAll(users);
                    patientListAdapter.notifyDataSetChanged(); // Notify the patient list adapter
                } else {
                    Toast.makeText(DoctorActivity.this, "No patients found", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();  // Call API to fetch all patients
    }

    // Function to search a patient
    private void searchPatient(String phone) {
        new FindPatient(new FindPatient.UserResultCallback() {
            @Override
            public void onResult(List<AddUserToLocal.UserInfo> users) {
                if (users != null && !users.isEmpty()) {
                    searchResults.clear();
                    searchResults.addAll(users);
                    // Use UserAdapter for displaying search results
                    userAdapter = new UserAdapter(DoctorActivity.this, searchResults, new UserAdapter.OnPatientAddedListener() {
                        @Override
                        public void onPatientAdded() {
                            // Refresh the patient list after a patient is added
                            loadAllPatients();
                        }
                    });
                    userListRecyclerView.setAdapter(userAdapter); // Switch adapter to UserAdapter
                } else {
                    Toast.makeText(DoctorActivity.this, "No patient found", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute(phone);
    }
}
