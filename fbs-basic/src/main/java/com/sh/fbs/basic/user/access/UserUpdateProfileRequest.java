package com.sh.fbs.basic.user.access;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Data
public class UserUpdateProfileRequest {
    @NotBlank(message = "Nickname is required")
    @Length(min = 2, max = 20, message = "Nickname length must be between 2 and 20 characters")
    private String nickname;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "Invalid phone number format")
    private String phone;
    
    @NotBlank(message = "Icon URL is required")
    private String icon;
    
    @Range(min = 0, max = 1, message = "Sex must be 0 or 1")
    private int sex;
    
    @NotBlank(message = "Birth date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Birth date format must be yyyy-MM-dd")
    private String birthDateStr;
    
    @NotBlank(message = "Area is required")
    private String area;
} 