package com.rboker.automation.config;

/**
 * Centraliza a leitura de configurações via System Properties (-D).
 * Assim, você consegue mudar comportamento do framework sem recompilar.
 *
 * Ex.: mvn test -DbaseUrl=https://site -Dbrowser=chrome -Dheadless=true
 */
public final class Config {

    private Config() {
        // Evita instanciação (classe utilitária)
    }

    public static String baseUrl() {
        return System.getProperty("baseUrl", "https://example.com");
    }

    public static String browser() {
        return System.getProperty("browser", "chrome"); // chrome|firefox|edge
    }

    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("headless", "false"));
    }

    public static boolean remote() {
        return Boolean.parseBoolean(System.getProperty("remote", "false"));
    }

    public static String remoteUrl() {
        return System.getProperty("remoteUrl", "http://localhost:4444/wd/hub");
    }

    public static int timeoutSeconds() {
        try {
            return Integer.parseInt(System.getProperty("timeout", "10"));
        } catch (NumberFormatException e) {
            return 10;
        }
    }

}
