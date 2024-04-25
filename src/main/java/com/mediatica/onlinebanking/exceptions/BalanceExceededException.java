package com.mediatica.onlinebanking.exceptions;

public class BalanceExceededException extends RuntimeException{

    public BalanceExceededException(String message)
    {
        super(message);
    }

}
