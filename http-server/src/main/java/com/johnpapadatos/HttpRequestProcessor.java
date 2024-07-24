package com.johnpapadatos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class HttpRequestProcessor {

    private HttpRequestProcessor() {
    }

    public static HttpResponse processRequest(HttpRequest httpRequest)
            throws IOException, UnsupportedMediaTypeException {
        File requestedResource = new File(httpRequest.getPath().substring(1)); // Strip "/"
        byte[] fileContents = Files.readAllBytes(requestedResource.toPath());
        String mimeType = getMimeType(httpRequest.getPath());
        String contentDisposition = getContentDisposition(httpRequest.getHeaders(), requestedResource.getName());
        return buildSuccessfulResponse(fileContents, mimeType, contentDisposition);
    }

    private static String getMimeType(String path) throws UnsupportedMediaTypeException {
        String fileExtension = path.substring(path.lastIndexOf(".") + 1);
        Optional<MIME> mime = Arrays.stream(MIME.values())
                .filter(m -> fileExtension.equals(m.getExtension()))
                .findFirst();
        if (!mime.isPresent()) {
            throw new UnsupportedMediaTypeException(fileExtension.toUpperCase() + " is not supported.");
        }
        return mime.get().getMimeType();
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
