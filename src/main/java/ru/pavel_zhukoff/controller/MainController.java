package ru.pavel_zhukoff.controller;

import ru.pavel_zhukoff.Page;
import ru.pavel_zhukoff.annotations.Controller;
import ru.pavel_zhukoff.annotations.RequestMapping;

@Controller
public class MainController {

    @RequestMapping
    public Page index() {
        return new Page("<h1>Hello, World!</h1>");
    }
}
