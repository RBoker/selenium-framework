package com.rboker.automation.tests.bdd.steps.wikipedia;

import com.rboker.automation.factories.Pages;
import com.rboker.automation.core.DriverManager;
import com.rboker.automation.support.TestConfig;
import com.rboker.automation.ui.pages.wikipedia.WikipediaPage;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import org.junit.jupiter.api.Assertions;

/**
 * Steps do cenário de busca na Wikipedia.
 * Steps não usam locators: apenas Page Objects (aderência ao padrão).
 */
public class WikipediaSearchSteps {

    private WikipediaPage wikiPage;

    @Dado("que acesso a home da Wikipedia")
    public void queAcessoAHomeDaWikipedia() {
        wikiPage = Pages.wikipedia()
                .open(TestConfig.baseUrl());
    }

    @Quando("eu pesquisar pelo termo {string}")
    public void euPesquisarPeloTermo(String termo) {
        // Reaproveita a instância aberta; se não existir, cria com o driver atual
        if (wikiPage == null) {
            wikiPage = new WikipediaPage(DriverManager.getDriver()).open(TestConfig.baseUrl());
        }
        wikiPage.search(termo);
    }

    @Então("devo ver o título do artigo {string}")
    public void devoVerOTituloDoArtigo(String tituloEsperado) {
        Assertions.assertEquals(tituloEsperado, wikiPage.heading());
    }

    @Quando("eu pesquiso por {string}")
    public void eu_pesquiso_por(String termo) {
        // Reaproveita o fluxo existente (garante que a home foi aberta)
        queAcessoAHomeDaWikipedia();
        euPesquisarPeloTermo(termo);
    }

    @Então("devo ver resultados relacionados a {string}")
    public void devo_ver_resultados_relacionados_a(String termoEsperado) {
        String titulo = wikiPage.heading();
        Assertions.assertTrue(
                titulo != null && titulo.toLowerCase().contains(termoEsperado.toLowerCase()),
                "O título do artigo deveria conter o termo pesquisado. Título atual: " + titulo
        );

        // Opcional (mas útil): valida que estamos em URL de wiki/artigo/resultado
        Assertions.assertTrue(
                wikiPage.isWikiOrSearchUrl(),
                "URL não parece ser de artigo/resultado da Wikipedia. URL: " + wikiPage.currentUrl()
        );
    }
}
