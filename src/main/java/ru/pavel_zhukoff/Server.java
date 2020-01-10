package ru.pavel_zhukoff;

import com.sun.net.httpserver.HttpServer;
import org.reflections.Reflections;
import ru.pavel_zhukoff.annotations.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.Executor;

public class Server {

    private static Server instance;
    private HttpServer server;

    private Server() throws IOException {
        server = HttpServer.create();
    }

    public static Server getInstance() throws IOException {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void run() throws IOException {
        server.bind(new InetSocketAddress(80), 0);
        server.setExecutor(null);
        try {
            loadControllers();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(int port) throws IOException {
        server.bind(new InetSocketAddress(port), 0);
        server.setExecutor(null);
        try {
            loadControllers();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadControllers() throws Exception {

        Reflections refs = new Reflections("ru.pavel_zhukoff.controller"); // TODO: Закинуть это в настройки
        Set<Class<?>> controllers = refs.getTypesAnnotatedWith(Controller.class);
        for (Class<?> controller: controllers) {
            server.createContext(controller.getAnnotation(Controller.class).baseUrl(),
                    new ru.pavel_zhukoff.Controller(controller));
        }
    }
}