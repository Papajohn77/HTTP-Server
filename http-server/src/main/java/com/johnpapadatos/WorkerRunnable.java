package com.johnpapadatos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.NoSuchFileException;

public class WorkerRunnable implements Runnable {
    private final Socket socket;

    public WorkerRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            handleNetworkRequest(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleNetworkRequest(InputStream in, OutputStream out) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpRequest httpRequest = HttpRequestParser.parseRequest(br);
            HttpResponse httpResponse = HttpRequestProcessor.processRequest(httpRequest);
            out.write(httpResponse.asBytes());
            out.flush();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            ErrorResponseHandler.send400ErrorResponse(out, e.getMessage());
        } catch (NoSuchFileException e) {
            e.printStackTrace();
            ErrorResponseHandler.send404ErrorResponse(out, e.getMessage());
        } catch (UnsupportedMediaTypeException e) {
            e.printStackTrace();
            ErrorResponseHandler.send415ErrorResponse(out, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            ErrorResponseHandler.send500ErrorResponse(out, e.getMessage());
        }
    }
}
