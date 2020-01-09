package ru.pavel_zhukoff.enums;

public enum RequestType {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT");

    private String name;

    RequestType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
