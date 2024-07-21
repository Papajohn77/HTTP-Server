package com.johnpapadatos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int DEFAULT_PORT = 4221;
    private static final int DEFAULT_CORE_POOL_SIZE = 8;
    private static final int DEFAULT_MAX_POOL_SIZE = 32;
    private static final int DEFAULT_KEEP_ALIVE = 3000;

    public static void main(String[] args) {
        // Read a configuration file from a specific location
        // Parse the configuration file and create a Config DTO

        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            // Ensures that the server will be able to restart without
            // waiting for old connections to time out.
            serverSocket.setReuseAddress(true);

            ExecutorService threadPool = new ThreadPoolExecutor(
                    DEFAULT_CORE_POOL_SIZE,
                    DEFAULT_MAX_POOL_SIZE,
                    DEFAULT_KEEP_ALIVE,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>());

            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.submit(new WorkerRunnable(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException: " + e.getMessage());
        }
    }
}