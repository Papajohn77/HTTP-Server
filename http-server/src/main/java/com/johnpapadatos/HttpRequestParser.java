package com.johnpapadatos;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

public class HttpRequestParser {
    private static final int END_OF_STREAM_INDICATOR = -1;
    private static final int CARRIAGE_RETURN_ASCII_CODE = 13;
    private static final int NEW_LINE_ASCII_CODE = 10;
    private static final int SPACE_ASCII_CODE = 32;

    private HttpRequestParser() {
    }

    public static HttpRequest parseRequest(InputStream in) throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        parseRequestLine(in, httpRequest);
        parseHeaders(in, httpRequest);
        parseRequestBody(in, httpRequest);
        return httpRequest;
    }

    private static void parseRequestLine(
            InputStream is, HttpRequest httpRequest) throws IOException {
        int bufferIdx = 0;
        byte[] buffer = new byte[256];

        int numOfSpacesEncountered = 0;
        boolean requestLineProcessed = false;
        while (!requestLineProcessed) {
            int currByte = is.read();
            if (currByte != SPACE_ASCII_CODE && currByte != CARRIAGE_RETURN_ASCII_CODE) {
                buffer[bufferIdx] = (byte) currByte;
                bufferIdx++;
                continue;
            }

            if (numOfSpacesEncountered == 0) {
                httpRequest.setMethod(new String(buffer).substring(0, bufferIdx));
                numOfSpacesEncountered++;
                buffer = new byte[256];
                bufferIdx = 0;
            } else if (numOfSpacesEncountered == 1) {
                httpRequest.setPath(new String(buffer).substring(0, bufferIdx));
                numOfSpacesEncountered++;
                buffer = new byte[256];
                bufferIdx = 0;
            } else if (numOfSpacesEncountered == 2) {
                httpRequest.setVersion(new String(buffer).substring(0, bufferIdx));
                numOfSpacesEncountered++;
                buffer = new byte[256];
                bufferIdx = 0;
                requestLineProcessed = true;
            }
        }

        is.skipNBytes(1); // Skip new line feed
    }

    private static void parseHeaders(
            InputStream is, HttpRequest httpRequest) throws IOException {
        var headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        String key = null;
        String value = null;

        int bufferIdx = 0;
        byte[] buffer = new byte[256];

        int counter = 0;
        int currByte;
        while ((currByte = is.read()) != END_OF_STREAM_INDICATOR) { // Never EOS
            if (currByte == CARRIAGE_RETURN_ASCII_CODE || currByte == NEW_LINE_ASCII_CODE) {
                counter++;
                if (counter == 4) {
                    break;
                }
            } else {
                counter = 0;
            }

            if (currByte != SPACE_ASCII_CODE
                    && currByte != CARRIAGE_RETURN_ASCII_CODE
                    && currByte != NEW_LINE_ASCII_CODE) {
                buffer[bufferIdx] = (byte) currByte;
                bufferIdx++;
                continue;
            }

            if (currByte == SPACE_ASCII_CODE) {
                // Exclude the colon byte
                key = new String(buffer).substring(0, bufferIdx - 1);
                buffer = new byte[256];
                bufferIdx = 0;
            } else if (counter <= 1 && currByte == CARRIAGE_RETURN_ASCII_CODE) {
                value = new String(buffer).substring(0, bufferIdx);
                buffer = new byte[256];
                bufferIdx = 0;

                if (key == null || value == null) {
                    throw new IllegalStateException("Invalid HTTP headers");
                }

                headers.put(key, value);
                key = null;
                value = null;
            }
        }

        httpRequest.setHeaders(headers);
    }

    private static void parseRequestBody(
            InputStream is, HttpRequest httpRequest) throws IOException {
        if (!httpRequest.getHeaders().containsKey("Content-Length")) {
            httpRequest.setBody("");
            return;
        }

        int contentLength = Integer.parseInt(httpRequest.getHeaders().get("Content-Length"));
        byte[] bytes = new byte[contentLength];
        is.read(bytes, 0, bytes.length);
        httpRequest.setBody(new String(bytes));
    }
}
