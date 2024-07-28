package com.johnpapadatos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ConfigProcessorTest {
    private static final int DEFAULT_PORT = 7290;
    private static final String DEFAULT_BASE_DIRECTORY = System.getProperty("user.dir");
    private static final int DEFAULT_CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() / 2;
    private static final int DEFAULT_MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_KEEP_ALIVE = 3000; // Milliseconds

    @Test
    void testProcessConfigFile_resourceFileMissing() {
        String configPropertiesFilename = "";
        Config actualConfig = ConfigProcessor.processConfigFile(configPropertiesFilename);
        Config expectedConfig = getExpectedConfig_defaultConfig();
        assertEquals(expectedConfig, actualConfig);
    }

    @Test
    void testProcessConfigFile_allPropertiesValid() {
        String configPropertiesFilename = "src/test/resources/config.properties";
        Config actualConfig = ConfigProcessor.processConfigFile(configPropertiesFilename);
        Config expectedConfig = getExpectedConfig_allPropertiesValid();
        assertEquals(expectedConfig, actualConfig);
    }

    @Test
    void testProcessConfigFile_portValid_maximumPoolSizeInvalid_othersMissing() {
        String configPropertiesFilename = "src/test/resources/config_2.properties";
        Config actualConfig = ConfigProcessor.processConfigFile(configPropertiesFilename);
        Config expectedConfig = getExpectedConfig_portValid_maximumPoolSizeInvalid_othersMissing();
        assertEquals(expectedConfig, actualConfig);
    }

    @Test
    void testProcessConfigFile_corePoolSizeValid_maximumPoolSizeValid_extraOptions() {
        String configPropertiesFilename = "src/test/resources/config_3.properties";
        Config actualConfig = ConfigProcessor.processConfigFile(configPropertiesFilename);
        Config expectedConfig = getExpectedConfig_corePoolSizeValid_maximumPoolSizeValid_extraOptions();
        assertEquals(expectedConfig, actualConfig);
    }

    @Test
    void testProcessConfigFile_allPropertiesValid_trailingSpaces_newLinesBetween() {
        String configPropertiesFilename = "src/test/resources/config_4.properties";
        Config actualConfig = ConfigProcessor.processConfigFile(configPropertiesFilename);
        Config expectedConfig = getExpectedConfig_allPropertiesValid_trailingSpaces_newLinesBetween();
        assertEquals(expectedConfig, actualConfig);
    }

    @Test
    void testProcessConfigFile_allPropertiesInvalid() {
        String configPropertiesFilename = "src/test/resources/config_5.properties";
        Config actualConfig = ConfigProcessor.processConfigFile(configPropertiesFilename);
        Config expectedConfig = getExpectedConfig_allPropertiesInvalid();
        assertEquals(expectedConfig, actualConfig);
    }

    private Config getExpectedConfig_defaultConfig() {
        Config config = new Config();
        config.setPort(DEFAULT_PORT);
        config.setBaseDir(DEFAULT_BASE_DIRECTORY);
        config.setCorePoolSize(DEFAULT_CORE_POOL_SIZE);
        config.setMaximumPoolSize(DEFAULT_MAX_POOL_SIZE);
        config.setKeepAliveTime(DEFAULT_KEEP_ALIVE);
        return config;
    }

    private Config getExpectedConfig_allPropertiesValid() {
        Config config = new Config();
        config.setPort(8080);
        config.setBaseDir("../");
        config.setCorePoolSize(4);
        config.setMaximumPoolSize(8);
        config.setKeepAliveTime(1250);
        return config;
    }

    private Config getExpectedConfig_portValid_maximumPoolSizeInvalid_othersMissing() {
        Config config = new Config();
        config.setPort(8080);
        config.setBaseDir(DEFAULT_BASE_DIRECTORY);
        config.setCorePoolSize(DEFAULT_CORE_POOL_SIZE);
        config.setMaximumPoolSize(DEFAULT_MAX_POOL_SIZE);
        config.setKeepAliveTime(DEFAULT_KEEP_ALIVE);
        return config;
    }

    private Config getExpectedConfig_corePoolSizeValid_maximumPoolSizeValid_extraOptions() {
        Config config = new Config();
        config.setPort(DEFAULT_PORT);
        config.setBaseDir(DEFAULT_BASE_DIRECTORY);
        config.setCorePoolSize(4);
        config.setMaximumPoolSize(8);
        config.setKeepAliveTime(DEFAULT_KEEP_ALIVE);
        return config;
    }

    private Config getExpectedConfig_allPropertiesValid_trailingSpaces_newLinesBetween() {
        Config config = new Config();
        config.setPort(8080);
        config.setBaseDir("../");
        config.setCorePoolSize(4);
        config.setMaximumPoolSize(8);
        config.setKeepAliveTime(1250);
        return config;
    }

    private Config getExpectedConfig_allPropertiesInvalid() {
        Config config = new Config();
        config.setPort(DEFAULT_PORT);
        config.setBaseDir(DEFAULT_BASE_DIRECTORY);
        config.setCorePoolSize(DEFAULT_CORE_POOL_SIZE);
        config.setMaximumPoolSize(DEFAULT_MAX_POOL_SIZE);
        config.setKeepAliveTime(DEFAULT_KEEP_ALIVE);
        return config;
    }
}
