package com.rboker.automation.support;

/**
 * Centraliza leitura de propriedades de execução.
 */
public final class TestConfig {

    private TestConfig() {
        // Utility class
    }

    /**
     * Base URL do sistema testado.
     * Ex.: -DbaseUrl=https://www.wikipedia.org
     */
    public static String baseUrl() {
        return System.getProperty("baseUrl", "https://www.wikipedia.org");
    }
}
