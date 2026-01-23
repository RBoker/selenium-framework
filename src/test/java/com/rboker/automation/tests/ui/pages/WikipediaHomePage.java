package com.rboker.automation.tests.ui.pages;

import com.rboker.automation.core.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

/**
 * Page Object da Home do Wikipedia.
 */
public class WikipediaHomePage extends BasePage {

    // Para en.wikipedia.org, este name costuma ser mais estável que o id.
    // Se quiser manter o id, pode deixar By.id("searchInput") também.
    private final By searchInput = By.name("search");

    public WikipediaHomePage(WebDriver driver) {
        super(driver);
    }

    public WikipediaResultsPage search(String text) {
        // Evita clicar no botão (que re-renderiza e gera stale). ENTER é mais estável.
        ElementActions.sendKeys(searchInput, text, Keys.ENTER);
        return new WikipediaResultsPage(driver);
    }
}
