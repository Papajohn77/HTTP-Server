package com.johnpapadatos;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.config.DecoderConfig.decoderConfig;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@TestInstance(Lifecycle.PER_CLASS)
class HttpServerIntegrationTests {
    private static final String TEST_BASE_DIRECTORY = "src/test/resources/media";

    private HttpServer httpServer;
    private ServerSocket serverSocket;
    private Thread serverThread;

    @BeforeAll
    public void setUp() throws IOException {
        serverSocket = new ServerSocket(0);
        httpServer = new HttpServer(serverSocket, Executors.newSingleThreadExecutor());
        serverThread = new Thread(() -> {
            try {
                httpServer.start(TEST_BASE_DIRECTORY);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        RestAssured.baseURI = "http://localhost:" + serverSocket.getLocalPort();
        RestAssured.config = RestAssured.config()
                .decoderConfig(decoderConfig().noContentDecoders());
    }

    @Test
    void testSuccessfulResponseTXT() throws IOException {
        String requestedResource = "/test.txt";
        byte[] expectedResponseBody = Files.readAllBytes(Paths.get(TEST_BASE_DIRECTORY + requestedResource));
        String expectedContentLength = Integer.toString(expectedResponseBody.length);

        Response successfulResponse = when()
                .get(requestedResource)
                .then()
                .extract().response();

        assertEquals(200, successfulResponse.statusCode());
        assertEquals("text/plain", successfulResponse.getHeader("Content-Type"));
        assertEquals("inline", successfulResponse.getHeader("Content-Disposition"));
        assertEquals(expectedContentLength, successfulResponse.getHeader("Content-Length"));
        assertEquals("close", successfulResponse.getHeader("Connection"));
        assertArrayEquals(expectedResponseBody, successfulResponse.getBody().asByteArray());
    }

    @Test
    void testSuccessfulResponseHTML() throws IOException {
        String requestedResource = "/test.html";
        byte[] expectedResponseBody = Files.readAllBytes(Paths.get(TEST_BASE_DIRECTORY + requestedResource));
        String expectedContentLength = Integer.toString(expectedResponseBody.length);

        Response successfulResponse = when()
                .get(requestedResource)
                .then()
                .extract().response();

        assertEquals(200, successfulResponse.statusCode());
        assertEquals("text/html", successfulResponse.getHeader("Content-Type"));
        assertEquals("inline", successfulResponse.getHeader("Content-Disposition"));
        assertEquals(expectedContentLength, successfulResponse.getHeader("Content-Length"));
        assertEquals("close", successfulResponse.getHeader("Connection"));
        assertArrayEquals(expectedResponseBody, successfulResponse.getBody().asByteArray());
    }

    @Test
    void testSuccessfulResponseTXT_contentDispositionAttachment() throws IOException {
        String requestedResource = "/test.txt";
        byte[] expectedResponseBody = Files.readAllBytes(Paths.get(TEST_BASE_DIRECTORY + requestedResource));
        String expectedContentLength = Integer.toString(expectedResponseBody.length);
        String expectedContentDisposition = "attachment; filename=\"" + requestedResource.substring(1) + "\"";

        Response successfulResponse = given()
                .header("X-Content-Disposition", "attachment")
                .when()
                .get(requestedResource)
                .then()
                .extract().response();

        assertEquals(200, successfulResponse.statusCode());
        assertEquals("text/plain", successfulResponse.getHeader("Content-Type"));
        assertEquals(expectedContentDisposition, successfulResponse.getHeader("Content-Disposition"));
        assertEquals(expectedContentLength, successfulResponse.getHeader("Content-Length"));
        assertEquals("close", successfulResponse.getHeader("Connection"));
        assertArrayEquals(expectedResponseBody, successfulResponse.getBody().asByteArray());
    }

    @Test
    void testSuccessfulResponseTXT_acceptEncodingGzip() throws IOException {
        String requestedResource = "/test.txt";
        byte[] expectedResponseBody = getBodyAsBytesGzipCompressed(
                Files.readAllBytes(Paths.get(TEST_BASE_DIRECTORY + requestedResource)));
        String expectedContentLength = Integer.toString(expectedResponseBody.length);

        Response successfulResponse = given()
                .header("Accept-Encoding", "gzip")
                .when()
                .get(requestedResource)
                .then()
                .extract().response();

        assertEquals(200, successfulResponse.statusCode());
        assertEquals("text/plain", successfulResponse.getHeader("Content-Type"));
        assertEquals("inline", successfulResponse.getHeader("Content-Disposition"));
        assertEquals(expectedContentLength, successfulResponse.getHeader("Content-Length"));
        assertEquals("close", successfulResponse.getHeader("Connection"));
        assertArrayEquals(expectedResponseBody, successfulResponse.getBody().asByteArray());
    }

    @Test
    void testSuccessfulResponseHTML_acceptEncodingGzip_contentDispositionAttachment() throws IOException {
        String requestedResource = "/test.html";
        byte[] expectedResponseBody = getBodyAsBytesGzipCompressed(
                Files.readAllBytes(Paths.get(TEST_BASE_DIRECTORY + requestedResource)));
        String expectedContentLength = Integer.toString(expectedResponseBody.length);
        String expectedContentDisposition = "attachment; filename=\"" + requestedResource.substring(1) + "\"";

        Response successfulResponse = given()
                .header("Accept-Encoding", "gzip")
                .header("X-Content-Disposition", "attachment")
                .when()
                .get(requestedResource)
                .then()
                .extract().response();

        assertEquals(200, successfulResponse.statusCode());
        assertEquals("text/html", successfulResponse.getHeader("Content-Type"));
        assertEquals(expectedContentDisposition, successfulResponse.getHeader("Content-Disposition"));
        assertEquals(expectedContentLength, successfulResponse.getHeader("Content-Length"));
        assertEquals("close", successfulResponse.getHeader("Connection"));
        assertArrayEquals(expectedResponseBody, successfulResponse.getBody().asByteArray());
    }

    @Test
    void testError400Response_unsupportedMethod_POST() {
        String requestedResource = "/test.txt";
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
    void testError400Response_unsupportedMethod_PUT() {
        String requestedResource = "/test.txt";
        String expectedResponseBody = "PUT method not supported.";
        String expectedContentLength = Integer.toString(expectedResponseBody.getBytes().length);

        Response error400Response = when()
                .put(requestedResource)
                .then()
                .extract().response();

        assertEquals(400, error400Response.statusCode());
        assertEquals("text/plain", error400Response.getHeader("Content-Type"));
        assertEquals(expectedContentLength, error400Response.getHeader("Content-Length"));
        assertEquals("close", error400Response.getHeader("Connection"));
        assertEquals(expectedResponseBody, error400Response.getBody().asString());
    }

    @Test
    void testError400Response_unsupportedMethod_DELETE() {
        String requestedResource = "/test.txt";
        String expectedResponseBody = "DELETE method not supported.";
        String expectedContentLength = Integer.toString(expectedResponseBody.getBytes().length);

        Response error400Response = when()
                .delete(requestedResource)
                .then()
                .extract().response();

        assertEquals(400, error400Response.statusCode());
        assertEquals("text/plain", error400Response.getHeader("Content-Type"));
        assertEquals(expectedContentLength, error400Response.getHeader("Content-Length"));
        assertEquals("close", error400Response.getHeader("Connection"));
        assertEquals(expectedResponseBody, error400Response.getBody().asString());
    }

    @Test
    void testError404Response_fileNotFound() {
        String requestedResource = "/non-existing-file.txt";
        String expectedResponseBody = "File " + requestedResource.substring(1) + " not found.";
        String expectedContentLength = Integer.toString(expectedResponseBody.getBytes().length);

        Response successfulResponse = when()
                .get(requestedResource)
                .then()
                .extract().response();

        System.out.println(successfulResponse.getBody().asString());
        System.out.println(successfulResponse.getHeader("Content-Length"));

        assertEquals(404, successfulResponse.statusCode());
        assertEquals("text/plain", successfulResponse.getHeader("Content-Type"));
        assertEquals(expectedContentLength, successfulResponse.getHeader("Content-Length"));
        assertEquals("close", successfulResponse.getHeader("Connection"));
        assertEquals(expectedResponseBody, successfulResponse.getBody().asString());
    }

    private static byte[] getBodyAsBytesGzipCompressed(byte[] body) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(os);
        gzipOutputStream.write(body, 0, body.length);
        gzipOutputStream.close();
        return os.toByteArray();
    }
}
