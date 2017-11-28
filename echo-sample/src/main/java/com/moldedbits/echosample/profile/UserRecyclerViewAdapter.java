package com.moldedbits.echosample.profile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moldedbits.echosample.R;
import com.moldedbits.echosample.User;

import java.util.ArrayList;
import java.util.List;


class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder> {

    private final List<User> userList;

    UserRecyclerViewAdapter() {
        userList = new ArrayList<>();
    }

    void addData(User user) {
        userList.add(user);
        notifyDataSetChanged();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        holder.bindViews(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvNumber;

        UserViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvNumber = view.findViewById(R.id.tv_number);
        }

        void bindViews(User user) {
            tvName.setText(user.getName());
            tvNumber.setText(user.getPhone());
        }
    }
}
