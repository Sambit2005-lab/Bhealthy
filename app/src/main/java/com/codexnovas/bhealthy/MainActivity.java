package com.codexnovas.bhealthy;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

            // Set click listeners
            homeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedTab != 1) {
                        setFragment(new Home_Fragment());

                        // Update UI for selected tab
                        updateTabUI(1, homeText, homeImage, R.drawable.home_icon_navbar);
                    }
                }
            });

            communityLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedTab != 2) {
                        setFragment(new Community_Fragment());

                        // Update UI for selected tab
                        updateTabUI(2, communityText, communityImage, R.drawable.community_icon_nav);
                    }
                }
            });

            leaderboardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedTab != 3) {
                        setFragment(new Leaderboard_Fragment());

                        // Update UI for selected tab
                        updateTabUI(3, leaderboardText, leaderboardImage, R.drawable.leaderboard_icon_nav);
                    }
                }
            });

            profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedTab != 4) {
                        setFragment(new Profile_Fragment());

                        // Update UI for selected tab
                        updateTabUI(4, profileText, profileImage, R.drawable.profile_icon_nav);
                    }
                }
            });

            // FloatingActionButton click listener
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        private void setFragment(Fragment fragment) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }

        private void updateTabUI(int tabId, TextView selectedText, ImageView selectedImage, int selectedIcon) {
            // Hide all texts
            findViewById(R.id.text_home).setVisibility(View.GONE);
            findViewById(R.id.text_community).setVisibility(View.GONE);
            findViewById(R.id.text_leaderboard).setVisibility(View.GONE);
            findViewById(R.id.text_profile).setVisibility(View.GONE);

            // Reset all images to default icons
            ((ImageView) findViewById(R.id.image_home)).setImageResource(R.drawable.home_icon_navbar);
            ((ImageView) findViewById(R.id.image_community)).setImageResource(R.drawable.community_icon_nav);
            ((ImageView) findViewById(R.id.image_leaderboard)).setImageResource(R.drawable.leaderboard_icon_nav);
            ((ImageView) findViewById(R.id.image_profile)).setImageResource(R.drawable.profile_icon_nav);

            // Reset all layouts to unselected color
            findViewById(R.id.home_layout).setBackgroundColor(getResources().getColor(R.color.white));
            findViewById(R.id.community_layout).setBackgroundColor(getResources().getColor(R.color.white));
            findViewById(R.id.leaderboard_layout).setBackgroundColor(getResources().getColor(R.color.white));
            findViewById(R.id.profile_layout).setBackgroundColor(getResources().getColor(R.color.white));

            // Set selected tab UI
            selectedText.setVisibility(View.VISIBLE);
            selectedImage.setImageResource(selectedIcon);
            switch (tabId) {
                case 1:
                    findViewById(R.id.home_layout).setBackgroundResource(R.drawable.background_btn_intro3);
                    break;
                case 2:
                    findViewById(R.id.community_layout).setBackgroundResource(R.drawable.background_btn_intro3);
                    break;
                case 3:
                    findViewById(R.id.leaderboard_layout).setBackgroundResource(R.drawable.background_btn_intro3);
                    break;
                case 4:
                    findViewById(R.id.profile_layout).setBackgroundResource(R.drawable.background_btn_intro3);
                    break;
            }

            selectedTab = tabId;
        }
    }
