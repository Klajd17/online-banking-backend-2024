package com.mediatica.onlinebanking.exceptions;

public class NoUserAuthenticatedException extends RuntimeException{
    public NoUserAuthenticatedException(String message)
    {
        super(message);
    }

}
