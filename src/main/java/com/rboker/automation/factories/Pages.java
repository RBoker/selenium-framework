package com.rboker.automation.factories;

import com.rboker.automation.core.DriverManager;
import com.rboker.automation.ui.pages.wikipedia.WikipediaPage;

/**
 * Factory central de Page Objects.
 * Mantém a criação padronizada e centraliza o acesso ao WebDriver.
 *
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
}
