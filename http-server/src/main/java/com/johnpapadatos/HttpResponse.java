package com.johnpapadatos;

import java.util.Map;
import java.util.TreeMap;

public class HttpResponse {
    private static final String CRLF = "\r\n";

    private String version;
    private String statusCode;
    private String reasonPhrase;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse() {
        headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public Map<String, String> getHeaders() {
        return new TreeMap<>(headers);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result + ((statusCode == null) ? 0 : statusCode.hashCode());
        result = prime * result + ((reasonPhrase == null) ? 0 : reasonPhrase.hashCode());
        result = prime * result + ((headers == null) ? 0 : headers.hashCode());
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HttpResponse other = (HttpResponse) obj;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        if (statusCode == null) {
            if (other.statusCode != null)
                return false;
        } else if (!statusCode.equals(other.statusCode))
            return false;
        if (reasonPhrase == null) {
            if (other.reasonPhrase != null)
                return false;
        } else if (!reasonPhrase.equals(other.reasonPhrase))
            return false;
        if (headers == null) {
            if (other.headers != null)
                return false;
        } else if (!headers.equals(other.headers))
            return false;
        if (body == null) {
            if (other.body != null)
                return false;
        } else if (!body.equals(other.body))
            return false;
        return true;
    }

    public byte[] asBytes() {
        StringBuilder response = new StringBuilder();
        response.append(version).append(" ").append(statusCode).append(" ").append(reasonPhrase).append(CRLF);
        response.append(
                String.join(CRLF, headers.entrySet().stream().map(h -> h.getKey() + ": " + h.getValue()).toList()));
        response.append(CRLF).append(CRLF);
        response.append(body);
        return response.toString().getBytes();
    }

    @Override
    public String toString() {
        return "HttpResponse [version=" + version
                + ", statusCode=" + statusCode
                + ", reasonPhrase=" + reasonPhrase
                + ", headers=" + headers
                + ", body=" + body
                + "]";
    }
}
