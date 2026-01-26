package com.rboker.automation.factories;

import com.rboker.automation.config.FrameworkConfig;
import com.rboker.automation.core.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;

/**
 * Factory para criação de waits usando timeout padrão do framework.
 *
 * Além de criar FluentWait, oferece helpers estáticos para:
 * - aguardar visibilidade/clickable/invisibilidade/presença
 * - enviar texto com segurança (limpa + digita)
 * - selecionar option em <select>
 * - validar atributos
 *
 * Observação:
 * Mantém compatibilidade com o código existente (defaultWait/uiWait/getWait).
 */
public final class WaitFactory {

    private static final Duration DEFAULT_POLLING = Duration.ofMillis(200);

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
                .pollingEvery(DEFAULT_POLLING)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class);
    }

    /**
     * Wait mais tolerante para UI/E2E (sites reais, animações, ads etc.).
     */
    public static FluentWait<WebDriver> uiWait() {
        return new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(FrameworkConfig.uiTimeoutSeconds()))
                .pollingEvery(DEFAULT_POLLING)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class)
                .ignoring(ElementClickInterceptedException.class);
    }

    /**
     * Alias por compatibilidade.
     */
    public static FluentWait<WebDriver> getWait() {
        return defaultWait();
    }

    /* =========================================================
       Helpers - visibilidade / presença / clique / texto
       ========================================================= */

    /**
     * Aguarda o elemento ficar visível e retorna o WebElement.
     */
    public static WebElement visibilityOf(By locator) {
        return uiWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Aguarda o elemento existir no DOM (mesmo que não esteja visível).
     * Útil para itens dentro de listas/scroll/menus.
     */
    public static WebElement presenceOf(By locator) {
        return uiWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Aguarda o elemento ficar clicável e retorna o WebElement.
     */
    public static WebElement elementToBeClickable(By locator) {
        return uiWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Aguarda o elemento desaparecer/ficar invisível.
     */
    public static boolean invisibilityOf(By locator) {
        return uiWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Click resiliente:
     * - tenta clickable normal
     * - se interceptar, faz scroll e tenta de novo
     * - se ainda interceptar/timeout, faz JS click como último recurso
     */
    public static void click(By locator) {
        WebDriver driver = DriverManager.getDriver();

        int attempts = 0;
        while (attempts < 3) {
            try {
                WebElement el = elementToBeClickable(locator);
                scrollIntoView(el);
                el.click();
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
            } catch (ElementClickInterceptedException e) {
                // tenta tirar o alvo da zona do overlay e repetir
                attempts++;
            } catch (TimeoutException e) {
                // às vezes o elemento existe mas não fica "clicável" por overlay/scroll
                break;
            }
        }

        // Fallback: presence + scroll + JS click (melhor esforço)
        WebElement el = presenceOf(locator);
        scrollIntoView(el);
        jsClick(el);
    }

    /**
     * Envia texto com segurança: espera visível, limpa e digita.
     */
    public static void sendKeys(By locator, String text) {
        WebElement el = visibilityOf(locator);
        el.clear();
        el.sendKeys(text);
    }

    /* =========================================================
       Helpers - select / atributos
       ========================================================= */

    /**
     * Seleciona um item em um <select> pelo texto visível.
     */
    public static void selectByVisibleText(By selectLocator, String visibleText) {
        WebElement el = visibilityOf(selectLocator);
        new Select(el).selectByVisibleText(visibleText);
    }

    /**
     * Aguarda até que um atributo do elemento seja igual ao valor esperado.
     */
    public static boolean attributeToBe(By locator, String attribute, String expectedValue) {
        return uiWait().until(ExpectedConditions.attributeToBe(locator, attribute, expectedValue));
    }

    /* =========================================================
       Helpers - utilitários opcionais
       ========================================================= */

    /**
     * Verifica rapidamente se um elemento está visível (sem explodir stacktrace no teste).
     */
    public static boolean isVisible(By locator) {
        try {
            visibilityOf(locator);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Scroll para o centro da viewport.
     */
    public static void scrollIntoView(WebElement el) {
        try {
            ((JavascriptExecutor) DriverManager.getDriver())
                    .executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
        } catch (Exception ignored) {
            // best effort
        }
    }

    /**
     * Click via JS (último recurso).
     */
    public static void jsClick(WebElement el) {
        ((JavascriptExecutor) DriverManager.getDriver())
                .executeScript("arguments[0].click();", el);
    }
}
