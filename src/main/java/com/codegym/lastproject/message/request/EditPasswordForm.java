package com.codegym.lastproject.message.request;

import javax.validation.constraints.NotNull;

public class EditPasswordForm {
    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
