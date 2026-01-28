package com.rboker.automation.projects.register.ui.steps;

import com.rboker.automation.projects.register.ui.pages.RegisterPage;
import com.rboker.automation.context.ScenarioContext;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

/**
 * Steps do fluxo de cadastro (Register).
 *
 * <p>Padrão do framework:
 * - WebDriver é gerenciado pelo Hook global (DriverManager)
 * - Steps recebem recursos do cenário via ScenarioContext (injeção do Cucumber)
 * - Page Objects vivem em src/test e recebem o driver do cenário</p>
 */
public class RegisterSteps {

    private final ScenarioContext context;

    private RegisterPage registerPage;

    // Guarda últimos valores selecionados para validação nos steps "Então"
    private String lastHobbiesCsv;
    private String lastLanguagesCsv;
    private String lastYear;
    private String lastMonth;
    private String lastDay;

    /**
     * Cucumber injeta o ScenarioContext por cenário (PicoContainer).
     */
    public RegisterSteps(ScenarioContext context) {
        this.context = context;
    }

    /**
     * Reinicializa estado por cenário (boa prática).
     * Evita que dados de validação "vazem" entre cenários.
     */
    @Before("@ui")
    public void beforeUiScenario() {
        registerPage = null;

        lastHobbiesCsv = null;
        lastLanguagesCsv = null;
        lastYear = null;
        lastMonth = null;
        lastDay = null;
    }

    /**
     * Page getter baseado no driver do cenário.
     */
    private RegisterPage page() {
        if (registerPage == null) {
            registerPage = new RegisterPage(context.getDriver());
        }
        return registerPage;
    }

    @Dado("que acesso a página de cadastro")
    public void acessarPaginaCadastro() {
        page().open(context.getBaseUrl());
    }

    @Quando("preencho o nome {string} e o sobrenome {string}")
    public void preencherNomeSobrenome(String nome, String sobrenome) {
        page().fillFirstName(nome);
        page().fillLastName(sobrenome);
    }

    @Quando("preencho o endereço {string}")
    public void preencherEndereco(String endereco) {
        page().fillAddress(endereco);
    }

    @Quando("preencho o email {string}")
    public void preencherEmail(String email) {
        page().fillEmail(email);
    }

    @Quando("preencho o email com um valor único")
    public void preencherEmailUnico() {
        page().fillUniqueEmail();
    }

    @Quando("preencho o telefone {string}")
    public void preencherTelefone(String telefone) {
        page().fillPhone(telefone);
    }

    @Quando("seleciono o gênero {string}")
    public void selecionarGenero(String genero) {
        page().selectGender(genero);
    }

    @Quando("seleciono o hobby {string}")
    public void selecionarHobby(String hobby) {
        lastHobbiesCsv = hobby;
        page().selectHobby(hobby);
    }

    @Quando("seleciono os hobbies {string}")
    public void selecionarMultiplosHobbies(String hobbies) {
        lastHobbiesCsv = hobbies;
        page().selectMultipleHobbies(hobbies);
    }

    @Quando("seleciono o idioma {string}")
    public void selecionarIdioma(String idioma) {
        lastLanguagesCsv = idioma;
        page().selectLanguage(idioma);
    }

    @Quando("seleciono os idiomas {string}")
    public void selecionarMultiplosIdiomas(String idiomas) {
        lastLanguagesCsv = idiomas;
        page().selectMultipleLanguages(idiomas);
    }

    @Quando("seleciono a skill {string}")
    public void selecionarSkill(String skill) {
        page().selectSkill(skill);
    }

    @Quando("seleciono o país no campo Country {string}")
    public void selecionarCountry(String country) {
        page().selectCountry(country);
    }

    @Quando("seleciono a data de nascimento {string} {string} {string}")
    public void selecionarDataNascimento(String ano, String mes, String dia) {
        lastYear = ano;
        lastMonth = mes;
        lastDay = dia;

        page().selectDateOfBirth(ano, mes, dia);
    }

    @Quando("informo a senha {string} e a confirmação {string}")
    public void informarSenha(String senha, String confirmacao) {
        page().fillPassword(senha, confirmacao);
    }

    @Quando("submeto o formulário")
    public void submeterFormulario() {
        page().submit();
    }

    @Quando("clico em Refresh")
    public void clicarRefresh() {
        page().refresh();
    }

    @Então("o cadastro deve ser enviado com sucesso")
    public void validarCadastroSucesso() {
        page().assertSubmitSuccess();
    }

    @Então("devo ver erro de obrigatoriedade para {string}")
    public void validarErroObrigatoriedade(String campo) {
        page().assertRequiredFieldError(campo);
    }

    @Então("devo ver mensagem de erro para o email")
    public void validarErroEmail() {
        page().assertEmailError();
    }

    @Então("devo ver mensagem de erro para o telefone")
    public void validarErroTelefone() {
        page().assertPhoneError();
    }

    @Então("devo ver mensagem de erro de confirmação de senha")
    public void validarErroSenha() {
        page().assertPasswordMismatch();
    }

    @Então("o formulário deve estar limpo")
    public void validarFormularioLimpo() {
        page().assertFormIsClean();
    }

    @Então("devo ver os hobbies selecionados corretamente")
    public void devoVerOsHobbiesSelecionadosCorretamente() {
        if (isBlank(lastHobbiesCsv)) {
            throw new IllegalStateException("Nenhum hobby foi armazenado para validação (lastHobbiesCsv está vazio).");
        }
        page().assertHobbiesSelected(lastHobbiesCsv);
    }

    @Então("devo ver os idiomas selecionados corretamente")
    public void devoVerOsIdiomasSelecionadosCorretamente() {
        if (isBlank(lastLanguagesCsv)) {
            throw new IllegalStateException("Nenhum idioma foi armazenado para validação (lastLanguagesCsv está vazio).");
        }
        page().assertLanguagesSelected(lastLanguagesCsv);
    }

    @Então("devo ver a data de nascimento selecionada corretamente")
    public void devoVerADataDeNascimentoSelecionadaCorretamente() {
        if (isBlank(lastYear) || isBlank(lastMonth) || isBlank(lastDay)) {
            throw new IllegalStateException("Data não foi armazenada para validação (year/month/day estão vazios).");
        }
        page().assertDateOfBirthSelected(lastYear, lastMonth, lastDay);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
