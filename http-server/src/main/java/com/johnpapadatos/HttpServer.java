package com.johnpapadatos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class HttpServer {
    private final ServerSocket serverSocket;
    private final ExecutorService threadPool;

    public HttpServer(ServerSocket serverSocket, ExecutorService threadPool) {
        this.serverSocket = serverSocket;
        this.threadPool = threadPool;
    }

    public void start(String baseDir) throws IOException {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                threadPool.submit(new WorkerRunnable(socket, baseDir));
            }
        } finally {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (!threadPool.isShutdown()) {
                threadPool.shutdown();
            }
        }
    }
}
