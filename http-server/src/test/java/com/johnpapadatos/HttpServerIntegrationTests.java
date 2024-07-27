package com.johnpapadatos;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@TestInstance(Lifecycle.PER_CLASS)
class HttpServerIntegrationTests {
    private static final int TEST_PORT = 7290;
    private static final String TEST_BASE_DIRECTORY = "src/test/resources";

    private HttpServer httpServer;
    private ServerSocket serverSocket;
    private Thread serverThread;

    @BeforeAll
    public void setUp() throws IOException {
        serverSocket = new ServerSocket(TEST_PORT);
        httpServer = new HttpServer(serverSocket, Executors.newSingleThreadExecutor());
        serverThread = new Thread(() -> {
            try {
                httpServer.start(TEST_BASE_DIRECTORY);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        RestAssured.baseURI = "http://localhost:" + TEST_PORT;
    }

    @Test
    void testSuccessfulResponseTXT() throws IOException {
        String requestedResource = "/test.txt";
        String expectedResponseBody = new String(
                Files.readAllBytes(Paths.get(TEST_BASE_DIRECTORY + requestedResource)));
        String expectedContentLength = Integer.toString(expectedResponseBody.getBytes().length);

        Response successfulResponse = when()
                .get(requestedResource)
                .then()
                .extract().response();

        assertEquals(200, successfulResponse.statusCode());
        assertEquals("text/plain", successfulResponse.getHeader("Content-Type"));
        assertEquals("inline", successfulResponse.getHeader("Content-Disposition"));
        assertEquals(expectedContentLength, successfulResponse.getHeader("Content-Length"));
        assertEquals("close", successfulResponse.getHeader("Connection"));
        assertEquals(expectedResponseBody, successfulResponse.getBody().asString());
    }

    @Test
    void testSuccessfulResponseTXT_contentDispositionAttachment() throws IOException {
        String requestedResource = "/test.txt";
        String expectedResponseBody = new String(
                Files.readAllBytes(Paths.get(TEST_BASE_DIRECTORY + requestedResource)));
        String expectedContentLength = Integer.toString(expectedResponseBody.getBytes().length);

        Response successfulResponse = given()
                .header("X-Content-Disposition", "attachment")
                .when()
                .get(requestedResource)
                .then()
                .extract().response();

        assertEquals(200, successfulResponse.statusCode());
        assertEquals("text/plain", successfulResponse.getHeader("Content-Type"));
        assertTrue(successfulResponse.getHeader("Content-Disposition").contains("attachment"));
        assertEquals(expectedContentLength, successfulResponse.getHeader("Content-Length"));
        assertEquals("close", successfulResponse.getHeader("Connection"));
        assertEquals(expectedResponseBody, successfulResponse.getBody().asString());
    }

    @Test
    void testError400Response_unsupportedMethod() {
        String requestedResource = "/non-existing.txt";
        String expectedResponseBody = "POST method not supported.";
        String expectedContentLength = Integer.toString(expectedResponseBody.getBytes().length);

        Response error400Response = when()
                .post(requestedResource)
                .then()
                .extract().response();

        assertEquals(400, error400Response.statusCode());
        assertEquals("text/plain", error400Response.getHeader("Content-Type"));
        assertEquals(expectedContentLength, error400Response.getHeader("Content-Length"));
        assertEquals("close", error400Response.getHeader("Connection"));
        assertEquals(expectedResponseBody, error400Response.getBody().asString());
    }

    @Test
    void testError404Response() {
        String requestedResource = "/non-existing.txt";
        String expectedResponseBody = "File non-existing.txt not found.";
        String expectedContentLength = Integer.toString(expectedResponseBody.getBytes().length);

        Response successfulResponse = when()
                .get(requestedResource)
                .then()
                .extract().response();

        assertEquals(404, successfulResponse.statusCode());
        assertEquals("text/plain", successfulResponse.getHeader("Content-Type"));
        assertEquals(expectedContentLength, successfulResponse.getHeader("Content-Length"));
        assertEquals("close", successfulResponse.getHeader("Connection"));
        assertEquals(expectedResponseBody, successfulResponse.getBody().asString());
    }
}
