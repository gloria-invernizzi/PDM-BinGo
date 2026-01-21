package com.application.bingo.ui.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.R;
import com.application.bingo.model.Notification;
import com.application.bingo.ui.viewmodel.NotificationViewModel;

import java.util.List;

/**
 * Adapter for displaying waste collection notifications in a RecyclerView.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;
    private final NotificationViewModel viewModel;
    private OnItemDeleteListener deleteListener;

    public NotificationAdapter(List<Notification> notifications, NotificationViewModel viewModel) {
        this.notifications = notifications;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvWasteType.setText(notification.getWasteType());
        holder.tvTime.setText(viewModel.formatDateTime(notification, holder.itemView.getContext()));
        holder.tvRepeat.setText(viewModel.formatRepeat(notification, holder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    public void updateList(List<Notification> newList) {
        this.notifications = newList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWasteType, tvTime, tvRepeat;
        ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWasteType = itemView.findViewById(R.id.tvWasteType);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRepeat = itemView.findViewById(R.id.tvRepeat);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            // Handle delete button clicks
            btnDelete.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Notification toDelete = notifications.get(pos);

                    new AlertDialog.Builder(v.getContext())
                            .setTitle(R.string.delete_notification)
                            .setMessage(R.string.delete_notification_dialog)
                            .setPositiveButton(R.string.solo_questa, (dialog, which) -> {
                                if (deleteListener != null) {
                                    deleteListener.onItemDelete(toDelete);
                                }
                            })
                            .setNegativeButton(R.string.all_repetitions, (dialog, which) -> {
                                if (deleteListener != null) {
                                    deleteListener.onItemDeleteRepeating(toDelete);
                                }
                            })
                            .setNeutralButton(R.string.cancel, null)
                            .show();
                }
            });
        }
    }

    /**
     * Interface for handling notification deletion events.
     */
    public interface OnItemDeleteListener {
        void onItemDelete(Notification notification);
        void onItemDeleteRepeating(Notification notification);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }
}
