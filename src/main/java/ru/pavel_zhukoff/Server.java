package ru.pavel_zhukoff;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

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
        loadControllers();
        server.start();
    }

    public void run(int port) throws IOException {
        server.bind(new InetSocketAddress(port), 0);
        loadControllers();
        server.start();
    }

    private void loadControllers() {

    }
}
