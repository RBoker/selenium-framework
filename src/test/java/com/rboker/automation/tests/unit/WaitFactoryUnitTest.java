package com.rboker.automation.tests.unit;

import com.rboker.automation.core.DriverManager;
import com.rboker.automation.factories.WaitFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

import static org.junit.jupiter.api.Assertions.*;

class WaitFactoryUnitTest {

    @AfterEach
    void tearDown() {
        DriverManager.quitDriver();
        System.clearProperty("timeout");
    }

    @Test
    void shouldCreateDefaultWaitUsingDriverFromDriverManager() {
        WebDriver mockDriver = Mockito.mock(WebDriver.class);
        DriverManager.setDriver(mockDriver);

        FluentWait<WebDriver> wait = WaitFactory.defaultWait();

        assertNotNull(wait, "defaultWait() não deveria retornar null");
    }

    @Test
    void shouldNotFailWhenCustomTimeoutIsProvided() {
        System.setProperty("timeout", "15");

        WebDriver mockDriver = Mockito.mock(WebDriver.class);
        DriverManager.setDriver(mockDriver);

        assertDoesNotThrow(
                WaitFactory::defaultWait,
                "defaultWait() não deveria falhar com timeout customizado"
        );
    }

    @Test
    void shouldNotFailWhenDefaultTimeoutIsUsed() {
        WebDriver mockDriver = Mockito.mock(WebDriver.class);
        DriverManager.setDriver(mockDriver);

        assertDoesNotThrow(
                WaitFactory::defaultWait,
                "defaultWait() não deveria falhar com timeout default"
        );
    }
}
