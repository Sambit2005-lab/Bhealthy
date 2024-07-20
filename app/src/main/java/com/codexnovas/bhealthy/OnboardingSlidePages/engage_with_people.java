package com.codexnovas.bhealthy.OnboardingSlidePages;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codexnovas.bhealthy.LoginPage;
import com.codexnovas.bhealthy.MainActivity;
import com.codexnovas.bhealthy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class engage_with_people extends Fragment {


    private AppCompatImageButton getStartedButton;
    private FirebaseAuth mAuth;

    public engage_with_people() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.engage_with_people, container, false);
        getStartedButton = view.findViewById(R.id.get_started_btn);

        getStartedButton.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                //  User is signed in, redirect to mainactivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getActivity(), "Hello!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginPage.class);
                startActivity(intent);

            }
        });




        return view;


    }
}