package com.fijimf.deepfijomega.controllers.user.forms;

public class ForgotPasswordForm {
    private final String email;

    public ForgotPasswordForm(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
