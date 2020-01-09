package ru.pavel_zhukoff.controllers;

import ru.pavel_zhukoff.Page;
import ru.pavel_zhukoff.annotations.Controller;
import ru.pavel_zhukoff.annotations.RequestParam;

@Controller
public class MainController {

    @RequestParam
    public Page index() {
        return new Page("<h1>Hello, World!</h1>");
    }
}
