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
import com.example.realprojectaistudymentor.model.Notification;
import com.example.realprojectaistudymentor.utils.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter hiển thị danh sách thông báo.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notifications = new ArrayList<>();

    public NotificationAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Notification> data) {
        this.notifications = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notif = notifications.get(position);

        // Icon theo type
        String icon;
        switch (notif.getType() != null ? notif.getType() : "") {
            case "badge": icon = "🏆"; break;
            case "streak": icon = "🔥"; break;
            case "achievement": icon = "⭐"; break;
            default: icon = "📢"; break;
        }

        holder.tvIcon.setText(icon);
        holder.tvTitle.setText(notif.getTitle());
        holder.tvMessage.setText(notif.getMessage());
        holder.tvTime.setText(Helper.formatDate(notif.getTimestamp()));

        // Chưa đọc → bold, đọc → thường
        if (!notif.isRead()) {
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            holder.tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            holder.tvMessage.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        } else {
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            holder.tvTitle.setTypeface(null, android.graphics.Typeface.NORMAL);
            holder.tvMessage.setTextColor(ContextCompat.getColor(context, R.color.text_hint));
        }
    }

    @Override
    public int getItemCount() { return notifications.size(); }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvTitle, tvMessage, tvTime;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tv_notif_icon);
            tvTitle = itemView.findViewById(R.id.tv_notif_title);
            tvMessage = itemView.findViewById(R.id.tv_notif_message);
            tvTime = itemView.findViewById(R.id.tv_notif_time);
        }
    }
}
