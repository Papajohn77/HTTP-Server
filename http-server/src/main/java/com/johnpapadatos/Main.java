package com.johnpapadatos;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        BlockingQueue<Socket> queue = new LinkedBlockingQueue<>();

        DispatcherRunnable dispatcherRunnable = new DispatcherRunnable(DEFAULT_PORT, queue);

        Thread dispatcherThread = new Thread(dispatcherRunnable, "Dispatcher");

        dispatcherThread.start();
    }
}