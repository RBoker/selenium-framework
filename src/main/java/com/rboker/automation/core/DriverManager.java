package com.rboker.automation.core;

import org.openqa.selenium.WebDriver;

/**
 * DriverManager centraliza o WebDriver por thread (ThreadLocal) para suportar paralelismo com segurança.
 * Responsável por armazenar, recuperar e finalizar o driver garantindo limpeza de recursos.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
        // Utility class
    }

    /**
     * Define o WebDriver da thread atual.
     *
     * @param driver WebDriver criado pela DriverFactory
     */
    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    /**
     * Obtém o WebDriver da thread atual.
     *
     * @return WebDriver ou null se não foi inicializado
     */
    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    /**
     * Finaliza o WebDriver da thread atual com segurança e remove do ThreadLocal.
     * Evita "driver zumbi" e vazamento de memória em execuções longas/CI.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }
}
