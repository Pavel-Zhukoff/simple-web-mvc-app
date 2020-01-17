package ru.pavel_zhukoff.request.adapter;

import ru.pavel_zhukoff.request.RequestParserAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipartAdapter implements RequestParserAdapter {

    @Override
    public List<List<Object>> parse(String query) {
        List<List<Object>> params = new ArrayList<>();
        if (query == null) {
            return params;
        }
        String[] parts = query.split("------WebKitFormBoundary");
        for (int i = 0; i < parts.length-1; i++) {
            Pattern keyPattern = Pattern.compile("name=\"(.*)\"\n");
            Pattern valuePattern = Pattern.compile("\n(.*)\n--");
            String key = keyPattern.matcher(parts[i]).group(1);
            String value = valuePattern.matcher(parts[i]).group(1);
            List<Object> buff = new ArrayList<>(2);
            buff.add(key);
            buff.add(value);
            params.add(buff);
        }
        return params;
    }
}
