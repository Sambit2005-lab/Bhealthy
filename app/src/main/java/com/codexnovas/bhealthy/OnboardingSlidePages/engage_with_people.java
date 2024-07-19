package com.codexnovas.bhealthy.OnboardingSlidePages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codexnovas.bhealthy.LoginPage;
import com.codexnovas.bhealthy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class engage_with_people extends Fragment {

    private AppCompatImageButton getStartedButton;
    private DatabaseReference databaseReference;
    private String userId;
    private StreakUpdateListener streakUpdateListener;
    private FirebaseAuth mAuth;

    public engage_with_people() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            streakUpdateListener = (StreakUpdateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement StreakUpdateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        streakUpdateListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.engage_with_people, container, false);
        getStartedButton = view.findViewById(R.id.get_started_btn);

        // Initialize Firebase Auth and userId
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            // Handle the case where userId is null
            // You might want to redirect the user to the login screen or show an error message

            return view; // or handle appropriately
        }

        // Initialize databaseReference after userId is set
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        getStartedButton.setOnClickListener(v -> {
            checkAndUpdateStreak();
            proceedToLogin();
        });

        return view;
    }

    private void checkAndUpdateStreak() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
                String lastVisitDate = dataSnapshot.child("lastVisitDate").getValue(String.class);
                Long streakCount = dataSnapshot.child("streakCount").getValue(Long.class);

                if (streakCount == null) {
                    streakCount = 0L;
                }

                if (lastVisitDate == null || !lastVisitDate.equals(currentDate)) {
                    if (lastVisitDate == null || LocalDate.parse(lastVisitDate).plusDays(1).equals(LocalDate.now())) {
                        streakCount++;
                    } else {
                        streakCount = 1L;
                    }
                    databaseReference.child("lastVisitDate").setValue(currentDate);
                    databaseReference.child("streakCount").setValue(streakCount);
                }

                if (streakCount % 30 == 0) {
                    // Notify the main activity to show the gift animation
                    streakUpdateListener.showGiftAnimation();
                }

                // Notify the main activity to update the streak count
                streakUpdateListener.updateStreakCount(streakCount);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void proceedToLogin() {
        Intent intent = new Intent(getActivity(), LoginPage.class);
        startActivity(intent);
    }

    public interface StreakUpdateListener {
        void updateStreakCount(long streakCount);
        void showGiftAnimation();
    }
}