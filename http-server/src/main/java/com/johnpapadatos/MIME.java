package com.johnpapadatos;

public enum MIME {
    JAR("jar", "application/java-archive"),
    APK("apk", "application/vnd.android.package-archive"),
    ZIP("zip", "application/zip"),
    PDF("pdf", "application/pdf"),
    DOC("doc", "application/msword"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    EXCEL("xlsx", "application/vnd.ms-excel"),
    POWERPOINT("pptx", "application/vnd.ms-powerpoint"),
    JSON("json", "application/json"),
    XML("xml", "application/xml"),
    TXT("txt", "text/plain"),
    CSV("csv", "text/csv"),
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JAVASCRIPT("js", "text/javascript"),
    JPEG("jpg", "image/jpeg"),
    PNG("png", "image/png"),
    WEBP("webp", "image/webp"),
    GIF("gif", "image/gif"),
    SVG("svg", "image/svg+xml"),
    MP4("mp4", "video/mp4");

    private final String extension;
    private final String mimeType;

    MIME(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }
}
