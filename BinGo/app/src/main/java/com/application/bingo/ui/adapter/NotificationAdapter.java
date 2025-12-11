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

import java.util.Calendar;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;
    private OnItemDeleteListener deleteListener;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
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

        Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(notification.getNotificationTime());
        holder.tvTime.setText(String.format("Ore %02d:%02d",
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE)));

        int weeks = notification.getRepeatWeeks();
        holder.tvRepeat.setText("Ogni " + weeks + (weeks > 1 ? " settimane" : " settimana"));
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

            // click sul bottone delete
            btnDelete.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Notification toDelete = notifications.get(pos);

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Cancella notifica")
                            .setMessage("Vuoi cancellare solo questa notifica o tutte le ripetizioni?")
                            .setPositiveButton("Solo questa", (dialog, which) -> {
                                if (deleteListener != null) {
                                    deleteListener.onItemDelete(toDelete);
                                }
                            })
                            .setNegativeButton("Tutte le ripetizioni", (dialog, which) -> {
                                if (deleteListener != null) {
                                    deleteListener.onItemDeleteRepeating(toDelete);
                                }
                            })
                            .setNeutralButton("Annulla", null)
                            .show();
                }
            });

        }
    }

    public interface OnItemDeleteListener {
        void onItemDelete(Notification notification);
        void onItemDeleteRepeating(Notification notification);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }
}
