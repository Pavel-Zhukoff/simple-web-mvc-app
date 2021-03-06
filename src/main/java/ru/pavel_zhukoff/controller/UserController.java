package ru.pavel_zhukoff.controller;

import ru.pavel_zhukoff.Page;
import ru.pavel_zhukoff.annotations.Controller;
import ru.pavel_zhukoff.annotations.RequestMapping;
import ru.pavel_zhukoff.annotations.RequestParam;
import ru.pavel_zhukoff.forms.UserForm;
import ru.pavel_zhukoff.request.RequestMethod;

@Controller(baseUrl = "/user")
public class UserController {

    @RequestMapping
    public Page index() {
        return new Page("<h1>Hello, User!</h1>");
    }

    @RequestMapping(uri = "/register")
    public Page register(@RequestParam UserForm user) {
        System.out.println("IN CONTROLLER");
        System.out.println(user);

        return new Page(user.toString());
    }

    @RequestMapping(uri = "/register", requsetType = RequestMethod.POST)
    public Page registerPost(@RequestParam UserForm user) {
        System.out.println("IN CONTROLLER");
        System.out.println(user);

        return new Page(user.toString());
    }
}
