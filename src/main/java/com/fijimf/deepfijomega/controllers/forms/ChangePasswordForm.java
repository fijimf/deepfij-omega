package com.fijimf.deepfijomega.controllers.forms;

public class ChangePasswordForm {
    private final String oldPassword;
    private final String newPassword;

    public ChangePasswordForm(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
