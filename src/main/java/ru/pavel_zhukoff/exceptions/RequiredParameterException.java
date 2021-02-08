package ru.pavel_zhukoff.exceptions;

public class RequiredParameterException extends Exception {
    public RequiredParameterException() {
        super();
    }

    public RequiredParameterException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
