package com.rboker.automation.tests.unit;

import com.rboker.automation.config.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests para a classe Config.
 * Testa leitura de System Properties (-D) com e sem valores definidos.
 */
class ConfigUnitTest {

    @AfterEach
    void clearProperties() {
        System.clearProperty("baseUrl");
        System.clearProperty("browser");
        System.clearProperty("headless");
        System.clearProperty("remote");
        System.clearProperty("remoteUrl");
        System.clearProperty("timeout");
    }

    @Test
    void shouldReturnDefaultBaseUrlWhenNotProvided() {
        assertEquals("https://example.com", Config.baseUrl());
    }

    @Test
    void shouldReturnCustomBaseUrlWhenProvided() {
        System.setProperty("baseUrl", "https://www.wikipedia.org");
        assertEquals("https://www.wikipedia.org", Config.baseUrl());
    }

    @Test
    void shouldReturnDefaultBrowserWhenNotProvided() {
        assertEquals("chrome", Config.browser());
    }

    @Test
    void shouldReturnCustomBrowserWhenProvided() {
        System.setProperty("browser", "firefox");
        assertEquals("firefox", Config.browser());
    }

    @Test
    void shouldReturnFalseForHeadlessByDefault() {
        assertFalse(Config.headless());
    }

    @Test
    void shouldReturnTrueForHeadlessWhenProvided() {
        System.setProperty("headless", "true");
        assertTrue(Config.headless());
    }

    @Test
    void shouldReturnDefaultTimeoutWhenNotProvided() {
        assertEquals(10, Config.timeoutSeconds());
    }

    @Test
    void shouldReturnCustomTimeoutWhenProvided() {
        System.setProperty("timeout", "25");
        assertEquals(25, Config.timeoutSeconds());
    }

    @Test
    void shouldReturnDefaultRemoteUrlWhenNotProvided() {
        assertEquals("http://localhost:4444/wd/hub", Config.remoteUrl());
    }

    @Test
    void shouldReturnCustomRemoteUrlWhenProvided() {
        System.setProperty("remoteUrl", "http://grid:4444/wd/hub");
        assertEquals("http://grid:4444/wd/hub", Config.remoteUrl());
    }

    @Test
    void shouldFallbackToDefaultTimeoutWhenInvalidValueIsProvided() {
        System.setProperty("timeout", "abc");
        assertEquals(10, Config.timeoutSeconds());
    }

}
