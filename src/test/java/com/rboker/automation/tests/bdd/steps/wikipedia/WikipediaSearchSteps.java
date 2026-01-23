package com.rboker.automation.tests.bdd.steps.wikipedia;

import com.rboker.automation.factories.Pages;
import com.rboker.automation.pages.WikipediaArticlePage;
import com.rboker.automation.support.TestConfig;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import org.junit.jupiter.api.Assertions;

/**
 * Steps do cenário de busca na Wikipedia.
 * Steps não usam locators: apenas Page Objects (aderência ao padrão).
 */
public class WikipediaSearchSteps {

    private WikipediaArticlePage articlePage;

    @Dado("que acesso a home da Wikipedia")
    public void queAcessoAHomeDaWikipedia() {
        Pages.wikipediaHome()
                .open(TestConfig.baseUrl());
    }

    @Quando("eu pesquisar pelo termo {string}")
    public void euPesquisarPeloTermo(String termo) {
        articlePage = Pages.wikipediaHome()
                .searchFor(termo);
    }

    @Então("devo ver o título do artigo {string}")
    public void devoVerOTituloDoArtigo(String tituloEsperado) {
        Assertions.assertEquals(tituloEsperado, articlePage.getArticleTitle());
    }

    @Quando("eu pesquiso por {string}")
    public void eu_pesquiso_por(String termo) {
        // Reaproveita o fluxo existente (garante que a home foi aberta)
        queAcessoAHomeDaWikipedia();
        euPesquisarPeloTermo(termo);
    }

    @Então("devo ver resultados relacionados a {string}")
    public void devo_ver_resultados_relacionados_a(String termoEsperado) {
        String titulo = articlePage.getArticleTitle();
        Assertions.assertTrue(
                titulo != null && titulo.toLowerCase().contains(termoEsperado.toLowerCase()),
                "O título do artigo deveria conter o termo pesquisado. Título atual: " + titulo
        );
    }


}
