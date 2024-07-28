package com.johnpapadatos;

public class Config {
    private int port;
    private String baseDir;
    private int corePoolSize;
    private int maximumPoolSize;
    private int keepAliveTime; // Milliseconds

    public Config() {
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + port;
        result = prime * result + ((baseDir == null) ? 0 : baseDir.hashCode());
        result = prime * result + corePoolSize;
        result = prime * result + maximumPoolSize;
        result = prime * result + keepAliveTime;
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
        Config other = (Config) obj;
        if (port != other.port)
            return false;
        if (baseDir == null) {
            if (other.baseDir != null)
                return false;
        } else if (!baseDir.equals(other.baseDir))
            return false;
        if (corePoolSize != other.corePoolSize)
            return false;
        if (maximumPoolSize != other.maximumPoolSize)
            return false;
        if (keepAliveTime != other.keepAliveTime)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Config [port=" + port
                + ", baseDir=" + baseDir
                + ", corePoolSize=" + corePoolSize
                + ", maximumPoolSize=" + maximumPoolSize
                + ", keepAliveTime=" + keepAliveTime
                + "]";
    }
}
