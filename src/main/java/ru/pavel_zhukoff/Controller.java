package ru.pavel_zhukoff;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.pavel_zhukoff.annotations.RequestMapping;
import ru.pavel_zhukoff.request.RequestMethod;
import ru.pavel_zhukoff.request.RequestParams;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class Controller implements HttpHandler {

    private Class<?> controllerClass;
    private HttpExchange httpExchange;
    private String baseUrl;

    public Controller(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
        baseUrl = controllerClass.getAnnotation(ru.pavel_zhukoff.annotations.Controller.class).baseUrl();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        httpExchange = exchange;
        String requestUri = httpExchange.getRequestURI().getPath();
        Map<String, Method> actions = new HashMap<>();
        for (Method action: controllerClass.getDeclaredMethods()) {
            if (action.isAnnotationPresent(RequestMapping.class)
                    && requestUri.startsWith(baseUrl)
                    && requestUri.contains(action.getAnnotation(RequestMapping.class).uri())) {
                actions.put(String.format("%s%s_%s",
                        baseUrl,
                        action.getAnnotation(RequestMapping.class).uri(),
                        action.getAnnotation(RequestMapping.class).requsetType().toString()), action);
            }
        }
        Method action = actions.get(String.format("%s_%s", requestUri, httpExchange.getRequestMethod()));
            if (action.getAnnotation(RequestMapping.class).requsetType().toString()
                        .equals(httpExchange.getRequestMethod().toUpperCase())) {
                Page page = null;
                try {
                    if (action.getParameterCount() == 0) {
                        page = (Page) action.invoke(controllerClass
                                        .getConstructor(null)
                                        .newInstance(null),
                                null);
                    } else {
                        page = invokeParametrizedMethod(action);
                    }
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
                    httpExchange.sendResponseHeaders(500, -1);
                }
            } else {
                httpExchange.sendResponseHeaders(405, -1);
            }
        httpExchange.getResponseBody().close();
    }

    private Page invokeParametrizedMethod(Method method) {
        Page page = null;
        List<Object> args = new ArrayList<>();
        String query;
        if (httpExchange.getRequestMethod().equals(RequestMethod.GET.name())) {
            query = httpExchange.getRequestURI().getRawQuery();
        } else {
            Scanner s = new Scanner(httpExchange.getRequestBody()).useDelimiter("\\A");
            query = s.hasNext() ? s.next() : "";
        }
        // Get the first entry in List of 1 element and then get the first value of entrySet value
        String header = httpExchange
                .getRequestHeaders()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals("Content-type"))
                .collect(Collectors.toList())
                .get(0).getValue().get(0);
        List<List<Object>> params = RequestParams.parse(query, httpExchange.getRequestMethod(), header);
        if (params == null) {
            throw new Exception("Params not found!");
        }
        for (List<Object> arg: params) {
            String key = String.valueOf(arg.get(0));
            Object value = arg.get(1);
            // TODO: Пересмотреть подход, потому что непонятно ничего
        }

        try {
            page = (Page) method.invoke(controllerClass
                            .getConstructor(null)
                            .newInstance(null),
                    args.toArray());
        } catch (IllegalAccessException
                | InvocationTargetException
                | InstantiationException
                | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return page;
    }

    private void printConnection(HttpExchange httpExchange) {
        StringBuilder sb = new StringBuilder();
        sb.append('\n').append(new Date()).append(" --- ");
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

        System.out.println("--- Requset Body ---");
        Scanner s = new Scanner(httpExchange.getRequestBody()).useDelimiter("\\A");
        System.out.println(s.hasNext() ? s.next() : "");
    }
}
