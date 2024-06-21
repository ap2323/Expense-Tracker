package com.budgetbuddy.exceptions;

public class UserAlreadyFoundException extends RuntimeException{
    public UserAlreadyFoundException(String message){
        super(message);
    }
}
