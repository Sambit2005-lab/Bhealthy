package com.codexnovas.bhealthy;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PersonalInfoPage extends AppCompatActivity {

    private EditText enterFullName, enterDateOfBirth, enterGender, enterEmail, enterPhone;
    private CountryCodePicker countryCodePicker;
    private ImageButton nextButton, calendarButton;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info_page);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        enterFullName = findViewById(R.id.enter_fullname);
        enterDateOfBirth = findViewById(R.id.enter_date);
        enterGender = findViewById(R.id.pronouns);
        enterEmail = findViewById(R.id.email);
        enterPhone = findViewById(R.id.phone);
        countryCodePicker = findViewById(R.id.ccp);
        nextButton = findViewById(R.id.nextBtn);
        calendarButton = findViewById(R.id.CalendarBtn);

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });
    }

    private void showDatePicker() {
        // Get current date as default date for the date picker
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the EditText with the selected date
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        enterDateOfBirth.setText(selectedDate);
                    }
                },
                year, month, dayOfMonth);

        // Show the date picker dialog
        datePickerDialog.show();
    }

    private void saveUserData() {
        String fullName = enterFullName.getText().toString().trim();
        String dateOfBirth = enterDateOfBirth.getText().toString().trim();
        String gender = enterGender.getText().toString().trim();
        String email = enterEmail.getText().toString().trim();
        String phone = enterPhone.getText().toString().trim();
        String countryCode = countryCodePicker.getSelectedCountryCode();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(dateOfBirth) ||
                TextUtils.isEmpty(gender) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullname", fullName);
        userMap.put("dateOfBirth", dateOfBirth);
        userMap.put("gender", gender);
        userMap.put("email", email);
        userMap.put("phone", phone);
        userMap.put("countryCode", countryCode);

        usersRef.updateChildren(userMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PersonalInfoPage.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                        // Navigate to next activity or perform desired action
                        Intent intent = new Intent(PersonalInfoPage.this, MedicalHistory.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(PersonalInfoPage.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}