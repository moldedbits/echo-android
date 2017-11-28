package com.moldedbits.echo.chat;

/**
 * Author viveksingh
 * Date 16/06/17.
 * This exception is needed to thrown if a field required to setup the Echo configuration is missing
 */

public class MandatoryFieldException extends RuntimeException {
    public String getDetailedMessage() {
        return "this is mandatory field. Please provide the values in EchoConfiguration";
    }

    public String getMessage(){
        return super.getMessage();
    }
}
