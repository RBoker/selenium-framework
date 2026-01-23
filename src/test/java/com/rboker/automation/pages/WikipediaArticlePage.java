package com.rboker.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.rboker.automation.core.DriverManager;

/**
 * Page Object da página de artigo da Wikipedia.
 */
public class WikipediaArticlePage {

    private final WebDriver driver;

    private final By firstHeading = By.id("firstHeading");

    public WikipediaArticlePage() {
        this.driver = DriverManager.getDriver();
    }

    /**
     * Obtém o título do artigo.
     *
     * @return texto do heading principal
     */
    public String getArticleTitle() {
        return driver.findElement(firstHeading).getText().trim();
    }
}
