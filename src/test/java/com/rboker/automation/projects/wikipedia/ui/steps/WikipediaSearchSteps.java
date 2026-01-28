package com.rboker.automation.projects.wikipedia.ui.steps;

import com.rboker.automation.projects.wikipedia.ui.pages.WikipediaPage;
import com.rboker.automation.context.ScenarioContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.junit.jupiter.api.Assertions;

/**
 * Steps do cenário de busca na Wikipedia.
 * Steps não usam locators: apenas Page Objects (aderência ao padrão).
 */
public class WikipediaSearchSteps {

    private final ScenarioContext context;
    private WikipediaPage wikiPage;

    /**
     * Cucumber injeta o ScenarioContext por cenário (PicoContainer).
     */
    public WikipediaSearchSteps(ScenarioContext context) {
        this.context = context;
    }

    @Dado("que acesso a home da Wikipedia")
    public void queAcessoAHomeDaWikipedia() {
        wikiPage = new WikipediaPage(context.getDriver())
                .open(context.getBaseUrl());
    }

    @Quando("eu pesquisar pelo termo {string}")
    public void euPesquisarPeloTermo(String termo) {
        // Reaproveita a instância aberta; se não existir, cria com o driver atual
        if (wikiPage == null) {
            wikiPage = new WikipediaPage(context.getDriver()).open(context.getBaseUrl());
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
