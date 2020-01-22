package ru.pavel_zhukoff.request;

import ru.pavel_zhukoff.request.adapter.QueryAdapter;

import java.util.List;

public class RequestParams {

    public static List<List<Object>> parse(String query, String requestMethod, String contentType) {
        if (requestMethod.equals(RequestMethod.GET.name())) {
            RequestParserAdapter rpa = new QueryAdapter();
            return rpa.parse(query);
        } else {
            for (PostContentType pct: PostContentType.values()) {
                if (pct.getName().equals(contentType)) {
                    return pct.getAdapter().parse(query);
                }
            }
        }
        return null;
    }
}
