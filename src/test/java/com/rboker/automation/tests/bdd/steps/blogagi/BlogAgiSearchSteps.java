package com.rboker.automation.tests.bdd.steps.blogagi;

import com.rboker.automation.factories.Pages;
import com.rboker.automation.support.TestConfig;
import com.rboker.automation.ui.pages.blogagi.BlogAgiHomePage;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import org.junit.jupiter.api.Assertions;

/**
 * Steps de pesquisa no Blog do Agi.
 * Mantém o padrão: Steps chamam apenas Page Objects (sem locators).
 */
public class BlogAgiSearchSteps {

    private BlogAgiHomePage page;
    private String termoPesquisado;

    @Dado("que acesso o Blog do Agi")
    public void queAcessoOBlogDoAgi() {
        // O Hooks já abre baseUrl automaticamente (@ui).
        // Aqui, garantimos instância pronta e validação básica de config.
        Assertions.assertNotNull(TestConfig.baseUrl(), "baseUrl não pode ser nula");
        page = Pages.blogAgi();
    }

    @Quando("realizo a pesquisa pelo termo {string}")
    public void realizoAPesquisaPeloTermo(String termo) {
        if (page == null) page = Pages.blogAgi();

        termoPesquisado = termo;

        // Garante desktop (no blog do Agi o breakpoint muda DOM e quebra locators)
        page.ensureDesktopViewport();

        // Executa busca via Page Object
        page.search(termo);
    }

    @Então("devo visualizar resultados relevantes da pesquisa")
    public void devoVisualizarResultadosRelevantes() {
        Assertions.assertNotNull(page, "Page não inicializada. Verifique o step @Dado.");
        Assertions.assertNotNull(termoPesquisado, "Termo pesquisado não foi definido.");

        Assertions.assertTrue(
                page.allResultTitlesContain(termoPesquisado),
                "Esperava que TODOS os títulos contivessem o termo pesquisado."
        );
    }

    @Então("devo visualizar a mensagem {string}")
    public void devoVisualizarMensagemNenhumResultado(String mensagemEsperada) {
        Assertions.assertNotNull(page, "Page não inicializada. Verifique o step @Dado.");

        Assertions.assertTrue(
                page.isNoResultsMessageDisplayed(mensagemEsperada),
                "Esperava a mensagem exata de nenhum resultado encontrado."
        );
    }
}
