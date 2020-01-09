package ru.pavel_zhukoff.controllers;

import ru.pavel_zhukoff.Page;
import ru.pavel_zhukoff.annotations.Controller;
import ru.pavel_zhukoff.annotations.RequestParam;
import ru.pavel_zhukoff.enums.RequestType;

@Controller(baseUrl = "/user")
public class UserController {

    @RequestParam
    public Page index() {
        return new Page("<h1>Hello, User!</h1>");
    }

    @RequestParam(uri = "/register")
    public Page register() {
        return new Page("<h1>Hello, User! It's REGISTRATION</h1>");
    }

    @RequestParam(uri = "/register", requsetType = RequestType.POST)
    public Page registerPost() {
        return new Page("<h1>Hello, User! It's REGISTRATION</h1>");
    }
}
