package com.dailycodework.universalpetcare.email;

/**
 * Signals that an email could not be sent through the configured mail sender.
 */
public class EmailSendException extends RuntimeException {

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailSendException(String message) {
        super(message);
    }
}
