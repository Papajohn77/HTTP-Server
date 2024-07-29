package com.johnpapadatos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.apache.tika.Tika;

public class HttpRequestProcessor {

    private HttpRequestProcessor() {
    }

    // No DI - will be tested through integration tests
    public static HttpResponse processRequest(HttpRequest httpRequest, String baseDir) throws IOException {
        if (baseDir.endsWith("/")) {
            baseDir = baseDir.substring(0, baseDir.length() - 1); // Strip trailing "/"
        }

        File requestedResource = new File(baseDir + httpRequest.getPath());
        byte[] fileContents = readFileContents(requestedResource);
        String mimeType = new Tika().detect(requestedResource);
        String contentDisposition = getContentDisposition(httpRequest.getHeaders(),
                getFilename(requestedResource.getName()));
        boolean gzipCompression = clientSupportsGzipCompression(httpRequest.getHeaders());
        return buildSuccessfulResponse(fileContents, mimeType, contentDisposition, gzipCompression);
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

    private static boolean clientSupportsGzipCompression(Map<String, String> requestHeaders) {
        return requestHeaders.containsKey("Accept-Encoding") && requestHeaders.get("Accept-Encoding").contains("gzip");
    }

    private static HttpResponse buildSuccessfulResponse(
            byte[] fileContents, String mimeType, String contentDisposition, boolean gzipCompression)
            throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setStatusCode("200");
        httpResponse.setReasonPhrase("OK");
        httpResponse.setHeader("Content-Type", mimeType);
        httpResponse.setHeader("Content-Disposition", contentDisposition);
        httpResponse.setHeader("Connection", "close");

        if (gzipCompression) {
            httpResponse.setHeader("Content-Encoding", "gzip");
            byte[] bodyAsBytesGzipCompressed = getBodyAsBytesGzipCompressed(fileContents);
            httpResponse.setBody(bodyAsBytesGzipCompressed);
            httpResponse.setHeader("Content-Length", Integer.toString(bodyAsBytesGzipCompressed.length));
        } else {
            httpResponse.setBody(fileContents);
            httpResponse.setHeader("Content-Length", Integer.toString(fileContents.length));
        }

        return httpResponse;
    }

    private static byte[] getBodyAsBytesGzipCompressed(byte[] body) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(os);
        gzipOutputStream.write(body, 0, body.length);
        gzipOutputStream.close();
        return os.toByteArray();
    }
}
