package com.rboker.automation.context;

import org.openqa.selenium.WebDriver;

/**
 * Contexto compartilhado por cenário (Cucumber).
 *
 * <p>Serve para compartilhar recursos do cenário (ex.: WebDriver, baseUrl, dados temporários)
 * entre Hooks e Step Definitions sem acoplar o framework a Pages específicas de projeto.</p>
 *
 * <p>Este objeto é instanciado pelo container do Cucumber (PicoContainer) com escopo de cenário.</p>
 */
public class ScenarioContext {

    private WebDriver driver;
    private String baseUrl;

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
