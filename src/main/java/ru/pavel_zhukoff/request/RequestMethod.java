package ru.pavel_zhukoff.request;

public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT");

    private String name;

    RequestMethod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
