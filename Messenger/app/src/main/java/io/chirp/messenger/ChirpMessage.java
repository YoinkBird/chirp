/*------------------------------------------------------------------------------
 *
 *  This file is part of the Chirp SDK for Android.
 *  For full information on usage and licensing, see http://chirp.io/
 *
 *  Copyright © 2011-2016, Asio Ltd.
 *  All rights reserved.
 *
 *----------------------------------------------------------------------------*/

package io.chirp.messenger;

public class ChirpMessage {
    public enum Type {
        RECEIVED, SENT
    }

    private Type type;
    private String message;
    private String identifier;

    public ChirpMessage(Type type, String message, String identifier) {
        this.type = type;
        this.message = message;
        this.identifier = identifier;
    }

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }
}
