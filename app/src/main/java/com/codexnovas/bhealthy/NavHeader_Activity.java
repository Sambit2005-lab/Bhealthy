package com.codexnovas.bhealthy;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NavHeader_Activity extends AppCompatActivity {

    private static final String TAG = "NavHeader_Activity";
    private ImageView profilePic;
    private TextView nameText;
    private TextView emailText;

    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.nav_header);

        profilePic = findViewById(R.id.profile_pic);
        nameText = findViewById(R.id.name_text);
        emailText = findViewById(R.id.email); // Ensure the correct ID is used

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Assume userId is obtained after authentication
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = database.getReference("users").child(userId);
        storageRef = storage.getReference().child("profile_pictures/").child(userId + ".jpg");

        loadProfileData();
    }

    private void loadProfileData() {
        Log.d(TAG, "Loading profile data...");
        // Fetch user data from Firebase Realtime Database
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("fullname").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    nameText.setText(name);
                    emailText.setText(email);
                    Log.d(TAG, "Name: " + name + ", Email: " + email);
                    // Load profile picture from Firebase Storage
                    loadProfilePicture();
                } else {
                    Log.d(TAG, "Data snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void loadProfilePicture() {
        Log.d(TAG, "Loading profile picture...");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "Profile picture URI: " + uri.toString());
                // Load the image using Glide or any other image loading library
                Glide.with(NavHeader_Activity.this)
                        .load(uri)
                        .circleCrop()
                        .into(profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Failed to load profile picture: " + exception.getMessage());
            }
        });
    }
}
