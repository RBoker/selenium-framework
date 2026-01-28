package com.rboker.automation.unit;

import com.rboker.automation.config.FrameworkConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests para a classe FrameworkConfig.
 * Testa leitura de System Properties (-D) com e sem valores definidos.
 *
 * Importante: os testes limpam as properties ANTES e DEPOIS para garantir isolamento,
 * já que em algumas execuções (IDE/CI) pode existir -DbaseUrl previamente definido.
 */
class ConfigUnitTest {


    @BeforeEach
    void clearPropertiesBefore() {
        clearAll();
        System.setProperty("yaml.enabled", "false");
    }

    @AfterEach
    void clearPropertiesAfter() {
        clearAll();
    }

    private void clearAll() {
        System.clearProperty("baseUrl");
        System.clearProperty("browser");
        System.clearProperty("headless");
        System.clearProperty("remote");
        System.clearProperty("remoteUrl");
        System.clearProperty("timeout");
        System.clearProperty("yaml.enabled");

    }

    @Test
    void shouldReturnDefaultBaseUrlWhenNotProvided() {
        assertEquals("https://example.com", FrameworkConfig.baseUrl());
    }

    @Test
    void shouldReturnCustomBaseUrlWhenProvided() {
        System.setProperty("baseUrl", "https://www.wikipedia.org");
        assertEquals("https://www.wikipedia.org", FrameworkConfig.baseUrl());
    }

    @Test
    void shouldReturnDefaultBrowserWhenNotProvided() {
        assertEquals("chrome", FrameworkConfig.browser());
    }

    @Test
    void shouldReturnCustomBrowserWhenProvided() {
        System.setProperty("browser", "firefox");
        assertEquals("firefox", FrameworkConfig.browser());
    }

    @Test
    void shouldReturnFalseForHeadlessByDefault() {
        assertFalse(FrameworkConfig.headless());
    }

    @Test
    void shouldReturnTrueForHeadlessWhenProvided() {
        System.setProperty("headless", "true");
        assertTrue(FrameworkConfig.headless());
    }

    @Test
    void shouldReturnDefaultTimeoutWhenNotProvided() {
        assertEquals(10, FrameworkConfig.timeoutSeconds());
    }

    @Test
    void shouldReturnCustomTimeoutWhenProvided() {
        System.setProperty("timeout", "25");
        assertEquals(25, FrameworkConfig.timeoutSeconds());
    }

    @Test
    void shouldReturnDefaultRemoteUrlWhenNotProvided() {
        assertEquals("http://localhost:4444/wd/hub", FrameworkConfig.remoteUrl());
    }

    @Test
    void shouldReturnCustomRemoteUrlWhenProvided() {
        System.setProperty("remoteUrl", "http://grid:4444/wd/hub");
        assertEquals("http://grid:4444/wd/hub", FrameworkConfig.remoteUrl());
    }

    @Test
    void shouldFallbackToDefaultTimeoutWhenInvalidValueIsProvided() {
        System.setProperty("timeout", "abc");
        assertEquals(10, FrameworkConfig.timeoutSeconds());
    }
}
