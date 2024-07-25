package com.codexnovas.bhealthy.LeaderboardFragment;

import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codexnovas.bhealthy.R;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> cardList;

    public LeaderboardAdapter(List<Object> cardList) {
        this.cardList = cardList;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = cardList.get(position);
        if (item instanceof ProgressCard) {
            return 0; // Type for ProgressCard
        } else if (item instanceof RewardsCard) {
            return 1; // Type for RewardsCard
        } else if (item instanceof StreaksCard) {
            return 2; // Type for StreaksCard
        } else if (item instanceof BadgesCard) {
            return 3; // Type for BadgesCard
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            View view = inflater.inflate(R.layout.progress_card, parent, false);
            return new ProgressViewHolder(view);
        } else if (viewType == 1) {
            View view = inflater.inflate(R.layout.rewards_card, parent, false);
            return new RewardsViewHolder(view);
        } else if (viewType == 2) {
            View view = inflater.inflate(R.layout.streaks_card, parent, false);
            return new StreaksViewHolder(view);
        } else if (viewType == 3) {
            View view = inflater.inflate(R.layout.badges_card, parent, false);
            return new BadgesViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = cardList.get(position);
        if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).bind((ProgressCard) item);
        } else if (holder instanceof RewardsViewHolder) {
            ((RewardsViewHolder) holder).bind((RewardsCard) item);
        } else if (holder instanceof StreaksViewHolder) {
            ((StreaksViewHolder) holder).bind((StreaksCard) item);
        } else if (holder instanceof BadgesViewHolder) {
            ((BadgesViewHolder) holder).bind((BadgesCard) item);
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView title, description1,description2;

        ProgressViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.progress);
            description1 = itemView.findViewById(R.id.check);
            description2= itemView.findViewById(R.id.earn);

        }

        void bind(ProgressCard item) {
            title.setText(item.title);
            description1.setText(item.description1);
            description2.setText(item.description2);
        }
    }

    static class RewardsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, getRewards;

        RewardsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.rewards);
            description = itemView.findViewById(R.id.complete);
            getRewards = itemView.findViewById(R.id.get);
        }

        void bind(RewardsCard item) {
            title.setText(item.title);
            description.setText(item.description);
            getRewards.setText(item.getRewards);
        }
    }

    static class StreaksViewHolder extends RecyclerView.ViewHolder {
        TextView title, streakNumber, description1,description2;

        StreaksViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.streaks);
            streakNumber = itemView.findViewById(R.id.streak_number);
            description1 = itemView.findViewById(R.id.visit);
            description2 = itemView.findViewById(R.id.continue_streak);
        }

        void bind(StreaksCard item) {
            title.setText(item.title);
            streakNumber.setText(item.streakNumber);
            description1.setText(item.description1);
            description2.setText(item.description2);
        }
    }

    static class BadgesViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        BadgesViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.badges);
        }

        void bind(BadgesCard item) {
            title.setText(item.title);
        }
    }
}