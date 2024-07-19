package com.codexnovas.bhealthy;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.codexnovas.bhealthy.CommunityFragment.Community_Fragment;
import com.codexnovas.bhealthy.HomeFragment.Home_Fragment;
import com.codexnovas.bhealthy.LeaderboardFragment.Leaderboard_Fragment;
import com.codexnovas.bhealthy.ProfileFragment.Profile_Fragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private int selectedTab = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        BottomAppBar bottomAppBar = findViewById(R.id.app_bar);
        FloatingActionButton actionButton = findViewById(R.id.action_btn);

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
        setFragment(new Home_Fragment());
        updateTabUI(1, homeImage, R.drawable.home_icon_selected);

        // Set click listeners
        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab != 1) {
                    setFragment(new Home_Fragment());

                    // Update UI for selected tab
                    updateTabUI(1, homeImage, R.drawable.home_icon_selected);
                }
            }
        });

        communityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab != 2) {
                    setFragment(new Community_Fragment());

                    // Update UI for selected tab
                    updateTabUI(2, communityImage, R.drawable.community_icon_selected);
                }
            }
        });

        leaderboardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab != 3) {
                    setFragment(new Leaderboard_Fragment());

                    // Update UI for selected tab
                    updateTabUI(3, leaderboardImage, R.drawable.leaderboard_icon_selected);
                }
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab != 4) {
                    setFragment(new Profile_Fragment());

                    // Update UI for selected tab
                    updateTabUI(4, profileImage, R.drawable.profile_icon_selected);
                }
            }
        });

        // FloatingActionButton click listener
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle action button click
            }
        });
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
}