package com.application.bingo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.R;
import com.application.bingo.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FamilyMemberAdapter extends RecyclerView.Adapter<FamilyMemberAdapter.ViewHolder> {

    private List<User> members;

    public FamilyMemberAdapter(List<User> members) {
        this.members = members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.family_member_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = members.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        
        if (user.getPhotoUri() != null && !user.getPhotoUri().isEmpty()) {
            try {
                Picasso.get().load(user.getPhotoUri())
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(holder.image);
            } catch (Exception e) {
                holder.image.setImageResource(R.drawable.ic_profile_placeholder);
            }
        } else {
            holder.image.setImageResource(R.drawable.ic_profile_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.member_name);
            email = itemView.findViewById(R.id.member_email);
            image = itemView.findViewById(R.id.member_image);
        }
    }
}
