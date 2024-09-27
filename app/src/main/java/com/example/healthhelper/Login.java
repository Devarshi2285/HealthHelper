package com.example.healthhelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    private EditText searchPhoneEdt;
    private Button searchBtn;
    private TextView gotoSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        searchBtn = findViewById(R.id.button);
        searchPhoneEdt = findViewById(R.id.editTextPhone);
        gotoSignUp = findViewById(R.id.textView2);

        checkUserDetails();

        gotoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = searchPhoneEdt.getText().toString();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    new FindUserTask() {
                        @Override
                        protected void onPostExecute(Integer responseCode) {
                            if (responseCode != null) {
                                if (responseCode == 200) {
                                    Toast.makeText(Login.this, "User found!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, VerifyOtp.class);
                                    intent.putExtra("phone_number", phoneNumber);
                                    startActivity(intent);
                                    finish();
                                } else if (responseCode == 201) {
                                    Toast.makeText(Login.this, "No user found with this phone number.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Login.this, "Unexpected response: " + responseCode, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Login.this, "Request failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute(phoneNumber);
                } else {
                    Toast.makeText(Login.this, "Please enter a phone number to search.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUserDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String designation = sharedPreferences.getString("designation", null);

        if (designation != null) {
            Intent intent;
            if (designation.equals("Doctor")) {
                intent = new Intent(Login.this, DoctorActivity.class);
            } else if (designation.equals("Patient")) {
                intent = new Intent(Login.this, PatientActivity.class);
            } else {
                return; // Handle unexpected designation
            }
            startActivity(intent);
            finish();
        }
    }
}
