package ru.pavel_zhukoff.exceptions;

public class NoSuchNotNullParameter extends Exception {
    public NoSuchNotNullParameter() {
        super();
    }

    public NoSuchNotNullParameter(String message) {
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
