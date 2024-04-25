package com.mediatica.onlinebanking.exceptions;

public class CurrencyMismatchException extends RuntimeException{
    public CurrencyMismatchException(String message)
    {
        super(message);
    }

}
