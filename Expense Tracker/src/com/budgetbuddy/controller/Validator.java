package com.budgetbuddy.controller;

final class Validator {

    public static boolean isValidMail(String mail) {
        String[] chunks = mail.split("@");
        if (chunks.length != 2) {
            return false;
        }

        String localPart = chunks[0];
        String domainPart = chunks[1];

        if (localPart.isEmpty() || domainPart.length() < 3) {
            return false;
        }

        if (!domainPart.contains(".") || domainPart.startsWith(".") || domainPart.endsWith(".")) {
            return false;
        }

        if (!Character.isLetter(localPart.charAt(0))) {
            return false;
        }

        for (char character : mail.toCharArray()) {
            if (!Character.isLetterOrDigit(character) && character != '_' && character != '.' && character != '@') {
                return false;
            }
        }

        if (mail.contains("..") || mail.contains(".@") || mail.contains("@.") || mail.contains("._.")) {
            return false;
        }

        return true;
    }


    public static boolean isValidPassword(String password) {
        // Check if password length is between 8 and 15
        if (password.length() < 8 || password.length() > 15) {
            return false;
        }

        // Check if password contains a space
        if (password.contains(" ")) {
            return false;
        }

        boolean hasDigit = false;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasSpecialChar = false;

        String specialCharacters = "@#!~$%^&*()-+/.:,<>?|";

        for (char ch : password.toCharArray()) {
            if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (Character.isUpperCase(ch)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowerCase = true;
            } else if (specialCharacters.indexOf(ch) >= 0) {
                hasSpecialChar = true;
            }
        }

        return hasDigit && hasUpperCase && hasLowerCase && hasSpecialChar;
    }

    public static boolean isValidTimeFormat(String time) {
        // Regular expression to validate time in HH:mm format (24-hour)
        String regex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";
        return time.matches(regex);
    }

}
