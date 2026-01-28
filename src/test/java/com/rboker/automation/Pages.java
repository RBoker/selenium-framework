package com.rboker.automation;

import com.rboker.automation.core.DriverManager;
import com.rboker.automation.projects.wikipedia.ui.pages.WikipediaPage;
import com.rboker.automation.projects.blogagi.ui.pages.BlogAgiHomePage;

/**
 * Factory central de Page Objects.
 * Mantém a criação padronizada e centraliza o acesso ao WebDriver.
 * <p>
 * Padrão:
 * - Pages em src/main
 * - Driver gerenciado via DriverManager
 * - Uma Page por domínio funcional
 */
public final class Pages {

    private Pages() {
        // Utility class
    }

    /**
     * Page única da Wikipedia (Home + Artigo/Resultados).
     */
    public static WikipediaPage wikipedia() {
        return new WikipediaPage(DriverManager.getDriver());
    }
    /**
     * Page única da Agi Blog (Home + Artigo/Resultados).
     */
    public static BlogAgiHomePage blogAgi() { return new BlogAgiHomePage(DriverManager.getDriver());
    }
}
