package com.fijimf.deepfijomega.manager;

public class DuplicatedUsernameException extends RuntimeException {
    public DuplicatedUsernameException(String username) {
        super("User with '"+username+"' already exists.");
    }
}
