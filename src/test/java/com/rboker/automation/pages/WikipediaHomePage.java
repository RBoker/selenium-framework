package com.rboker.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.rboker.automation.core.DriverManager;


/**
 * Page Object da Home da Wikipedia.
 */
public class WikipediaHomePage {

    private final WebDriver driver;

    private final By searchInput = By.id("searchInput");
    private final By searchButton = By.cssSelector("button[type='submit']");

    public WikipediaHomePage() {
        this.driver = DriverManager.getDriver();
    }

    /**
     * Abre a home no baseUrl já definido externamente.
     *
     * @param baseUrl URL base
     * @return a própria página
     */
    public WikipediaHomePage open(String baseUrl) {
        driver.get(baseUrl);
        return this;
    }

    /**
     * Realiza a pesquisa e navega para a página do artigo.
     *
     * @param term termo de busca
     * @return página do artigo
     */
    public WikipediaArticlePage searchFor(String term) {
        WebElement input = driver.findElement(searchInput);
        input.clear();
        input.sendKeys(term);
        driver.findElement(searchButton).click();
        return new WikipediaArticlePage();
    }
}
