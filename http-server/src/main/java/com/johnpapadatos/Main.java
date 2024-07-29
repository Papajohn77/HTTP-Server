package com.johnpapadatos;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String CONFIG_PROPERTIES_FILE_PATH = "./config/config.properties";

    public static void main(String[] args) {
        try {
            Config config = ConfigProcessor.processConfigFile(CONFIG_PROPERTIES_FILE_PATH);
            displayAppliedConfigOptions(config);

            ServerSocket serverSocket = new ServerSocket(config.getPort(), config.getBacklog());
            System.out.println("Listening on port: " + config.getPort());

            // Ensures that the server will be able to restart without
            // waiting for old connections to time out.
            serverSocket.setReuseAddress(true);

            ExecutorService threadPool = new ThreadPoolExecutor(
                    config.getCorePoolSize(),
                    config.getMaximumPoolSize(),
                    config.getKeepAliveTime(),
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>());

            HttpServer httpServer = new HttpServer(serverSocket, threadPool);
            Thread serverThread = new Thread(() -> {
                try {
                    httpServer.start(config.getBaseDir());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void displayAppliedConfigOptions(Config config) {
        System.out.println("=========== Configuration ===========");
        System.out.println("Port: " + config.getPort());
        System.out.println("Base directory: " + config.getBaseDir());
        System.out.println("Core pool size: " + config.getCorePoolSize());
        System.out.println("Maximum pool size: " + config.getMaximumPoolSize());
        System.out.println("Keep alive time: " + config.getKeepAliveTime());
        System.out.println("=====================================");
    }
}