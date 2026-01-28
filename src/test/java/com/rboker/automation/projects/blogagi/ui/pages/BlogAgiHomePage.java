package com.rboker.automation.projects.blogagi.ui.pages;

import com.rboker.automation.core.BasePage;
import com.rboker.automation.core.ElementActions;
import com.rboker.automation.factories.WaitFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Page Object do Blog do Agi.
 *
 * Responsabilidades:
 * - abrir busca (lupa)
 * - pesquisar por termo (digitar + ENTER)
 * - detectar resultados (títulos)
 * - detectar mensagem de "nenhum resultado"
 */
public class BlogAgiHomePage extends BasePage {

    private static final By SEARCH_ICON =
            By.cssSelector("a.slide-search.astra-search-icon, a.astra-search-icon");

    private static final By SEARCH_INPUT =
            By.cssSelector("input#search-field, input.search-field[name='s'], form.search-form input[type='search']");

    private static final By RESULT_TITLES =
            By.cssSelector("main#main article h2.entry-title a");

    private static final By NO_RESULTS_MESSAGE =
            By.cssSelector("main#main section.no-results.not-found .page-content p");

    public BlogAgiHomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Garante viewport desktop para evitar layout mobile (breakpoint muda DOM e quebra interações).
     *
     * @return this
     */
    public BlogAgiHomePage ensureDesktopViewport() {
        driver.manage().window().setSize(new Dimension(1366, 768));
        return this;
    }

    /**
     * Realiza a pesquisa via UI:
     * - garante viewport desktop
     * - clica na lupa (primeiro elemento visível)
     * - espera o input aparecer visível
     * - digita o termo + ENTER
     * - espera resultados OU mensagem de vazio OU URL com ?s=
     *
     * @param term termo pesquisado
     */
    public void search(String term) {
        if (term == null) {
            throw new IllegalArgumentException("term não pode ser nulo");
        }

        ensureDesktopViewport();

        // 1) Se o input já estiver visível, não precisa abrir overlay/lupa
        if (isAnyDisplayed(SEARCH_INPUT)) {
            ElementActions.sendKeys(SEARCH_INPUT, term, Keys.ENTER);
            waitSearchResultState();
            return;
        }

        // 2) Caso não esteja, tenta abrir via lupa (primeiro visível)
        ElementActions.clickFirstDisplayed(SEARCH_ICON);

        // 3) Agora espera o input ficar visível
        ElementActions.waitVisible(SEARCH_INPUT);

        // 4) Digita + ENTER
        ElementActions.sendKeys(SEARCH_INPUT, term, Keys.ENTER);

        waitSearchResultState();
    }

    private void waitSearchResultState() {
        WaitFactory.uiWait().until(d -> {
            try {
                return d.getCurrentUrl().contains("?s=")
                        || !d.findElements(RESULT_TITLES).isEmpty()
                        || !d.findElements(NO_RESULTS_MESSAGE).isEmpty();
            } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                return false;
            }
        });
    }

    /**
     * Verifica se existe pelo menos 1 elemento localizado que esteja visível.
     */
    private boolean isAnyDisplayed(By locator) {
        List<WebElement> els = driver.findElements(locator);
        return els.stream().anyMatch(WebElement::isDisplayed);
    }

    /**
     * Retorna os títulos dos resultados (lista pode ser vazia).
     *
     * @return lista de títulos
     */
    public List<String> getResultTitles() {
        List<String> titles = new ArrayList<>();
        List<WebElement> els = driver.findElements(RESULT_TITLES);

        for (WebElement el : els) {
            String t = el.getText();
            if (t != null && !t.isBlank()) {
                titles.add(t.trim());
            }
        }
        return titles;
    }

    /**
     * Regra: existem vários RESULT_TITLES e todos devem conter o termo pesquisado
     * (case-insensitive + accent-insensitive).
     *
     * @param term termo pesquisado
     * @return true se todos os títulos contiverem o termo
     */
    public boolean allResultTitlesContain(String term) {
        List<String> titles = getResultTitles();
        if (titles.isEmpty()) {
            return false;
        }

        String needle = normalize(term);
        for (String title : titles) {
            if (!normalize(title).contains(needle)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se a mensagem de "não encontrado" está exibida e é exatamente a esperada.
     *
     * @param expectedMessage mensagem esperada
     * @return true se a mensagem estiver presente e igual
     */
    public boolean isNoResultsMessageDisplayed(String expectedMessage) {
        if (expectedMessage == null) {
            throw new IllegalArgumentException("expectedMessage não pode ser nula");
        }

        WaitFactory.uiWait().until(d -> !d.findElements(NO_RESULTS_MESSAGE).isEmpty());

        String actual = driver.findElement(NO_RESULTS_MESSAGE).getText();
        if (actual == null) {
            return false;
        }

        return actual.trim().equals(expectedMessage.trim());
    }

    private String normalize(String s) {
        if (s == null) return "";
        String lower = s.toLowerCase(Locale.ROOT).trim();
        String n = Normalizer.normalize(lower, Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "");
    }
}
