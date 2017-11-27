package com.moldedbits.echosample.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;
import com.moldedbits.echosample.R;
import com.moldedbits.echosample.profile.ProfileActivity;

import java.util.concurrent.TimeUnit;


public class RegisterActivity extends AppCompatActivity {

    public static final String NUMBER = "number";
    public static final String USER_NAME = "user_name";

    private EditText etPhoneNumber;
    private EditText etName;
    private Button btnRegister;
    private LinearLayout llPhone;
    private LinearLayout llOtp;
    private EditText etOtp;
    private TextView tvResendCode;

    private OnVerificationStateChangedCallbacks callbacks;
    private ForceResendingToken requestToken;
    private String verificationId;
    private FirebaseAuth firebaseAuth;

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;

    private static int uiState = STATE_INITIALIZED;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        etPhoneNumber = (EditText) findViewById(R.id.et_number);
        etName = (EditText) findViewById(R.id.et_name);
        btnRegister = (Button) findViewById(R.id.btn_register);
        llPhone = (LinearLayout) findViewById(R.id.ll_phone_number);
        llOtp = (LinearLayout) findViewById(R.id.ll_otp);
        etOtp = (EditText) findViewById(R.id.et_otp);
        tvResendCode = (TextView) findViewById(R.id.tv_resend);

        registerClick();
        OtpResendClick();
        updateUi(uiState);

        firebaseAuth = FirebaseAuth.getInstance();
        callbacks = new OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d("PHONE", "" + phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("FAILURE", "" + e.getMessage());
            }

            @Override
            public void onCodeSent(String s, ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                requestToken = forceResendingToken;
                verificationId = s;
                uiState = STATE_CODE_SENT;
                updateUi(STATE_CODE_SENT);
            }
        };
    }

    private void OtpResendClick() {
        tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        etPhoneNumber.getText().toString(), 60, TimeUnit.SECONDS,
                        RegisterActivity.this, callbacks, requestToken);
            }
        });
    }

    private void updateUi(int uiState) {
        switch (uiState) {
            case STATE_CODE_SENT:
                llPhone.setVisibility(View.GONE);
                llOtp.setVisibility(View.VISIBLE);
                btnRegister.setText(R.string.verify);
                break;
            case STATE_INITIALIZED:
                llPhone.setVisibility(View.VISIBLE);
                llOtp.setVisibility(View.GONE);
                btnRegister.setText(R.string.register);
                break;
        }
    }

    private void registerClick() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (uiState) {
                    case STATE_CODE_SENT:
                        verifyPhoneNumberWithCode(verificationId, etOtp.getText().toString());
                        break;
                    case STATE_INITIALIZED:
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                String.valueOf(etPhoneNumber.getText()), 60,
                                TimeUnit.SECONDS,
                                RegisterActivity.this,
                                callbacks);
                        break;
                }
            }
        });
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            openProfileActivity();
                        } else {
                            Log.d("signIn", task.toString());
                            Snackbar.make(llPhone, R.string.invalid_otp, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(USER_NAME, etName.getText().toString());
        intent.putExtra(NUMBER, etPhoneNumber.getText().toString());
        startActivity(intent);
    }
}
