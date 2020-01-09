package ru.pavel_zhukoff;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpPrincipal;
import ru.pavel_zhukoff.annotations.RequestParam;
import ru.pavel_zhukoff.controllers.MainController;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller implements HttpHandler {

    private Class<?> controllerClass;
    private String baseUrl;

    public Controller(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
        baseUrl = controllerClass.getAnnotation(ru.pavel_zhukoff.annotations.Controller.class).baseUrl();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        printConnection(httpExchange);
        String requestUri = httpExchange.getRequestURI().getPath();
        Map<String, Method> actions = new HashMap<>();
        for (Method action: controllerClass.getDeclaredMethods()) {
            if (action.isAnnotationPresent(RequestParam.class)
                    && requestUri.startsWith(baseUrl)
                    && requestUri.contains(action.getAnnotation(RequestParam.class).uri())) {
                actions.put(String.format("%s%s", baseUrl, action.getAnnotation(RequestParam.class).uri()), action);
            }
        }
        Method action = actions.get(requestUri);
            if (action.getAnnotation(RequestParam.class).requsetType().toString()
                        .equals(httpExchange.getRequestMethod().toUpperCase())) {
                Page page = null;
                try {
                    page = (Page) action.invoke(controllerClass
                                    .getConstructor(null)
                                    .newInstance(null),
                            null);
                } catch (IllegalAccessException
                        | InvocationTargetException
                        | InstantiationException
                        | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                if (page != null) {
                    httpExchange.sendResponseHeaders(200, page.toString().getBytes().length);
                    httpExchange.getResponseBody().write(page.toString().getBytes());
                } else {
                    httpExchange.sendResponseHeaders(500, 0);
                }
            } else {
                httpExchange.sendResponseHeaders(500, 0);
            }
        httpExchange.getResponseBody().close();
    }

    private void printConnection(HttpExchange httpExchange) {
        StringBuilder sb = new StringBuilder();
        sb.append(new Date()).append(" --- ");
        sb.append(httpExchange.getRequestMethod()).append(" --- ").append(httpExchange.getRequestURI());
        System.out.println(sb.toString());

        System.out.println("--- Headers ---");
        Headers requestHeaders = httpExchange.getRequestHeaders();
        requestHeaders.entrySet().forEach(System.out::println);

        System.out.println("--- HTTP method ---");
        String requestMethod = httpExchange.getRequestMethod();
        System.out.println(requestMethod);

        System.out.println("--- Query ---");
        URI requestURI = httpExchange.getRequestURI();
        String query = requestURI.getQuery();
        System.out.println(query);
    }
}
