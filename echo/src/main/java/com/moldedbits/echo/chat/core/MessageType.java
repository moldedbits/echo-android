package com.moldedbits.echo.chat.core;

public enum MessageType {
    P2P_CHAT(1),
    P2P_ATTACHMENT_IMAGE(2),
    LWTD(3),
    LWTC(4),
    SYS_ACK(5),
    P2P_FIRST_MSG(6),
    P2P_ATTACHMENT_DOC(7);

    private int value;

    public int getValue() {
        return value;
    }

    MessageType(int val) {
        value = val;
    }

    public static MessageType valueOf(final int val) {
        switch (val) {
            case 1:
                return P2P_CHAT;
            case 2:
                return P2P_ATTACHMENT_IMAGE;
            case 3:
                return LWTD;
            case 4:
                return LWTC;
            case 5:
                return SYS_ACK;
            case 6:
                return P2P_FIRST_MSG;
            case 7:
                return P2P_ATTACHMENT_DOC;
            default:
                return null;
        }
    }
}
