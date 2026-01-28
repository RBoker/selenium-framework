package com.rboker.automation.projects.blogagi.ui.steps;

import com.rboker.automation.context.ScenarioContext;
import com.rboker.automation.projects.blogagi.ui.pages.BlogAgiHomePage;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.junit.jupiter.api.Assertions;

/**
 * Steps de pesquisa no Blog do Agi.
 * Mantém o padrão: Steps chamam apenas Page Objects (sem locators).
 *
 * <p>Novo padrão do framework:
 * - WebDriver é gerenciado pelo Hook global
 * - Steps recebem recursos do cenário via ScenarioContext (injeção do Cucumber)
 * - Page Objects recebem o driver no construtor (não usam DriverManager diretamente)</p>
 */
public class BlogAgiSearchSteps {

    private final ScenarioContext context;

    private BlogAgiHomePage page;
    private String termoPesquisado;

    /**
     * Cucumber injeta o ScenarioContext por cenário.
     */
    public BlogAgiSearchSteps(ScenarioContext context) {
        this.context = context;
    }

    @Dado("que acesso o Blog do Agi")
    public void queAcessoOBlogDoAgi() {
        // O Hooks já abre baseUrl automaticamente (@ui). Aqui só garantimos contexto válido e page pronta.
        Assertions.assertNotNull(context.getDriver(), "WebDriver não foi inicializado no Hook.");
        Assertions.assertNotNull(context.getBaseUrl(), "baseUrl não pode ser nula (verifique YAML/config do projeto).");

        page = new BlogAgiHomePage(context.getDriver());
    }

    @Quando("realizo a pesquisa pelo termo {string}")
    public void realizoAPesquisaPeloTermo(String termo) {
        if (page == null) {
            page = new BlogAgiHomePage(context.getDriver());
        }

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
