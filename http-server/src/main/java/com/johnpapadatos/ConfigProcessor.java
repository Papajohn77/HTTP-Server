package com.johnpapadatos;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ConfigProcessor {
    private static final int DEFAULT_PORT = 7290;
    private static final String DEFAULT_BASE_DIRECTORY = System.getProperty("user.dir");
    private static final int DEFAULT_CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() / 2;
    private static final int DEFAULT_MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_KEEP_ALIVE = 3000; // Milliseconds
    private static final int REGISTER_PORT_RANGE_LOWER_BOUND = 1024;
    private static final int REGISTER_PORT_RANGE_UPPER_BOUND = 49151;

    private ConfigProcessor() {
    }

    public static Config processConfigFile(String configPropertiesFilename) {
        try (InputStream in = ConfigProcessor.class.getClassLoader().getResourceAsStream(configPropertiesFilename)) {
            if (in == null) {
                return createDefaultConfig();
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                Map<String, String> configOptions = new HashMap<>();

                String line;
                while ((line = br.readLine()) != null) {
                    String[] configOptionParts = line.strip().split("=");
                    if (configOptionParts.length != 2) {
                        continue;
                    }
                    configOptions.put(configOptionParts[0], configOptionParts[1]);
                }

                return createConfig(configOptions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createDefaultConfig();
    }

    private static Config createDefaultConfig() {
        Config config = new Config();
        config.setPort(DEFAULT_PORT);
        config.setBaseDir(DEFAULT_BASE_DIRECTORY);
        config.setCorePoolSize(DEFAULT_CORE_POOL_SIZE);
        config.setMaximumPoolSize(DEFAULT_MAX_POOL_SIZE);
        config.setKeepAliveTime(DEFAULT_KEEP_ALIVE);
        return config;
    }

    private static Config createConfig(Map<String, String> configOptions) {
        Config config = new Config();

        int port = isValidPort(configOptions.get("port"))
                ? Integer.parseInt(configOptions.get("port"))
                : DEFAULT_PORT;
        config.setPort(port);

        String baseDir = isValidBaseDir(configOptions.get("baseDir"))
                ? configOptions.get("baseDir")
                : DEFAULT_BASE_DIRECTORY;
        config.setBaseDir(baseDir);

        int corePoolSize = isValidCorePoolSize(configOptions.get("corePoolSize"))
                ? Integer.parseInt(configOptions.get("corePoolSize"))
                : DEFAULT_CORE_POOL_SIZE;
        config.setCorePoolSize(corePoolSize);

        int maximumPoolSize = isValidMaximumPoolSize(configOptions.get("maximumPoolSize"), corePoolSize)
                ? Integer.parseInt(configOptions.get("maximumPoolSize"))
                : DEFAULT_MAX_POOL_SIZE;
        config.setMaximumPoolSize(maximumPoolSize);

        int keepAliveTime = isValidKeepAliveTime(configOptions.get("keepAliveTime"))
                ? Integer.parseInt(configOptions.get("keepAliveTime"))
                : DEFAULT_KEEP_ALIVE;
        config.setKeepAliveTime(keepAliveTime);

        return config;
    }

    private static boolean isValidPort(String port) {
        if (port == null) {
            return false;
        }

        if (!port.matches("\\d+")) {
            return false;
        }

        return Integer.parseInt(port) >= REGISTER_PORT_RANGE_LOWER_BOUND
                && Integer.parseInt(port) <= REGISTER_PORT_RANGE_UPPER_BOUND;
    }

    private static boolean isValidBaseDir(String baseDirPath) {
        if (baseDirPath == null) {
            return false;
        }

        File baseDir = new File(baseDirPath);
        return baseDir.exists() && baseDir.isDirectory();
    }

    private static boolean isValidCorePoolSize(String poolSize) {
        if (poolSize == null) {
            return false;
        }

        if (!poolSize.matches("\\d+")) {
            return false;
        }

        return Integer.parseInt(poolSize) >= 0;
    }

    private static boolean isValidMaximumPoolSize(String poolSize, int corePoolSize) {
        if (poolSize == null) {
            return false;
        }

        if (!poolSize.matches("\\d+")) {
            return false;
        }

        return Integer.parseInt(poolSize) > 0 && Integer.parseInt(poolSize) >= corePoolSize;
    }

    private static boolean isValidKeepAliveTime(String keepAliveTime) {
        if (keepAliveTime == null) {
            return false;
        }

        return keepAliveTime.matches("\\d+");
    }
}