package com.moldedbits.echo.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import moldedbits.com.echo.R;

/**
 * Author viveksingh
 * Date 22/06/17.
 *
 * This class is used to open a bottom sheet when attachment icons in Chat activity is clicked.
 * Just set a onclick listener for this class and you can add your own implementation for click
 * events.
 * This class show three items in bottom sheet-
 * Documents(To show all the documents in the phone to send) R.id.attachment_document
 * Camera(To open camera to take pictures to send) R.id.attachment_camera
 * Gallery(To open gallery to send pictures) R.id.attachment_gallery
 * <p>
 * Use the above ids for your customized onClick events
 */

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private View.OnClickListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView camera = (TextView) getView().findViewById(R.id.attachment_camera);
        TextView gallery = (TextView) getView().findViewById(R.id.attachment_gallery);
        TextView document = (TextView) getView().findViewById(R.id.attachment_document);

        camera.setOnClickListener(this.listener);
        gallery.setOnClickListener(this.listener);
        document.setOnClickListener(this.listener);
    }

    public void setOnclickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {

    }
}
