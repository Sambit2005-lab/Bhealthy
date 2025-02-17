package com.codexnovas.bhealthy;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadPic extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView pfpPreview;
    private AppCompatButton uploadBtn, skipBtn, preLanguageBtn;
    private AppCompatImageButton backBtn, nextBtn;
    private Spinner langSpinner;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pic);

        pfpPreview = findViewById(R.id.pfp_preview);
        uploadBtn = findViewById(R.id.upload_btn);
        skipBtn = findViewById(R.id.skip_for_now);
        backBtn = findViewById(R.id.back_btn);
        nextBtn = findViewById(R.id.next_btn);
        progressBar = findViewById(R.id.progress_bar);
        langSpinner = findViewById(R.id.lang_spinner);

        // Initialize Firebase instances
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("profile_pictures");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set up the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle skip button action
                Intent intent = new Intent(UploadPic.this, Loading_page.class);
                startActivity(intent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button action
                Intent intent = new Intent(UploadPic.this, HealthMetrics.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle next button action
                savePreferredLanguage();
                Intent intent = new Intent(UploadPic.this, Loading_page.class);
                intent.putExtra("profileImageUrl", imageUri);
                startActivity(intent);
                finish();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(profileImageIntent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            pfpPreview.setImageURI(imageUri);
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                StorageReference fileReference = storageReference.child(userId + ".jpg");
                progressBar.setVisibility(View.VISIBLE);

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        mDatabase.child("users").child(userId).child("profileImage").setValue(imageUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressBar.setVisibility(View.GONE);
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(UploadPic.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                                                        } else {
                                                            Toast.makeText(UploadPic.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadPic.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(UploadPic.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(UploadPic.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePreferredLanguage() {
        String selectedLanguage = langSpinner.getSelectedItem().toString();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("users").child(userId).child("preferredLanguage").setValue(selectedLanguage)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UploadPic.this, "Preferred language saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UploadPic.this, "Failed to save preferred language", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(UploadPic.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
