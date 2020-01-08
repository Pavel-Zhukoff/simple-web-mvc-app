package ru.pavel_zhukoff;

import java.io.IOException;

public class ServerRunner {

    public static void run() throws IOException {
        Server.getInstance().run(8080);
    }
}
