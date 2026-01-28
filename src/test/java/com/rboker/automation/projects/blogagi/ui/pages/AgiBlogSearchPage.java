package com.rboker.automation.projects.blogagi.ui.pages;

import com.rboker.automation.core.BasePage;
import com.rboker.automation.core.ElementActions;
import com.rboker.automation.factories.WaitFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object para busca no Blog do Agi (tema Astra/WordPress).
 *
 * <p>Pontos críticos do site para automação:</p>
 * <ul>
 *   <li>O ícone de busca pode existir múltiplas vezes no DOM (alguns invisíveis).</li>
 *   <li>O input de busca só fica visível após abrir o overlay.</li>
 *   <li>Cliques podem ser interceptados (overlay/header), exigindo fallback para JS.</li>
 * </ul>
 */
public class AgiBlogSearchPage extends BasePage {

    private static final By SEARCH_ICON =
            By.cssSelector("a.slide-search.astra-search-icon, a.astra-search-icon");

    private static final By SEARCH_INPUT =
            By.cssSelector("input#search-field");

    private static final By RESULT_TITLES =
            By.cssSelector("main#main article h2.entry-title a");

    private static final By NO_RESULTS_MESSAGE =
            By.cssSelector("main#main section.no-results.not-found .page-content p");

    public AgiBlogSearchPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Garante viewport desktop para evitar layout mobile (breakpoint muda DOM).
     *
     * @return this
     */
    public AgiBlogSearchPage ensureDesktopViewport() {
        driver.manage().window().setSize(new Dimension(1366, 768));
        return this;
    }

    /**
     * Abre o overlay de busca clicando no ícone visível.
     *
     * @return this
     */
    public AgiBlogSearchPage openSearch() {
        // Garante viewport desktop para evitar DOM mobile
        ensureDesktopViewport();

        WebElement icon = firstDisplayed(SEARCH_ICON);
        jsClick(icon);

        // Espera o input ficar visível (no site ele só aparece após o clique no ícone)
        ElementActions.waitVisible(SEARCH_INPUT);
        return this;
    }

    /**
     * Executa busca pelo termo informado.
     *
     * <p>ENTER costuma ser mais estável do que clicar no botão submit (overlay pode interceptar).</p>
     *
     * @param term termo a buscar
     * @return this
     */
    public AgiBlogSearchPage search(String term) {
        ElementActions.type(SEARCH_INPUT, term);
        ElementActions.sendKeys(SEARCH_INPUT, Keys.ENTER);

        // Aguarda estado de busca: URL ?s= OU resultados OU mensagem de nenhum resultado
        WaitFactory.uiWait().until(d ->
                d.getCurrentUrl().contains("?s=")
                        || !d.findElements(RESULT_TITLES).isEmpty()
                        || !d.findElements(NO_RESULTS_MESSAGE).isEmpty()
        );

        return this;
    }

    public boolean hasResults() {
        return !driver.findElements(RESULT_TITLES).isEmpty();
    }

    public boolean hasNoResultsMessage() {
        return !driver.findElements(NO_RESULTS_MESSAGE).isEmpty();
    }

    public List<String> resultTitles() {
        return driver.findElements(RESULT_TITLES)
                .stream()
                .map(e -> e.getText().trim())
                .filter(t -> !t.isBlank())
                .toList();
    }

    private WebElement firstDisplayed(By locator) {
        WaitFactory.uiWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));

        List<WebElement> all = driver.findElements(locator);

        return all.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Nenhum elemento visível para: " + locator));
    }

    private void jsClick(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }
}
