package com.example.space.chatapp.ui.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.space.chatapp.R;
import com.example.space.chatapp.models.User;

import java.util.List;

public class UserListingRecyclerAdapter extends RecyclerView.Adapter<UserListingRecyclerAdapter.ViewHolder> {

    private List<User> users;

    public UserListingRecyclerAdapter(List<User> users) {
        this.users = users;
    }

    public void add(User user) {
        users.add(user);
        notifyItemInserted(users.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_user_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserListingRecyclerAdapter.ViewHolder holder, int position) {
        User user = users.get(position);

        if (user.getEmail() != null) {
            String alphabet = user.getEmail().substring(0, 1);
            holder.txtUsername.setText(user.getEmail());
            holder.txtUserAlphabet.setText(alphabet);
        }
    }

    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        }
        return 0;
    }

    public User getUser(int position) {
        return users.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUserAlphabet, txtUsername;

        ViewHolder(View itemView) {
            super(itemView);
            txtUserAlphabet = itemView.findViewById(R.id.text_view_user_alphabet);
            txtUsername = itemView.findViewById(R.id.text_view_username);
        }
    }
}
