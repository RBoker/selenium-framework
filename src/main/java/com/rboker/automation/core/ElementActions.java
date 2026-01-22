package com.rboker.automation.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Ações seguras para interação com elementos.
 * O objetivo é esconder a complexidade de waits e reduzir flakiness.
 */
public final class ElementActions {

    private ElementActions() {
        // Evita instanciação
    }

    /**
     * Aguarda um elemento ficar visível.
     *
     * @param locator localizador do elemento
     * @return WebElement visível
     */
    public static WebElement waitVisible(By locator) {
        return WaitFactory.defaultWait()
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Aguarda um elemento ficar clicável.
     *
     * @param locator localizador do elemento
     * @return WebElement clicável
     */
    public static WebElement waitClickable(By locator) {
        return WaitFactory.defaultWait()
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Clica em um elemento de forma segura (com wait de clicável).
     *
     * @param locator localizador do elemento
     */
    public static void click(By locator) {
        waitClickable(locator).click();
    }

    /**
     * Digita em um elemento (com wait de visível).
     *
     * @param locator localizador do elemento
     * @param text texto a ser digitado
     */
    public static void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    /**
     * Retorna o texto visível de um elemento (trimado).
     *
     * @param locator localizador do elemento
     * @return texto do elemento (trimado)
     */
    public static String text(By locator) {
        return waitVisible(locator).getText().trim();
    }

    /**
     * Alias mais explícito para leitura de texto.
     * Mantém compatibilidade e sem duplicar lógica.
     */
    public static String getText(By locator) {
        return text(locator);
    }
}
