package com.johnpapadatos;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parseRequest(BufferedReader br) throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        parseRequestLine(br, httpRequest);
        parseRequestHeaders(br, httpRequest);
        parseRequestBody(br, httpRequest);
        return httpRequest;
    }

    private static void parseRequestLine(
            BufferedReader br, HttpRequest httpRequest) throws IOException {
        String[] requestLineParts = br.readLine().split(" ");
        if (requestLineParts.length != 3) {
            throw new IllegalArgumentException("Invalid request-line.");
        }

        String method = requestLineParts[0];
        if (!method.equals("GET")) {
            throw new IllegalArgumentException(method + " method not supported.");
        }
        httpRequest.setMethod(method);

        String path = requestLineParts[1];
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Invalid request-target.");
        }
        httpRequest.setPath(path);

        String version = requestLineParts[2];
        if (!version.equals("HTTP/1.1")) {
            throw new IllegalArgumentException("Currently only HTTP 1.1 is supported.");
        }
        httpRequest.setVersion(version);
    }

    private static void parseRequestHeaders(
            BufferedReader br, HttpRequest httpRequest) throws IOException {
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            String[] headerParts = line.split(": ");
            if (headerParts.length != 2) {
                throw new IllegalArgumentException("Invalid header: " + line);
            }
            httpRequest.setHeader(headerParts[0], headerParts[1]);
        }
    }

    private static void parseRequestBody(
            BufferedReader br, HttpRequest httpRequest) throws IOException {
        if (!httpRequest.getHeaders().containsKey("Content-Length")) {
            httpRequest.setBody("");
            return;
        }

        int contentLength = Integer.parseInt(httpRequest.getHeaders().get("Content-Length"));
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, buffer.length);
        httpRequest.setBody(new String(buffer));
    }
}
