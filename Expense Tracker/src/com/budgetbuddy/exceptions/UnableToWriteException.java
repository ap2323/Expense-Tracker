package com.budgetbuddy.exceptions;

public class UnableToWriteException extends RuntimeException{
    public UnableToWriteException(String message){
        super(message);
    }
}
