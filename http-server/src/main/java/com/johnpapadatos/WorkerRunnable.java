package com.johnpapadatos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WorkerRunnable implements Runnable {
    private final Socket socket;

    public WorkerRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream()) {

            HttpRequest httpRequest = HttpRequestParser.parseRequest(in);

            out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
