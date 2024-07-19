package com.codexnovas.bhealthy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import com.codexnovas.bhealthy.CommunityFragment.Community_Fragment;
import com.codexnovas.bhealthy.HomeFragment.Home_Fragment;
import com.codexnovas.bhealthy.LeaderboardFragment.Leaderboard_Fragment;
import com.codexnovas.bhealthy.ProfileFragment.Profile_Fragment;
import com.codexnovas.bhealthy.OnboardingSlidePages.engage_with_people;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements engage_with_people.StreakUpdateListener {

    private int selectedTab = 1;
    private FloatingActionButton actionBtn;
    private TextView streakCountText;
    private boolean showingIcon = true;
    private Handler handler = new Handler();
    private long currentStreakCount = 0;
    private boolean isGiftDay = false; // Replace this with your actual logic to determine gift day
    private DatabaseReference streakRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        BottomAppBar bottomAppBar = findViewById(R.id.app_bar);
        LinearLayout homeLayout = findViewById(R.id.home_layout);
        LinearLayout communityLayout = findViewById(R.id.community_layout);
        LinearLayout leaderboardLayout = findViewById(R.id.leaderboard_layout);
        LinearLayout profileLayout = findViewById(R.id.profile_layout);

        ImageView homeImage = findViewById(R.id.image_home);
        ImageView communityImage = findViewById(R.id.image_community);
        ImageView leaderboardImage = findViewById(R.id.image_leaderboard);
        ImageView profileImage = findViewById(R.id.image_profile);

        TextView homeText = findViewById(R.id.text_home);
        TextView communityText = findViewById(R.id.text_community);
        TextView leaderboardText = findViewById(R.id.text_leaderboard);
        TextView profileText = findViewById(R.id.text_profile);

        // Initialize custom views
        actionBtn = findViewById(R.id.action_btn);
        streakCountText = findViewById(R.id.streak_count_text);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = getCurrentUserId(); // Method to retrieve current user ID
        streakRef = database.getReference("users").child(userId).child("streakCount");

        // Fetch streak count from Firebase
        fetchStreakCountFromFirebase();

        // Set home fragment as default
        setFragment(new Home_Fragment());
        updateTabUI(1, homeImage, R.drawable.home_icon_selected);

        // Set click listeners
        homeLayout.setOnClickListener(v -> {
            if (selectedTab != 1) {
                setFragment(new Home_Fragment());
                updateTabUI(1, homeImage, R.drawable.home_icon_selected);
            }
        });

        communityLayout.setOnClickListener(v -> {
            if (selectedTab != 2) {
                setFragment(new Community_Fragment());
                updateTabUI(2, communityImage, R.drawable.community_icon_selected);
            }
        });

        leaderboardLayout.setOnClickListener(v -> {
            if (selectedTab != 3) {
                setFragment(new Leaderboard_Fragment());
                updateTabUI(3, leaderboardImage, R.drawable.leaderboard_icon_selected);
            }
        });

        profileLayout.setOnClickListener(v -> {
            if (selectedTab != 4) {
                setFragment(new Profile_Fragment());
                updateTabUI(4, profileImage, R.drawable.profile_icon_selected);
            }
        });

        // FloatingActionButton click listener
        actionBtn.setOnClickListener(v -> {
            // Handle action button click if needed
        });

        startStreakIconAnimationLoop();
    }

    private void fetchStreakCountFromFirebase() {
        streakRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentStreakCount = dataSnapshot.getValue(Long.class);
                    updateStreakCount(currentStreakCount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUid(); // Return the unique user ID
        } else {
            // Handle the case where there is no authenticated user
            return null; // or throw an exception or return a default value
        }
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void updateTabUI(int tabId, ImageView selectedImage, int selectedIcon) {
        // Reset all images to unselected icons
        ((ImageView) findViewById(R.id.image_home)).setImageResource(R.drawable.home_icon_navbar);
        ((ImageView) findViewById(R.id.image_community)).setImageResource(R.drawable.community_icon_nav);
        ((ImageView) findViewById(R.id.image_leaderboard)).setImageResource(R.drawable.leaderboard_icon_nav);
        ((ImageView) findViewById(R.id.image_profile)).setImageResource(R.drawable.profile_icon_nav);

        // Set selected tab icon
        selectedImage.setImageResource(selectedIcon);

        selectedTab = tabId;
    }

    @Override
    public void updateStreakCount(long streakCount) {
        currentStreakCount = streakCount;
        streakCountText.setText(String.valueOf(currentStreakCount));
    }

    @Override
    public void showGiftAnimation() {
        // Handle gift animation or icon change
        // For example, changing the icon and managing visibility
        actionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GiftPage.class);
            startActivity(intent);
        });
        actionBtn.setImageResource(R.drawable.gift_icon); // Set gift icon drawable
    }

    private void startStreakIconAnimationLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isGiftDay) {
                    // Show gift icon
                    actionBtn.setVisibility(View.GONE); // Hide FAB
                    streakCountText.setVisibility(View.GONE); // Hide streak count text
                    actionBtn.setImageResource(R.drawable.gift_icon); // Set gift icon drawable
                } else {
                    if (showingIcon) {
                        // Show the streak icon
                        actionBtn.setVisibility(View.VISIBLE);
                        actionBtn.setImageResource(R.drawable.streak); // Your streak icon drawable
                        streakCountText.setVisibility(View.GONE); // Hide streak count text
                    } else {
                        // Show the streak count
                        actionBtn.setVisibility(View.GONE); // Hide FAB
                        streakCountText.setVisibility(View.VISIBLE); // Show streak count text
                    }
                    showingIcon = !showingIcon;
                }
                handler.postDelayed(this, 2000); // Flip every 2 seconds
            }
        }, 0); // Start immediately
    }
}