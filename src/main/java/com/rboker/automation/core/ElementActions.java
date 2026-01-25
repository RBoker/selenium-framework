package com.rboker.automation.core;

import com.rboker.automation.factories.WaitFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


/**
 * Ações seguras para interação com elementos.
 * O objetivo é esconder a complexidade de waits e reduzir flakiness.
 */
public final class ElementActions {

    private static final int DEFAULT_RETRY_ATTEMPTS = 3;

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
     * Implementa retry para cenários de DOM dinâmico (StaleElementReference) e overlays (click interceptado).
     *
     * @param locator localizador do elemento
     */
    public static void click(By locator) {
        int attempts = 0;

        while (true) {
            try {
                waitClickable(locator).click();
                return;
            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                attempts++;
                if (attempts >= DEFAULT_RETRY_ATTEMPTS) {
                    throw e;
                }
                // Re-tenta: o DOM pode ter sido atualizado (stale) ou um overlay momentâneo interceptou o clique.
            }
        }
    }

    /**
     * Digita em um elemento (com wait de visível).
     * Implementa retry para cenários onde o input pode ser re-renderizado (stale).
     *
     * @param locator localizador do elemento
     * @param text texto a ser digitado
     */
    public static void type(By locator, String text) {
        int attempts = 0;

        while (true) {
            try {
                WebElement el = waitVisible(locator);
                el.clear();
                el.sendKeys(text);
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
                if (attempts >= DEFAULT_RETRY_ATTEMPTS) {
                    throw e;
                }
                // Re-tenta: o elemento pode ter sido re-renderizado entre o wait e o sendKeys.
            }
        }
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

    /**
     * Envia teclas para um elemento (com wait de visível).
     * Útil para combinações como Keys.ENTER sem concatenar String.
     *
     * @param locator localizador do elemento
     * @param keys teclas/textos a serem enviados
     */
    public static void sendKeys(By locator, CharSequence... keys) {
        int attempts = 0;

        while (true) {
            try {
                WebElement el = waitVisible(locator);
                el.clear();
                el.sendKeys(keys);
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
                if (attempts >= 3) {
                    throw e;
                }
            }
        }
    }

}
