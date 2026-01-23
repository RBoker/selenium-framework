package com.rboker.automation.tests.ui.pages;

import com.rboker.automation.core.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object da página de resultados/artigo do Wikipedia.
 */
public class WikipediaResultsPage extends BasePage {

    private final By firstHeading = By.id("firstHeading");

    public WikipediaResultsPage(WebDriver driver) {
        super(driver);
        ElementActions.waitVisible(firstHeading);
    }

    public String heading() {
        return ElementActions.getText(firstHeading);
    }

    public boolean isWikiOrSearchUrl() {
        String url = currentUrl();
        return url != null && (url.contains("wiki") || url.contains("search"));
    }
}
