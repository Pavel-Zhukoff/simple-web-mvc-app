package ru.pavel_zhukoff.controller;

import ru.pavel_zhukoff.Page;
import ru.pavel_zhukoff.annotations.Controller;
import ru.pavel_zhukoff.annotations.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @RequestMapping
    public Page index() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Pasha");
        return new Page("indexTemplate", data);
    }
}
