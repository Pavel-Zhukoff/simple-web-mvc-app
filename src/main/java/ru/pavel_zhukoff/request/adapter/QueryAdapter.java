package ru.pavel_zhukoff.request.adapter;

import ru.pavel_zhukoff.request.RequestParserAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class QueryAdapter implements RequestParserAdapter {

    @Override
    public List<List<Object>> parse(String query) {
        List<List<Object>> params = new ArrayList<>();
        String[] parts = query.split("[&]");
        for (String part: parts) {
            String[] param = part.split("[=]");
            try {
                List<Object> buff = new ArrayList<>(2);
                buff.add(param[0]);
                buff.add(URLDecoder.decode(param[1], "UTF-8"));
                params.add(buff);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return params;
    }
}
