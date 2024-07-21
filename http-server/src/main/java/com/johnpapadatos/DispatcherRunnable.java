package com.johnpapadatos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class DispatcherRunnable implements Runnable {
    private final int port;
    private final BlockingQueue<Socket> queue;

    public DispatcherRunnable(int port, BlockingQueue<Socket> queue) {
        this.port = port;
        this.queue = queue;
    }

    /*
     * The dispatcher thread listens for incoming client requests
     * on the specified port. Upon receiving a request, it accepts
     * the client socket connection to establish a TCP connection,
     * and then puts the client socket to the queue to be serviced
     * by the worker threads.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Ensures that the server will be able to restart without
            // waiting for old connections to time out.
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket socket = serverSocket.accept();
                queue.put(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("InterruptedException: " + e.getMessage());
        }
    }
}
