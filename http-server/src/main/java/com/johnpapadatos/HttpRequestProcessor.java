package com.johnpapadatos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Map;

import org.apache.tika.Tika;

public class HttpRequestProcessor {

    private HttpRequestProcessor() {
    }

    public static HttpResponse processRequest(HttpRequest httpRequest, String baseDir) throws IOException {
        if (baseDir.endsWith("/")) {
            baseDir = baseDir.substring(0, baseDir.length() - 1); // Strip trailing "/"
        }

        File requestedResource = new File(baseDir + httpRequest.getPath());
        byte[] fileContents = readFileContents(requestedResource);
        String mimeType = new Tika().detect(requestedResource);
        String contentDisposition = getContentDisposition(httpRequest.getHeaders(),
                getFilename(requestedResource.getName()));
        return buildSuccessfulResponse(fileContents, mimeType, contentDisposition);
    }

    private static byte[] readFileContents(File file) throws IOException {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (NoSuchFileException e) {
            e.printStackTrace();
            String message = "File " + getFilename(e.getMessage()) + " not found.";
            throw new NoSuchFileException(message);
        }
    }

    private static String getFilename(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    private static String getContentDisposition(Map<String, String> requestHeaders, String filename) {
        String contentDisposition = "inline";
        if (requestHeaders.containsKey("X-Content-Disposition")) {
            contentDisposition = requestHeaders.get("X-Content-Disposition");
            if (contentDisposition.equals("attachment")) {
                contentDisposition += "; filename=\"" + filename + "\"";
            }
        }
        return contentDisposition;
    }

    private static HttpResponse buildSuccessfulResponse(
            byte[] fileContents, String mimeType, String contentDisposition) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setStatusCode("200");
        httpResponse.setReasonPhrase("OK");
        httpResponse.setHeader("Content-Type", mimeType);
        httpResponse.setHeader("Content-Disposition", contentDisposition);
        httpResponse.setHeader("Content-Length", Integer.toString(fileContents.length));
        httpResponse.setHeader("Connection", "close");
        // Content-Encoding: gzip
        httpResponse.setBody(new String(fileContents));
        return httpResponse;
    }
}
