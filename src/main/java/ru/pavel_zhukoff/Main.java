package ru.pavel_zhukoff;

import java.io.IOException;

public class Main {

    public static void main(String... args) {
        try {
            ServerRunner.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
