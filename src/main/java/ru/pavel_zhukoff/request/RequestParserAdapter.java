package ru.pavel_zhukoff.request;

import java.util.List;

public interface RequestParserAdapter {
    public List<List<Object>> parse(String query);
}
