package ru.pavel_zhukoff;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.pavel_zhukoff.annotations.RequestParam;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

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
        for (Method action: controllerClass.getDeclaredMethods()) {
            if (action.isAnnotationPresent(RequestParam.class)
                    && requestUri.startsWith(baseUrl)
                    && requestUri.contains(action.getAnnotation(RequestParam.class).uri())
                    && action.getAnnotation(RequestParam.class).requsetType().toString()
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
                httpExchange.getResponseBody().close();
            }
        }
    }

    private void printConnection(HttpExchange httpExchange) {
        StringBuilder sb = new StringBuilder();
        sb.append(new Date()).append(" -- ");
        sb.append(httpExchange.getRequestMethod()).append(" -- ").append(httpExchange.getRequestURI());
        System.out.println(sb.toString());
    }
}
