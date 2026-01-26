package com.rboker.automation.ui.pages.blogagi;

import com.rboker.automation.core.BasePage;
import com.rboker.automation.core.ElementActions;
import com.rboker.automation.factories.WaitFactory;
import org.openqa.selenium.*;
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

    // ===== Locators (mantidos conforme você já tinha) =====
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

        // Aguarda resultado OU mensagem de nenhum resultado
        WaitFactory.uiWait().until(d ->
                !d.findElements(RESULT_TITLES).isEmpty()
                        || !d.findElements(NO_RESULTS_MESSAGE).isEmpty()
        );

        return this;
    }

    /**
     * Indica se há resultados na página atual.
     *
     * @return true se existir pelo menos 1 título de resultado
     */
    public boolean hasResults() {
        return !driver.findElements(RESULT_TITLES).isEmpty();
    }

    /**
     * Indica se há mensagem de nenhum resultado.
     *
     * @return true se a mensagem estiver presente
     */
    public boolean hasNoResultsMessage() {
        return !driver.findElements(NO_RESULTS_MESSAGE).isEmpty();
    }

    /**
     * Retorna a lista de títulos (texto) dos resultados.
     * Útil para asserts mais ricos.
     *
     * @return lista de textos dos links de títulos
     */
    public List<String> resultTitles() {
        return driver.findElements(RESULT_TITLES)
                .stream()
                .map(e -> e.getText().trim())
                .filter(t -> !t.isBlank())
                .toList();
    }

    // ===========================
    // Helpers internos
    // ===========================

    /**
     * Retorna o primeiro elemento visível para um locator que pode retornar múltiplos matches.
     * Essencial para o tema Astra, que replica componentes no DOM.
     *
     * @param locator By do elemento
     * @return primeiro WebElement visível
     */
    private WebElement firstDisplayed(By locator) {
        WaitFactory.uiWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));

        List<WebElement> all = driver.findElements(locator);

        return all.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Nenhum elemento visível para: " + locator));
    }

    /**
     * Clique via JavaScript em WebElement.
     * Necessário porque o click pode ser interceptado por overlay/header.
     *
     * @param el elemento alvo
     */
    private void jsClick(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }
}
