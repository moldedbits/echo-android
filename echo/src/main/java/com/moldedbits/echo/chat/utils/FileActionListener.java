package com.moldedbits.echo.chat.utils;

import android.net.Uri;

import java.io.File;

public interface FileActionListener {
    void onActionCompleted(File file, Uri fileUri);
}
