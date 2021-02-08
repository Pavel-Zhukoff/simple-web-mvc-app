package ru.pavel_zhukoff.request.adapter;

import ru.pavel_zhukoff.request.RequestParserAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MultipartAdapter implements RequestParserAdapter {

    @Override
    public List<List<Object>> parse(String query) {
        List<List<Object>> params = new ArrayList<>();
        if (query == null) {
            return params;
        }
        Pattern querySplitPattern = Pattern.compile(query.split("\r\n")[0]);
        String[] parts = querySplitPattern.split(query);
        for (int i = 1; i < parts.length-1; i++) {
            Pattern keyPattern = Pattern.compile("name=\"(.*)\"\r\n");
            Pattern valuePattern = Pattern.compile("\r\n\r\n(.*)\r\n");
            Matcher keyMatcher = keyPattern.matcher(parts[i]);
            Matcher valueMatcher = valuePattern.matcher(parts[i]);
            if (!keyMatcher.find()) continue;
            String key = keyMatcher.group(1);
            String value = "";
            if (valueMatcher.find()) {
                value = valueMatcher.group(1);
            }
            List<Object> buff = new ArrayList<>(2);
            buff.add(key);
            buff.add(value);
            params.add(buff);
        }
        return params;
    }
}
