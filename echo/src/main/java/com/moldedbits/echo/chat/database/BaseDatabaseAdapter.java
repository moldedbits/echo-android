package com.moldedbits.echo.chat.database;

import android.support.annotation.NonNull;

import com.moldedbits.echo.chat.core.model.EchoMessage;

import java.util.List;

/**
 * Author viveksingh
 * Date 15/06/17.
 */

public interface BaseDatabaseAdapter<T> {
     T fromEchoMessage(@NonNull final EchoMessage message);

    EchoMessage toEchoMessage(@NonNull final T message);

    List<T> fromEchoMessages(@NonNull final List<EchoMessage> message);

    List<EchoMessage> toEchoMessages(@NonNull final List<T> message);

}
