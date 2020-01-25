package ru.pavel_zhukoff;

import com.sun.net.httpserver.HttpServer;
import org.reflections.Reflections;
import ru.pavel_zhukoff.annotations.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;

public class Server {

    private static Server instance;
    private HttpServer server;

    private Server() throws IOException {
        Config.loadConfig();
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
        start();
    }

    public void run(int port) throws IOException {
        server.bind(new InetSocketAddress(port), 0);
        start();
    }

    private void start() {
        server.setExecutor(null);
        loadControllers();
        server.start();
    }

    private void loadControllers() {
        Reflections refs = new Reflections(Config.getProperty("controller.package"));
        Set<Class<?>> controllers = refs.getTypesAnnotatedWith(Controller.class);
        for (Class<?> controller: controllers) {
            server.createContext(controller.getAnnotation(Controller.class).baseUrl(),
                    new Router(controller));
        }
    }
}