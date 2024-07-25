package com.codexnovas.bhealthy.LeaderboardFragment;

import android.media.RouteListingPreference;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codexnovas.bhealthy.R;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;



public class Leaderboard_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private List<Object> cardList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        recyclerView = view.findViewById(R.id.med_alert_card_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        cardList = new ArrayList<>();
        cardList.add(new ProgressCard("Progress", "Check your daily progress here.","Complete to earn your reward"));
        cardList.add(new RewardsCard("Rewards", "Complete challenges ", "to get rewards"));
        cardList.add(new StreaksCard("Streaks", "7","Visit everyday to","continue your streak"));
        cardList.add(new BadgesCard("Badges"));

        adapter = new LeaderboardAdapter(cardList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}