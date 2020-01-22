package ru.pavel_zhukoff.request;

import ru.pavel_zhukoff.request.adapter.JsonAdapter;
import ru.pavel_zhukoff.request.adapter.MultipartAdapter;
import ru.pavel_zhukoff.request.adapter.QueryAdapter;

public enum PostContentType {
    JSON("application/json", new JsonAdapter()),
    MULTIPART("multipart/form-data", new MultipartAdapter()),
    URLENCODED("application/x-www-form-urlencoded", new QueryAdapter());

    private String name;
    private RequestParserAdapter adapter;

    PostContentType(String name, RequestParserAdapter adapter) {
        this.name = name;
        this.adapter = adapter;
    }

    public String getName() {
        return name;
    }

    public RequestParserAdapter getAdapter() {
        return adapter;
    }

    @Override
    public String toString() {
        return name;
    }
}
