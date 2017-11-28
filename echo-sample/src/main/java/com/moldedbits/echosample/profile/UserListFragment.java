package com.moldedbits.echosample.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moldedbits.echosample.R;
import com.moldedbits.echosample.User;

public class UserListFragment extends Fragment implements ValueEventListener {

    private RecyclerView recyclerView;
    private UserRecyclerViewAdapter recyclerViewAdapter;
    private FirebaseDatabase firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_contact_item, container, false);
        recyclerView = view.findViewById(R.id.rv_contacts);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().addValueEventListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAdapter = new UserRecyclerViewAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            User user = postSnapshot.getValue(User.class);
            recyclerViewAdapter.addData(user);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
