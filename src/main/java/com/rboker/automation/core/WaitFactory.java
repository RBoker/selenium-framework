package com.rboker.automation.core;

import com.rboker.automation.config.FrameworkConfig;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

/**
 * Factory para criação de waits usando timeout padrão do framework.
 */
public final class WaitFactory {

    private WaitFactory() {
        // Evita instanciação
    }

    /**
     * Wait padrão do framework.
     * - Timeout vindo do Config
     * - Polling configurado
     * - Ignora StaleElementReferenceException (comum em páginas dinâmicas)
     */
    public static FluentWait<WebDriver> defaultWait() {
        return new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(FrameworkConfig.timeoutSeconds()))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Alias por compatibilidade.
     */
    public static FluentWait<WebDriver> getWait() {
        return defaultWait();
    }
}
