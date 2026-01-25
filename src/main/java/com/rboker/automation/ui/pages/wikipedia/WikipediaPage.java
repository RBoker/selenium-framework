package com.rboker.automation.ui.pages.wikipedia;

import com.rboker.automation.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object único para a Wikipedia (Home + Artigo/Resultados).
 *
 * Responsabilidades:
 * - pesquisar por um termo
 * - obter heading (título principal da página atual)
 * - expor URL atual para validações
 */
public class WikipediaPage extends BasePage {

    private final By searchInput = By.id("searchInput");
    private final By searchButton = By.cssSelector("button[type='submit']");
    private final By firstHeading = By.id("firstHeading");

    public WikipediaPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Abre a home no baseUrl informado.
     */
    public WikipediaPage open(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl não pode ser nulo/vazio");
        }
        driver.get(baseUrl);
        return this;
    }

    /**
     * Pesquisa por um termo.
     * Após o submit, o navegador vai para artigo ou resultados.
     */
    public WikipediaPage search(String term) {
        WebElement input = driver.findElement(searchInput);
        input.clear();
        input.sendKeys(term);
        driver.findElement(searchButton).click();
        return this;
    }

    /**
     * Heading principal da página atual (artigo/resultados).
     */
    public String heading() {
        return driver.findElement(firstHeading).getText().trim();
    }

    /**
     * Heurística simples para validar que estamos em uma URL de artigo
     * ou página de busca/resultados.
     */
    public boolean isWikiOrSearchUrl() {
        String url = currentUrl().toLowerCase();
        return url.contains("wikipedia.org/wiki/")
                || url.contains("wikipedia.org/w/index.php")
                || url.contains("wikipedia.org/w/");
    }

}
