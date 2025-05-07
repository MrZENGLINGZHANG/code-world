package com.sh.fbs.basic.user.access;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserChangePasswordRequest {
    @NotBlank(message = "Old password is required")
    @Length(min = 8, max = 20, message = "Password length must be between 8 and 20 characters")
    private String oldPassword;
    
    @NotBlank(message = "New password is required")
    @Length(min = 8, max = 20, message = "Password length must be between 8 and 20 characters")
    private String newPassword;
    
    @NotBlank(message = "Captcha is required")
    @Length(min = 4, max = 8, message = "Captcha length must be between 4 and 8 characters")
    private String captcha;
} 