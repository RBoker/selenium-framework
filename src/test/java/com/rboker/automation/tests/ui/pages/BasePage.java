package com.rboker.automation.tests.ui.pages;

import org.openqa.selenium.WebDriver;

/**
 * BasePage para Page Objects no escopo de testes.
 * Mantém o WebDriver para navegação e leitura de URL/título.
 *
 * Observação:
 * - ElementActions/WaitFactory no framework são utilitários estáticos,
 *   então os Page Objects chamam ElementActions diretamente.
 */
public abstract class BasePage {

    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    public String title() {
        return driver.getTitle();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }
}
