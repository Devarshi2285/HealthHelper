package com.example.healthhelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VerifyOtp extends AppCompatActivity implements SendOtp.SendOtpCallback {
    private static final String TAG = "VerifyOtp";
    private EditText editTextOTP;
    private Button btnVerify;
    private String phoneNumber;
    private String receivedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        editTextOTP = findViewById(R.id.editTextNumberDecimal);
        btnVerify = findViewById(R.id.button2);
        phoneNumber = getIntent().getStringExtra("phone_number");

        if (phoneNumber != null && !phoneNumber.startsWith("+")) {
            phoneNumber = "+91" + phoneNumber;
        }

        sendOtpToBackend();

        btnVerify.setOnClickListener(v -> verifyOTP());
    }

    private void sendOtpToBackend() {
        new SendOtp(this).execute(phoneNumber);
    }

    @Override
    public void onOtpSent(String otp) {
        receivedOtp = otp;
        Log.d(TAG, "Received OTP: " + receivedOtp);
        Toast.makeText(VerifyOtp.this, "OTP Sent", Toast.LENGTH_SHORT).show();
    }

    private void verifyOTP() {
        String enteredOtp = editTextOTP.getText().toString();
        if (enteredOtp.equals(receivedOtp)) {
            Log.d(TAG, "OTP verified successfully!");
            new AddUserToLocal(VerifyOtp.this).execute(phoneNumber);
            navigateToHome();
        } else {
            Log.e(TAG, "Invalid OTP entered.");
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(VerifyOtp.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
