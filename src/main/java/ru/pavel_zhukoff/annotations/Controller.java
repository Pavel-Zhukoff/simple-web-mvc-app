package ru.pavel_zhukoff.annotations;

public @interface Controller {
    String baseUrl() default "/";
}
