package com.example.realprojectaistudymentor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.database.entity.BadgeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter hiển thị danh sách badge.
 * Badge đã unlock hiện màu sáng, chưa unlock hiện mờ.
 */
public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder> {

    private Context context;
    private List<BadgeEntity> badges = new ArrayList<>();

    public BadgeAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<BadgeEntity> data) {
        this.badges = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_badge, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        BadgeEntity badge = badges.get(position);

        holder.tvEmoji.setText(badge.iconEmoji != null ? badge.iconEmoji : "🏅");
        holder.tvName.setText(badge.badgeName);
        holder.tvDescription.setText(badge.description);

        if (badge.unlocked) {
            // Đã mở khóa — hiện đầy đủ màu sắc
            holder.tvEmoji.setAlpha(1.0f);
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            holder.tvDescription.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            holder.itemView.setAlpha(1.0f);
        } else {
            // Chưa mở khóa — hiện mờ
            holder.tvEmoji.setAlpha(0.3f);
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.text_hint));
            holder.tvDescription.setTextColor(ContextCompat.getColor(context, R.color.text_hint));
            holder.itemView.setAlpha(0.5f);
        }
    }

    @Override
    public int getItemCount() { return badges.size(); }

    static class BadgeViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName, tvDescription;

        BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_badge_emoji);
            tvName = itemView.findViewById(R.id.tv_badge_name);
            tvDescription = itemView.findViewById(R.id.tv_badge_description);
        }
    }
}
