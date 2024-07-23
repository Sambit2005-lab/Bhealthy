package com.codexnovas.bhealthy.OnboardingSlidePages;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.codexnovas.bhealthy.R;

public class onboarding_background extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    private Handler sliderHandler = new Handler();
    private boolean isUserInteracting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_background);

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);




        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Set a touch listener to handle user interaction
        viewPager.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isUserInteracting = true;
                    sliderHandler.removeCallbacks(sliderRunnable);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isUserInteracting = false;
                    sliderHandler.postDelayed(sliderRunnable, 6000);
                    break;
            }
            return false;
        });

        // Start the slider handler
        sliderHandler.postDelayed(sliderRunnable, 6000); // 3 seconds delay
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isUserInteracting) {
                int nextItem = viewPager.getCurrentItem() + 1;
                if (nextItem < pagerAdapter.getItemCount()) {
                    viewPager.setCurrentItem(nextItem);
                    Log.d("SliderRunnable", "Sliding to item: " + nextItem);
                } else {
                    // Stop sliding on the last page
                    Log.d("SliderRunnable", "Reached the last item, stopping slide");
                    sliderHandler.removeCallbacks(sliderRunnable);
                    return;
                }
                sliderHandler.postDelayed(this, 6000); // 3 seconds delay
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isUserInteracting) {
            sliderHandler.postDelayed(sliderRunnable, 3000); // Resume with 3 seconds delay
        }
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }
}