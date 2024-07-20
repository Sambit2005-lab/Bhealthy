package com.codexnovas.bhealthy;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.codexnovas.bhealthy.CommunityFragment.Community_Fragment;
import com.codexnovas.bhealthy.HomeFragment.HomeFragment;
import com.codexnovas.bhealthy.LeaderboardFragment.Leaderboard_Fragment;
import com.codexnovas.bhealthy.ProfileFragment.Profile_Fragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private int selectedTab = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userReference;
    private FloatingActionButton actionBtn;
    private TextView streakCountText;
    private Handler handler = new Handler();
    private boolean showingIcon = true;
    private boolean isGiftDay = false;
    private int streakCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        BottomAppBar bottomAppBar = findViewById(R.id.app_bar);
        actionBtn = findViewById(R.id.action_btn);
        streakCountText = findViewById(R.id.streak_count_text);

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

        // Set home fragment as default
        setFragment(new HomeFragment());
        updateTabUI(1, homeImage, R.drawable.home_icon_selected);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        // Check streak on app launch
        checkStreak();

        // Set click listeners
        homeLayout.setOnClickListener(v -> {
            if (selectedTab != 1) {
                setFragment(new HomeFragment());
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
            // Handle action button click
            if (isGiftDay) {
                // Open gift page
                openGiftPage();
            }
        });

        // Start continuous visibility/invisibility toggling
        startVisibilityToggle();
    }

    private void checkStreak() {
        userReference.child("streak").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long lastLoginDate = 0;
                streakCount = 0;

                if (snapshot.exists()) {
                    lastLoginDate = snapshot.child("lastLoginDate").getValue(Long.class);
                    streakCount = snapshot.child("streakCount").getValue(Integer.class);
                }

                String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

                if (lastLoginDate == 0) {
                    // User logged in for the first time
                    updateStreakData(currentDate, 1);
                    streakCount = 1;
                } else {
                    String lastLoginDateString = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date(lastLoginDate));

                    if (currentDate.equals(lastLoginDateString)) {
                        // User logged in the same day, do nothing
                        return;
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, -1);
                    String yesterdayDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());

                    if (yesterdayDate.equals(lastLoginDateString)) {
                        // User logged in consecutive days, increase the streak
                        streakCount++;
                    } else {
                        // It's been more than a day, reset the streak
                        streakCount = 1;
                    }

                    // Update last login date
                    updateStreakData(currentDate, streakCount);
                }

                // Update UI
                updateStreakUI(streakCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void updateStreakData(String currentDate, int streakCount) {
        userReference.child("streak").child("lastLoginDate").setValue(System.currentTimeMillis());
        userReference.child("streak").child("streakCount").setValue(streakCount);
    }

    private void updateStreakUI(int streakCount) {
        if (streakCount % 30 == 0 && streakCount != 0) {
            isGiftDay = true;
        } else {
            isGiftDay = false;
        }
    }

    private void startVisibilityToggle() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isGiftDay) {
                    // Show gift icon
                    actionBtn.setVisibility(View.VISIBLE); // Show FAB
                    actionBtn.setImageResource(R.drawable.gift_icon); // Set gift icon drawable
                    streakCountText.setVisibility(View.GONE); // Hide streak count text
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
                        streakCountText.setText(String.valueOf(streakCount)); // Update streak count text
                    }
                    showingIcon = !showingIcon;
                }
                handler.postDelayed(this, 2000); // Flip every 2 seconds
            }
        }, 0); // Start immediately
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

        // Set selected tab image
        selectedImage.setImageResource(selectedIcon);

        // Reset selected tab
        selectedTab = tabId;
    }

    private void openGiftPage() {
        // Implement opening the gift page
        // For example:
        // Intent intent = new Intent(this, GiftActivity.class);
        // startActivity(intent);
    }
}

