package ru.pavel_zhukoff.controller;

import ru.pavel_zhukoff.Page;
import ru.pavel_zhukoff.annotations.Controller;
import ru.pavel_zhukoff.annotations.RequestMapping;
import ru.pavel_zhukoff.request.RequestMethod;

@Controller(baseUrl = "/user")
public class UserController {

    @RequestMapping
    public Page index() {
        return new Page("<h1>Hello, User!</h1>");
    }

    @RequestMapping(uri = "/register")
    public Page register() {
        return new Page("<h1>Hello, User! It's REGISTRATION</h1>");
    }

    @RequestMapping(uri = "/register", requsetType = RequestMethod.POST)
    public Page registerPost() {
        return new Page("<h1>Hello, User! It's REGISTRATION</h1>");
    }
}
