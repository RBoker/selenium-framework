package com.rboker.automation.tests.ui.pages;

import com.rboker.automation.core.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object da Home do Wikipedia.
 */
public class WikipediaHomePage extends BasePage {

    private final By searchInput = By.id("searchInput");
    private final By searchButton = By.cssSelector("button[type='submit']");

    public WikipediaHomePage(WebDriver driver) {
        super(driver);
    }

    public WikipediaResultsPage search(String text) {
        ElementActions.type(searchInput, text);
        ElementActions.click(searchButton);
        return new WikipediaResultsPage(driver);
    }
}
