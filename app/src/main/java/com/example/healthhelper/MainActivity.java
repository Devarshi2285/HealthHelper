package com.example.healthhelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText employeeNameEdt, employeePhoneEdt, employeeAddressEdt;
    private Button sendDatabtn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        employeeNameEdt = findViewById(R.id.idEdtEmployeeName);
        employeePhoneEdt = findViewById(R.id.idEdtEmployeePhoneNumber);
        employeeAddressEdt = findViewById(R.id.idEdtEmployeeAddress);


        sendDatabtn = findViewById(R.id.idBtnSendData);

        sendDatabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = employeeNameEdt.getText().toString();
                String phone = employeePhoneEdt.getText().toString();
                String address = employeeAddressEdt.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
                    Toast.makeText(MainActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    RegisterUserTask registerTask = new RegisterUserTask();

                    registerTask.execute(name, phone, address);

                    Intent intent=new Intent(MainActivity.this,Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void addDatatoFirebase(String name, String phone, String address) {
        // Create a new unique key for each employee
        String employeeId = databaseReference.push().getKey();

        EmployeeInfo employeeInfo = new EmployeeInfo(name, phone, address);

        databaseReference.child(employeeId).setValue(employeeInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(MainActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields after successful addition
                    Intent intent=new Intent(MainActivity.this,Login.class);
                    startActivity(intent);
                    finish();
                    clearInputFields();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearInputFields() {
        employeeNameEdt.setText("");
        employeePhoneEdt.setText("");
        employeeAddressEdt.setText("");
    }
}