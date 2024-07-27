package com.johnpapadatos;

import java.io.IOException;
import java.io.OutputStream;

public class ErrorResponseHandler {
    private static final String CRLF = "\r\n";

    private ErrorResponseHandler() {
    }

    public static void send400ErrorResponse(OutputStream out, String message) throws IOException {
        String response = buildErrorResponse("400", "Bad Request", message);
        out.write(response.getBytes());
        out.flush();
    }

    public static void send404ErrorResponse(OutputStream out, String message) throws IOException {
        String response = buildErrorResponse("404", "Not Found", message);
        out.write(response.getBytes());
        out.flush();
    }

    public static void send500ErrorResponse(OutputStream out, String message) throws IOException {
        String response = buildErrorResponse("500", "Internal Server Error", message);
        out.write(response.getBytes());
        out.flush();
    }

    private static String buildErrorResponse(String statusCode, String reasonPhrase, String message) {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1").append(" ").append(statusCode).append(" ").append(reasonPhrase).append(CRLF);
        response.append("Content-Type: text/plain").append(CRLF);
        response.append("Content-Length: ").append(message.length()).append(CRLF);
        response.append("Connection: close").append(CRLF);
        response.append(CRLF);
        response.append(message);
        return response.toString();
    }
}
