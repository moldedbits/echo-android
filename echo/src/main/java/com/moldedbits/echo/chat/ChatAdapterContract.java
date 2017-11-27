package com.moldedbits.echo.chat;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.moldedbits.echo.chat.core.model.EchoMessage;

public interface ChatAdapterContract {
    interface ViewContract {
        void displayMessage(@Nullable final String message);

        void displayDocument(final String name, final String date, final Uri uri, final boolean showDoc);

        void displayImage(final Bitmap bitmap, final EchoMessage message, final boolean showImage);

        void displayStatusIcons(final int iconResource);

        void displayTimestamp(@NonNull final String timestamp);

    }

    interface PresenterContract {
        void bindData(EchoMessage message);
    }
}
