package ru.pavel_zhukoff;

public class Page {
    private String body;

    public Page(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return body;
    }
}
