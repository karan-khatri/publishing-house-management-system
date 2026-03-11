package org.swam.publishing_house.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.swam.publishing_house.util.HelperUtil;

public class ChangePasswordRequestDTO {
    @NotBlank(message = "Current password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern(regexp = HelperUtil.PASSWORD_REGEX, message = HelperUtil.PASSWORD_REQUIREMENTS_MESSAGE)
    private String newPassword;

    // Getters and setters remain the same
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}