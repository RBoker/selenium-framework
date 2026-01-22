package com.rboker.automation.core;

import org.openqa.selenium.WebDriver;

/**
 * BasePage: base para Page Objects.
 * Mantém o WebDriver para navegação e ações que não passem pelos utilitários.
 *
 * Observação:
 * - ElementActions e WaitFactory são utilitários estáticos no framework,
 *   então os Page Objects devem chamá-los diretamente.
 */
public abstract class BasePage {

    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }
}
