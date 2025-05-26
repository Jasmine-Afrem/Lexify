package com.example.lexify.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lexify.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private List<AchievementsActivity.Achievement> achievements;

    public AchievementsAdapter(List<AchievementsActivity.Achievement> achievements) {
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        AchievementsActivity.Achievement achievement = achievements.get(position);
        
        holder.titleTextView.setText(achievement.title);
        holder.descriptionTextView.setText(achievement.description);
        holder.iconImageView.setImageResource(achievement.iconResId);
        
        // Update card appearance based on unlock status
        if (achievement.isUnlocked) {
            holder.cardView.setStrokeColor(holder.cardView.getContext().getColor(R.color.lex_purple_primary));
            holder.iconImageView.setColorFilter(holder.cardView.getContext().getColor(R.color.lex_purple_primary));
            holder.titleTextView.setTextColor(holder.cardView.getContext().getColor(R.color.lex_purple_primary));
            holder.descriptionTextView.setAlpha(1.0f);
            holder.iconImageView.setAlpha(1.0f);
            holder.statusTextView.setVisibility(View.GONE);
        } else {
            holder.cardView.setStrokeColor(holder.cardView.getContext().getColor(R.color.lex_text_subtle_on_light_bg));
            holder.iconImageView.setColorFilter(holder.cardView.getContext().getColor(R.color.lex_text_subtle_on_light_bg));
            holder.titleTextView.setTextColor(holder.cardView.getContext().getColor(R.color.lex_text_subtle_on_light_bg));
            holder.descriptionTextView.setAlpha(0.5f);
            holder.iconImageView.setAlpha(0.5f);
            holder.statusTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView iconImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView statusTextView;

        AchievementViewHolder(View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            iconImageView = itemView.findViewById(R.id.achievementIcon);
            titleTextView = itemView.findViewById(R.id.achievementTitle);
            descriptionTextView = itemView.findViewById(R.id.achievementDescription);
            statusTextView = itemView.findViewById(R.id.achievementStatus);
        }
    }
} 