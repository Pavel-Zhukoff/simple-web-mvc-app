package ru.pavel_zhukoff;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.pavel_zhukoff.annotations.RequestMapping;
import ru.pavel_zhukoff.annotations.RequestParam;
import ru.pavel_zhukoff.request.RequestMethod;
import ru.pavel_zhukoff.request.RequestParams;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class Router implements HttpHandler {

    private Class<?> controllerClass;
    private HttpExchange httpExchange;
    private String baseUrl;

    public Router(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
        baseUrl = controllerClass.getAnnotation(ru.pavel_zhukoff.annotations.Controller.class).baseUrl();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        httpExchange = exchange;
        printConnection();
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
                        String query;
                        if (httpExchange.getRequestMethod().equals(RequestMethod.GET.name())) {
                            query = httpExchange.getRequestURI().getRawQuery();
                        } else {
                            Scanner s = new Scanner(httpExchange.getRequestBody()).useDelimiter("\\A");
                            query = s.hasNext() ? s.next() : "";
                        }
                        page = invokeParametrizedMethod(action,
                                query,
                                httpExchange.getRequestMethod());
                    }
                } catch (IllegalAccessException
                        | IllegalArgumentException
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
// ПЕРЕДЕЛАТЬ ЭТО ДЛЯ КЛАССОВ

    private Page invokeParametrizedMethod(Method method,
                                          String query,
                                          String requestMethod) throws IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Page page = null;
        List<Object> args = new ArrayList<>();
        Map<String, List<Object>> aargs = new HashMap<>();
        String contentType = httpExchange.getRequestHeaders().containsKey("Content-type")?
                httpExchange.getRequestHeaders().get("Content-type").get(0):
                "";
        List<List<Object>> params = RequestParams.parse(query, requestMethod, contentType);
        if (params == null) {
            throw new IllegalArgumentException("Params not found!");
        }
        for (List<Object> arg: params) {
            String key = String.valueOf(arg.get(0));
            Object value = arg.get(1);
            if (!aargs.containsKey(key)) {
                aargs.put(key, new ArrayList<>());
            }
            aargs.get(key).add(value);
        }
        for (Parameter param: method.getParameters()) {
            if (param.isAnnotationPresent(RequestParam.class)) {
                String argName = param.getAnnotation(RequestParam.class).name();
                if (aargs.containsKey(argName)) {
                    if (aargs.get(argName).size() > 1) {
                        args.add(aargs.get(argName));
                    } else {

                        args.add(aargs.get(argName).get(0));
                    }
                } else if (!param.getAnnotation(RequestParam.class).required()) {
                    args.add(null);
                } else {
                    throw new IllegalArgumentException(String.format("Argument %s is required!", argName));
                }
            }
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

    private void printConnection() {
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
