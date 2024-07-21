package com.johnpapadatos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class WorkerRunnable implements Runnable {
    private final Socket socket;

    public WorkerRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream out = socket.getOutputStream()) {

            try {
                HttpRequest httpRequest = HttpRequestParser.parseRequest(br);
                out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            } catch (IllegalArgumentException e) {
                String errorMsg = e.getMessage();
                String response = "HTTP/1.1 400 Bad Request\r\nContent-Length: " + errorMsg.length() + "\r\n\r\n"
                        + errorMsg;
                out.write(response.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
