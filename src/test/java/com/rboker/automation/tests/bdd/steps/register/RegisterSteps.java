package com.rboker.automation.tests.bdd.steps.register;

import com.rboker.automation.ui.pages.RegisterPage;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class RegisterSteps {

    private RegisterPage registerPage;

    // Guarda últimos valores selecionados para validação nos steps "Então"
    private String lastHobbiesCsv;
    private String lastLanguagesCsv;
    private String lastYear;
    private String lastMonth;
    private String lastDay;

    /**
     * Lazy init para evitar NPE quando os Steps são instanciados antes do Hook criar o driver.
     */
    private RegisterPage page() {
        if (registerPage == null) {
            registerPage = new RegisterPage();
        }
        return registerPage;
    }

    @Dado("que acesso a página de cadastro")
    public void acessarPaginaCadastro() {
        // garante page object “fresh” por cenário
        registerPage = null;

        // limpa cache de valores entre cenários
        lastHobbiesCsv = null;
        lastLanguagesCsv = null;
        lastYear = null;
        lastMonth = null;
        lastDay = null;

        page().open();
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
        // mantém compatibilidade e também alimenta o "lastHobbiesCsv" (unitário)
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
        // mantém compatibilidade e também alimenta o "lastLanguagesCsv" (unitário)
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

    /* =========================================================
       Novos "Então" (antes estavam Undefined)
       ========================================================= */

    @Então("devo ver os hobbies selecionados corretamente")
    public void devoVerOsHobbiesSelecionadosCorretamente() {
        if (lastHobbiesCsv == null || lastHobbiesCsv.isBlank()) {
            throw new IllegalStateException("Nenhum hobby foi armazenado para validação (lastHobbiesCsv está vazio).");
        }
        page().assertHobbiesSelected(lastHobbiesCsv);
    }

    @Então("devo ver os idiomas selecionados corretamente")
    public void devoVerOsIdiomasSelecionadosCorretamente() {
        if (lastLanguagesCsv == null || lastLanguagesCsv.isBlank()) {
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
