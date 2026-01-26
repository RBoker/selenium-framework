package com.rboker.automation.core;

import com.rboker.automation.factories.WaitFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

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
     * Clique via JavaScript.
     * Útil quando overlays/animações impedem o clique nativo do Selenium.
     *
     * @param locator localizador do elemento
     */
    public static void jsClick(By locator) {
        WebElement el = waitVisible(locator);
        ((JavascriptExecutor) DriverManager.getDriver())
                .executeScript("arguments[0].click();", el);
    }

    /**
     * Clique via JavaScript em um WebElement.
     * Útil quando overlays/animações impedem o clique nativo do Selenium.
     *
     * @param element elemento alvo
     */
    public static void jsClick(WebElement element) {
        ((JavascriptExecutor) DriverManager.getDriver())
                .executeScript("arguments[0].click();", element);
    }

    /**
     * Aguarda um elemento ficar visível.
     *
     * @param locator localizador do elemento
     * @return WebElement visível
     */
    public static WebElement waitVisible(By locator) {
        return WaitFactory.uiWait()
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Aguarda um elemento ficar clicável.
     *
     * @param locator localizador do elemento
     * @return WebElement clicável
     */
    public static WebElement waitClickable(By locator) {
        return WaitFactory.uiWait()
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Retorna o primeiro elemento visível (isDisplayed) encontrado por um locator.
     *
     * <p>Esse método é essencial para páginas que duplicam componentes no DOM (ex.: temas WordPress como Astra),
     * onde o mesmo locator pode retornar múltiplos elementos e apenas um está visível/interagível.</p>
     *
     * @param locator localizador que pode retornar múltiplos matches
     * @return primeiro WebElement visível
     * @throws NoSuchElementException caso nenhum match esteja visível
     */
    public static WebElement firstDisplayed(By locator) {
        WaitFactory.uiWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));

        List<WebElement> all = DriverManager.getDriver().findElements(locator);

        return all.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Nenhum elemento visível para: " + locator));
    }

    /**
     * Clica no primeiro elemento visível encontrado por um locator.
     *
     * <p>Implementa retry para cenários de DOM dinâmico (StaleElementReference) e overlays (click interceptado),
     * com fallback para clique via JavaScript.</p>
     *
     * @param locator localizador que pode retornar múltiplos matches
     */
    public static void clickFirstDisplayed(By locator) {
        int attempts = 0;

        while (true) {
            try {
                WebElement el = firstDisplayed(locator);

                // Aguarda clicabilidade no WebElement (não no By), porque o By pode apontar para elemento invisível
                WaitFactory.uiWait().until(ExpectedConditions.elementToBeClickable(el)).click();
                return;

            } catch (StaleElementReferenceException e) {
                attempts++;
                if (attempts >= DEFAULT_RETRY_ATTEMPTS) throw e;

            } catch (ElementClickInterceptedException | TimeoutException e) {
                attempts++;
                if (attempts >= DEFAULT_RETRY_ATTEMPTS) {
                    // fallback definitivo
                    WebElement el = firstDisplayed(locator);
                    jsClick(el);
                    return;
                }
            }
        }
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
            } catch (StaleElementReferenceException e) {
                attempts++;
                if (attempts >= DEFAULT_RETRY_ATTEMPTS) throw e;
            } catch (ElementClickInterceptedException e) {
                attempts++;
                if (attempts >= DEFAULT_RETRY_ATTEMPTS) {
                    // fallback definitivo
                    jsClick(locator);
                    return;
                }
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
