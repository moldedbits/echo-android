package com.moldedbits.echo.chat.utils;

import android.util.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

/**
 * Author viveksingh
 * Date 22/06/17.
 * This class is used to convert the byte array to jsonElement and vice versa
 */

public class ByteArrayAdapter {
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        return Base64.decode(json.getAsString(), Base64.NO_WRAP);
    }

    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {

        return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
    }
}
