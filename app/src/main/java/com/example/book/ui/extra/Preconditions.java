package com.example.book.ui.extra;

import android.text.TextUtils;

import java.util.regex.Pattern;

public final class Preconditions {
    public static final int MINPASSWORDLENGTH = 8;

    public static boolean checkNotNull(String string) {
        if (string == null)  {
            return false;
        } else {
            return true;
        }
    }
    public static boolean checkNotEmpty(String string) {
        if (TextUtils.isEmpty(string)) {
            return false;
        } else {
            return true;
        }
    }
    public static boolean isStrongPassword(String password) {
        return hasMinimumLength(password) &&
                containsUppercaseCharacter(password) &&
                containsLowercaseCharacter(password) &&
                containsNumericalCharacter(password) &&
                containsSpecialCharacter(password);
    }
    private static boolean hasMinimumLength(String password) {
        return password.length() >= MINPASSWORDLENGTH;
    }
    private static boolean containsUppercaseCharacter(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }
    private static boolean containsLowercaseCharacter(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }
    private static boolean containsSpecialCharacter(String password) {
        String specialCharacters = "!@#$%^&*()-_=+";

        for (char c : password.toCharArray()) {
            if (specialCharacters.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }
    private static boolean containsNumericalCharacter(String string) {
        for (char c : string.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
    public static boolean validPakistanNumber(String phoneNumber) {
        String regex = "^\\+92\\d{10}$";
        return Pattern.matches(regex, phoneNumber);
    }
}