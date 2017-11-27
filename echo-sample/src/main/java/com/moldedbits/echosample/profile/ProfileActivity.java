package com.moldedbits.echosample.profile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moldedbits.echosample.R;
import com.moldedbits.echosample.User;
import com.moldedbits.echosample.register.RegisterActivity;

/**
 * Created by shishank on 23/06/17.
 */

public class ProfileActivity extends AppCompatActivity implements ValueEventListener {

    private EditText etName;
    private EditText etNumber;
    private Button btnSave;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = (EditText) findViewById(R.id.et_name);
        etNumber = (EditText) findViewById(R.id.et_number);
        btnSave = (Button) findViewById(R.id.btn_save);

        setData();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().addValueEventListener(this);

        updateDatabase();
    }

    private void setData() {
        if (getIntent() != null) {
            String phone = getIntent().getStringExtra(RegisterActivity.NUMBER);
            String name = getIntent().getStringExtra(RegisterActivity.USER_NAME);
            etName.setText(name);
            etNumber.setText(phone);
        }
    }

    private void updateDatabase() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        User user = new User();
                        user.setName(etName.getText().toString());
                        user.setPhone(etNumber.getText().toString());
                        String id = firebaseDatabase.getReference().push().getKey();
                        firebaseDatabase.getReference().child(id).setValue(user);
                        openContactActivity();
                    }
                });
            }
        });
    }

    private void openContactActivity() {
        startActivity(new Intent(this,ContactsActivity.class));
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d("Datasnap", dataSnapshot.toString());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d("ERROR_DATABASE", databaseError.getMessage());
    }
}
