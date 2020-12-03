package com.fijimf.deepfijomega.exception;

public class DuplicatedUsernameException extends RuntimeException {
    public DuplicatedUsernameException(String username) {
        super("User with '"+username+"' already exists.");
    }
}
