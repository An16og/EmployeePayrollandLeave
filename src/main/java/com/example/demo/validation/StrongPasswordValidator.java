package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 64;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // null/blank is handled by @NotBlank — let it pass here to avoid double-reporting
        if (password == null || password.isBlank()) {
            return true;
        }

        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            return false;
        }

        boolean hasUpper   = false;
        boolean hasLower   = false;
        boolean hasDigit   = false;
        boolean hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch))      hasUpper   = true;
            else if (Character.isLowerCase(ch))  hasLower   = true;
            else if (Character.isDigit(ch))      hasDigit   = true;
            else                                 hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
