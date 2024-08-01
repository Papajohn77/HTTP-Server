package com.johnpapadatos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

import com.johnpapadatos.exceptions.MethodNotSupportedException;

class HttpRequestParserTest {
    @Test
    void testParseRequest_simpleHttpRequest() throws IOException, MethodNotSupportedException {
        String httpRequest = "GET / HTTP/1.1\r\n\r\n";
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpRequest.getBytes())));
        HttpRequest expectedHttpRequest = getExpectedHttpRequest_simpleHttpRequest();
        HttpRequest actualHttpRequest = HttpRequestParser.parseRequest(br);
        assertEquals(expectedHttpRequest, actualHttpRequest);
    }

    @Test
    void testParseRequest_simpleHttpRequest_withHeaders() throws IOException, MethodNotSupportedException {
        String httpRequest = "GET / HTTP/1.1\r\n"
                + "Host: localhost:4221\r\n"
                + "User-Agent: curl/7.64.1\r\n"
                + "Accept: */*\r\n"
                + "\r\n";
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpRequest.getBytes())));
        HttpRequest expectedHttpRequest = getExpectedHttpRequest_simpleHttpRequest_withHeaders();
        HttpRequest actualHttpRequest = HttpRequestParser.parseRequest(br);
        assertEquals(expectedHttpRequest, actualHttpRequest);
    }

    @Test
    void testParseRequest_simpleHttpRequest_withHeadersAndBody() throws IOException, MethodNotSupportedException {
        String httpRequest = "GET / HTTP/1.1\r\n"
                + "Host: localhost:4221\r\n"
                + "User-Agent: curl/7.64.1\r\n"
                + "Accept: */*\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: 8\r\n"
                + "\r\n"
                + "{\"id\":1}";
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpRequest.getBytes())));
        HttpRequest expectedHttpRequest = getExpectedHttpRequest_simpleHttpRequest_withHeadersAndBody();
        HttpRequest actualHttpRequest = HttpRequestParser.parseRequest(br);
        assertEquals(expectedHttpRequest, actualHttpRequest);
    }

    /*
     * When the Content-Length header is missing, it is assumed that there is no
     * request body.
     */
    @Test
    void testParseRequest_simpleHttpRequest_withBodyAndNoContentLength() throws IOException, MethodNotSupportedException {
        String httpRequest = "GET / HTTP/1.1\r\n"
                + "Host: localhost:4221\r\n"
                + "User-Agent: curl/7.64.1\r\n"
                + "Accept: */*\r\n"
                + "\r\n"
                + "{\"id\":1}";
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpRequest.getBytes())));
        HttpRequest expectedHttpRequest = getExpectedHttpRequest_simpleHttpRequest_withBodyAndNoContentLength();
        HttpRequest actualHttpRequest = HttpRequestParser.parseRequest(br);
        assertEquals(expectedHttpRequest, actualHttpRequest);
    }

    @Test
    void testParseRequest_simpleHttpRequest_invalidRequestLine() {
        String httpRequestInvalidMethod = "POST / HTTP/1.1\r\n\r\n";
        BufferedReader brInvalidMethod = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(httpRequestInvalidMethod.getBytes())));
        assertThrows(MethodNotSupportedException.class, () -> HttpRequestParser.parseRequest(brInvalidMethod));

        String httpRequestInvalidPath = "GET path HTTP/1.1\r\n\r\n";
        BufferedReader brInvalidPath = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(httpRequestInvalidPath.getBytes())));
        assertThrows(IllegalArgumentException.class, () -> HttpRequestParser.parseRequest(brInvalidPath));

        String httpRequestInvalidVersion = "GET path HTTP/2\r\n\r\n";
        BufferedReader brInvalidVersion = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(httpRequestInvalidVersion.getBytes())));
        assertThrows(IllegalArgumentException.class, () -> HttpRequestParser.parseRequest(brInvalidVersion));
    }

    @Test
    void testParseRequest_simpleHttpRequest_invalidHeaders() {
        String httpRequestInvalidHeader1 = "GET / HTTP/1.1\r\n"
                + "Host:localhost:4221\r\n" // No space after colon
                + "User-Agent: curl/7.64.1\r\n"
                + "Accept: */*\r\n"
                + "\r\n";
        BufferedReader brInvalidHeader1 = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(httpRequestInvalidHeader1.getBytes())));
        assertThrows(IllegalArgumentException.class, () -> HttpRequestParser.parseRequest(brInvalidHeader1));

        String httpRequestInvalidHeader2 = "GET / HTTP/1.1\r\n"
                + "Header\r\n" // Invalid header
                + "User-Agent: curl/7.64.1\r\n"
                + "Accept: */*\r\n"
                + "\r\n";
        BufferedReader brInvalidHeader2 = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(httpRequestInvalidHeader2.getBytes())));
        assertThrows(IllegalArgumentException.class, () -> HttpRequestParser.parseRequest(brInvalidHeader2));

        String httpRequestInvalidHeader3 = "GET / HTTP/1.1\r\n"
                + "Host:localhost:4221\r\n"
                + "User-Agent: curl/7.64.1" // Missing CRLF
                + "Accept: */*\r\n"
                + "\r\n";
        BufferedReader brInvalidHeader3 = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(httpRequestInvalidHeader3.getBytes())));
        assertThrows(IllegalArgumentException.class, () -> HttpRequestParser.parseRequest(brInvalidHeader3));
    }

    private HttpRequest getExpectedHttpRequest_simpleHttpRequest() {
        HttpRequest expectedHttpRequest = new HttpRequest();
        expectedHttpRequest.setMethod("GET");
        expectedHttpRequest.setPath("/");
        expectedHttpRequest.setVersion("HTTP/1.1");
        expectedHttpRequest.setBody("");
        return expectedHttpRequest;
    }

    private HttpRequest getExpectedHttpRequest_simpleHttpRequest_withHeaders() {
        HttpRequest expectedHttpRequest = new HttpRequest();
        expectedHttpRequest.setMethod("GET");
        expectedHttpRequest.setPath("/");
        expectedHttpRequest.setVersion("HTTP/1.1");
        expectedHttpRequest.setHeader("Host", "localhost:4221");
        expectedHttpRequest.setHeader("User-Agent", "curl/7.64.1");
        expectedHttpRequest.setHeader("Accept", "*/*");
        expectedHttpRequest.setBody("");
        return expectedHttpRequest;
    }

    private HttpRequest getExpectedHttpRequest_simpleHttpRequest_withHeadersAndBody() {
        HttpRequest expectedHttpRequest = new HttpRequest();
        expectedHttpRequest.setMethod("GET");
        expectedHttpRequest.setPath("/");
        expectedHttpRequest.setVersion("HTTP/1.1");
        expectedHttpRequest.setHeader("Host", "localhost:4221");
        expectedHttpRequest.setHeader("User-Agent", "curl/7.64.1");
        expectedHttpRequest.setHeader("Accept", "*/*");
        expectedHttpRequest.setHeader("Content-Type", "application/json");
        expectedHttpRequest.setHeader("Content-Length", "8");
        expectedHttpRequest.setBody("{\"id\":1}");
        return expectedHttpRequest;
    }

    private HttpRequest getExpectedHttpRequest_simpleHttpRequest_withBodyAndNoContentLength() {
        HttpRequest expectedHttpRequest = new HttpRequest();
        expectedHttpRequest.setMethod("GET");
        expectedHttpRequest.setPath("/");
        expectedHttpRequest.setVersion("HTTP/1.1");
        expectedHttpRequest.setHeader("Host", "localhost:4221");
        expectedHttpRequest.setHeader("User-Agent", "curl/7.64.1");
        expectedHttpRequest.setHeader("Accept", "*/*");
        expectedHttpRequest.setBody("");
        return expectedHttpRequest;
    }
}
