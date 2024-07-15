package com.codexnovas.bhealthy.OnboardingSlidePages;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    public ScreenSlidePagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new welcome_to_bhealthy();
            case 1:
                return new smart_insights();
            case 2:
                return new understand_your_health();
            case 3:
                return new engage_with_people();
            default:
                return new welcome_to_bhealthy();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of screens
    }
}
