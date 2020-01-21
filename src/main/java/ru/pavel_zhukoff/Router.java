package ru.pavel_zhukoff;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.pavel_zhukoff.annotations.RequestMapping;
import ru.pavel_zhukoff.annotations.RequestParam;
import ru.pavel_zhukoff.request.ParamFactory;
import ru.pavel_zhukoff.request.RequestMethod;
import ru.pavel_zhukoff.request.RequestParams;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

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
                        page = invokeParametrizedMethod(action);
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

    private Page invokeParametrizedMethod(Method method) throws IllegalArgumentException
                                                                , NoSuchMethodException
                                                                , IllegalAccessException
                                                                , InvocationTargetException
                                                                , InstantiationException {
        Page page = null;
        List<Object> args = new ArrayList<>();
        Map<String, Object> paramsMap = new HashMap<>();
        String requestMethod = httpExchange.getRequestMethod();
        String contentType = httpExchange.getRequestHeaders().containsKey("Content-type")?
                httpExchange.getRequestHeaders().get("Content-type").get(0):
                "";
        String query;
        if (requestMethod.equals(RequestMethod.GET.name())) {
            query = httpExchange.getRequestURI().getRawQuery();
        } else {
            Scanner s = new Scanner(httpExchange.getRequestBody()).useDelimiter("\\A");
            query = s.hasNext() ? s.next() : "";
        }
        List<List<Object>> params = RequestParams.parse(query, requestMethod, contentType);
        if (params == null) {
            throw new IllegalArgumentException("Params not found!");
        }
        for (List<Object> arg: params) {
            String key = String.valueOf(arg.get(0));
            Object value = arg.get(1);
            if (!paramsMap.containsKey(key)) {
                paramsMap.put(key, value);
            } else if (paramsMap.containsKey(key) && paramsMap.get(key) instanceof List<?>) {
                ((List<Object>) paramsMap.get(key)).add(value);
            } else {
                Object buff = paramsMap.get(key);
                List<Object> array = new ArrayList<>();
                array.add(buff);
                array.add(value);
                paramsMap.put(key, array);
            }
        }
        ParamFactory paramFactory = new ParamFactory(method, paramsMap);
        Object[] args1 = paramFactory.getParsedArgs();
        page = (Page) method.invoke(controllerClass
                        .getConstructor(null)
                        .newInstance(null),
                args1);
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
