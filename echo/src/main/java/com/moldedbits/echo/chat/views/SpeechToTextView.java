package com.moldedbits.echo.chat.views;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moldedbits.echo.chat.utils.AnimationUtils;

import java.util.ArrayList;
import java.util.Locale;

import moldedbits.com.echo.R;

/**
 * This view is required to convert the Speech into text. Use this view where user text for chat is
 * entered.
 */
public class SpeechToTextView extends LinearLayout implements TextWatcher, TextView.OnEditorActionListener {

    public static final int REQ_CODE_SPEECH_INPUT = 12345;
    private final Context mContext;
    private LayoutInflater mInflater;

    @Nullable
    EditText mMessageInput;

    @Nullable
    ImageView mSendAction;

    @Nullable
    ImageView mSpeechAction;

    OnActionPerformListener mActionPerformListener;

    public SpeechToTextView(Context context) {
        this(context, null, -1);
    }

    public SpeechToTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SpeechToTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        View view = mInflater.inflate(R.layout.view_speech_to_text, this, true);
        mMessageInput = (EditText) view.findViewById(R.id.et_message);
        mSendAction = (ImageView) view.findViewById(R.id.iv_send_message);
        mSpeechAction = (ImageView) view.findViewById(R.id.iv_speak_now);

        if (mSpeechAction != null) {
            mSpeechAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickSpeakNow(v);
                }
            });
        }

        if (mSendAction != null) {
            mSendAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCLickSendMessage(v);
                }
            });
        }

        if (mMessageInput != null) {
            mMessageInput.addTextChangedListener(this);
            mMessageInput.setOnEditorActionListener(this);
        }
    }

    public void onClickSpeakNow(View view) {
        promptSpeechInput();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                mContext.getString(R.string.speech_prompt));
        try {
            ((Activity) mContext).startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Snackbar.make(this,
                    mContext.getString(R.string.speech_not_supported),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    public void onCLickSendMessage(View view) {
        mActionPerformListener.onPerformAction(view);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s)) {
            AnimationUtils.hideFromTop(mSpeechAction);
            AnimationUtils.showFromBottom(mSendAction);
        } else {
            AnimationUtils.hideFromTop(mSendAction);
            AnimationUtils.showFromBottom(mSpeechAction);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            Snackbar.make(this, "Implement Code to send message", Snackbar.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public void setText(String text) {
        mMessageInput.setText(text);
    }

    public Editable getText() {
        return mMessageInput.getText();
    }

    public void onSpeechTextReceived(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mMessageInput.setText(result.get(0));
                }
                break;
            }
        }
    }

    public void setOnActionPerformListener(OnActionPerformListener actionPerformListener) {
        mActionPerformListener = actionPerformListener;
    }

    public interface OnActionPerformListener {
        void onPerformAction(View view);
    }
}
