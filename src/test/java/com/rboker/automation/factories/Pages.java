package com.rboker.automation.factories;

import com.rboker.automation.pages.WikipediaArticlePage;
import com.rboker.automation.pages.WikipediaHomePage;

/**
 * Factory central de Page Objects.
 * Mantém a criação padronizada e facilita evolução (ex.: injecção de dependências).
 */
public final class Pages {

    private Pages() {
        // Utility class
    }

    public static WikipediaHomePage wikipediaHome() {
        return new WikipediaHomePage();
    }

    public static WikipediaArticlePage wikipediaArticle() {
        return new WikipediaArticlePage();
    }
}
