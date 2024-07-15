package com.codexnovas.bhealthy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MedicalHistory extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private AppCompatImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medical_history);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        backBtn=findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicalHistory.this, LoginPage.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicalHistory();
            }
        });

        setupRadioButtons();
    }

    private void saveMedicalHistory() {
        if (!validateFields()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String userId = mAuth.getCurrentUser().getUid();

        String existingMedicalConditions = getSelectedCondition();
        String medications = ((EditText) findViewById(R.id.take_med_edit)).getText().toString();
        String allergies = getSelectedAllergy();
        String familyMedicalHistory = getSelectedFamilyCondition();
        String pastMedicalReports = ((EditText) findViewById(R.id.past_report_edit)).getText().toString();

        MedicalHistoryModel medicalHistory = new MedicalHistoryModel(
                existingMedicalConditions,
                medications,
                allergies,
                familyMedicalHistory,
                pastMedicalReports
        );

        mDatabase.child("users").child(userId).setValue(medicalHistory)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(MedicalHistory.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MedicalHistory.this, HealthMatrics.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MedicalHistory.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateFields() {
        if (getSelectedCondition().isEmpty() ||
                ((EditText) findViewById(R.id.take_med_edit)).getText().toString().isEmpty() ||
                getSelectedAllergy().isEmpty() ||
                getSelectedFamilyCondition().isEmpty() ||
                ((EditText) findViewById(R.id.past_report_edit)).getText().toString().isEmpty()) {

            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupRadioButtons() {
        setupRadioGroup(R.id.medical_codn_btn, R.id.medical_codn_edit, R.id.other_btn);
        setupRadioGroup(R.id.allergies_btn, R.id.allergies_edit, R.id.other_allergies_btn);
        setupRadioGroup(R.id.family_hist_btn, R.id.family_hist_edit, R.id.other_family_hist_btn);
    }

    private void setupRadioGroup(int radioGroupId, int editTextId, int otherButtonId) {
        RadioGroup radioGroup = findViewById(radioGroupId);
        EditText editText = findViewById(editTextId);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == otherButtonId) {
                    editText.setVisibility(View.VISIBLE);
                } else {
                    editText.setVisibility(View.GONE);
                }
            }
        });
    }

    private String getSelectedCondition() {
        RadioGroup radioGroup = findViewById(R.id.medical_codn_btn);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        if (selectedId == R.id.other_btn) {
            return ((EditText) findViewById(R.id.medical_codn_edit)).getText().toString();
        }
        return radioButton != null ? radioButton.getText().toString() : "";
    }

    private String getSelectedAllergy() {
        RadioGroup radioGroup = findViewById(R.id.allergies_btn);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        if (selectedId == R.id.other_allergies_btn) {
            return ((EditText) findViewById(R.id.allergies_edit)).getText().toString();
        }
        return radioButton != null ? radioButton.getText().toString() : "";
    }

    private String getSelectedFamilyCondition() {
        RadioGroup radioGroup = findViewById(R.id.family_hist_btn);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        if (selectedId == R.id.other_family_hist_btn) {
            return ((EditText) findViewById(R.id.family_hist_edit)).getText().toString();
        }
        return radioButton != null ? radioButton.getText().toString() : "";
    }
}

class MedicalHistoryModel {
    public String existingMedicalConditions;
    public String medications;
    public String allergies;
    public String familyMedicalHistory;
    public String pastMedicalReports;

    public MedicalHistoryModel() {
        // Default constructor required for calls to DataSnapshot.getValue(MedicalHistoryModel.class)
    }

    public MedicalHistoryModel(String existingMedicalConditions, String medications, String allergies, String familyMedicalHistory, String pastMedicalReports) {
        this.existingMedicalConditions = existingMedicalConditions;
        this.medications = medications;
        this.allergies = allergies;
        this.familyMedicalHistory = familyMedicalHistory;
        this.pastMedicalReports = pastMedicalReports;
    }
}