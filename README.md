# Multi-threaded HTTP Server Documentation

## Overview

This project implements a minimal multi-threaded HTTP server capable of handling **GET** requests based on the [RFC 9112](https://datatracker.ietf.org/doc/html/rfc9112) and serving files from a specified directory. The server can be configured through a properties file.

## Configuration

The server configuration options are defined in a properties file, which is expected at `./config/config.properties`. More specifically, the `config.properties` file should be placed inside a `config` directory in the working directory where the server is run. If the properties file is not provided or some configurations are missing, default values will be used.

### Configuration Options

- **port**: Port number on which the server listens. Default is `7290`.
- **backlog**: Maximum number of incoming connections that can be queued. Default is `50`.
- **baseDir**: Directory from which the files are served. Default is the current working directory.
- **corePoolSize**: Initial size of the thread pool. Default is half of the available CPU cores.
- **maximumPoolSize**: Maximum size of the thread pool. Default is the number of available CPU cores.
- **keepAliveTime**: Time (in milliseconds) that threads in the thread pool will remain idle before being terminated. Default is `3000`.

### Example: config.properties

```
port=8080
backlog=100
baseDir=../
corePoolSize=4
maximumPoolSize=8
keepAliveTime=5000
```

<br/>

## Request Headers

The server accepts several request headers that influence its behavior. Below is a list of supported headers and their effects on the server's responses.

### Supported Request Headers

1. **Content-Length**
   - Description: Indicates the size of the request body in bytes.
   - Effect: Required for requests with a body, otherwise the server may not parse the request correctly.

2. **Accept-Encoding**
   - Description: Indicates the content encodings the client supports.
   - Effect: If the client includes `gzip` in the `Accept-Encoding` header, the server will compress the response body using GZIP. If this header is absent, the response body will be sent uncompressed.

3. **X-Content-Disposition**
   - Description: Controls the disposition of the content, whether it should be displayed inline or treated as an attachment.
   - Effect: If set to `attachment`, the server will include the `Content-Disposition` header with the value `attachment; filename="<requested-filename>"`, prompting the client to download the file. If this header is absent, the content will be displayed inline (i.e. directly in the browser tab or the terminal window).

<br/>

## Response Headers

The server sends several response headers to the client to convey information about the response. Below is a list of these headers, their purposes, and what the client can expect.

### Sent Response Headers

1. **Content-Type**
   - Description: Indicates the MIME type of the response content.
   - Effect: Specifies the media type of the requested resource. The client can use this information to determine how to process the content.

2. **Content-Disposition**
   - Description: Provides information on how the content should be displayed.
   - Effect: If set to `inline`, the content will be displayed directly in the browser tab or the terminal window. If set to `attachment`, the client will prompt the user to download the file instead.

3. **Content-Encoding**
   - Description: Specifies any encoding transformations applied to the content.
   - Effect: If set to `gzip`, it indicates that the content is compressed with GZIP. The client can use this information to decompress the content before processing it.

4. **Content-Length**
   - Description: Indicates the size of the response body in bytes.
   - Effect: Helps the client know the size of the response body, and ensuring the entire response has been received. 

5. **Connection**
   - Description: Controls whether the network connection stays open after the current request.
   - Effect: It is set to `close`, indicating that the server will close the connection after delivering the response.

<br/>

## Error Responses

   - **400 Bad Request**: Sent when the request is malformed.
   - **404 Not Found**: Sent when the requested resource does not exist.
   - **405 Method Not Allowed**: Sent when the HTTP method is not supported.
   - **500 Internal Server Error**: Sent when an unexpected error occurs on the server (i.e. the socket has been closed).

<br/>

## Usage

1. [Build from source](#build-from-source)
2. [Download the JAR from Releases](#download-the-jar-from-releases)
3. [Docker](#docker)

### 1) Build from source

#### Prerequisites

- [Java 17](https://www.oracle.com/java/technologies/downloads/#java17)
- [Maven](https://maven.apache.org/download.cgi)

#### Steps

- ##### Clone

  `git clone https://github.com/Papajohn77/HTTP-Server.git`

- ##### Change Directory

  `cd HTTP-Server/http-server`

- ##### Build

  `mvn clean package`

- ##### Execute

  `java -jar target/http-server.jar`

<br/>

### 2) Download the JAR from Releases

#### Prerequisites

- [Java 17](https://www.oracle.com/java/technologies/downloads/#java17)

#### Steps

- ##### Download the JAR from [Releases](./releases)
  
- ##### Execute (assuming the downloaded JAR is present in the current directory)
  
  `java -jar http-server.jar`

<br/>

### 3) Docker

#### Prerequisites

- [Docker Engine](https://docs.docker.com/get-docker)

#### Steps

- ##### Execute in Powershell
  
  `docker run -p <host-port>:<container-port> -v ${PWD}:/app/workdir papajohn77/http-server`

- ##### Execute in Linux
  
  `docker run -p <host-port>:<container-port> -v $(pwd):/app/workdir papajohn77/http-server`

<br/>

## Examples

### Setup

   - Assume that there is an `index.html` file present in the specified **baseDir** with the following contents:
   ```html
   <!DOCTYPE html>
    <html>
    <head>
      <title>Test page</title>
    </head>
    <body>
      <h1>Hello from HTTP server</h1>
    </body>
    </html>
   ```

   - Assume that there is **no** `test.txt` file present in the specified **baseDir**.

### Example 1: Basic Request

   - This is a simple **GET** request to fetch the `index.html` file:
   `curl -v http://<ip-address>:<port>/index.html`

   Response:
   ```
   HTTP/1.1 200 OK
   Content-Type: text/html
   Content-Length: 126
   Content-Disposition: inline
   Connection: close

   <!DOCTYPE html>
   <html>
   <head>
     <title>Test page</title>
   </head>
   <body>
     <h1>Hello from HTTP server</h1>
   </body>
   </html>
   ```

<br/>

### Example 2: Request with GZIP encoding

   - This is a **GET** request to fetch the `index.html` file, specifying that the client can accept gzip-encoded content:
   `curl -H "Accept-Encoding: gzip" -v http://<ip-address>:<port>/index.html`

   Response:
   ```
   HTTP/1.1 200 OK
   Content-Type: text/html
   Content-Length: 117
   Content-Disposition: inline
   Content-Encoding: gzip
   Connection: close

   ... (compressed content) ...
   ```

<br/>

### Example 3: Request a non-existing file

   - This is a **GET** request to fetch a non-existing file `test.txt`:
   `curl -v http://<ip-address>:<port>/test.txt`

   Response:
   ```
   HTTP/1.1 404 Not Found
   Content-Type: text/plain
   Content-Length: 24
   Connection: close

   File test.txt not found.
   ```

<br/>

## Author

- Ioannis Papadatos
