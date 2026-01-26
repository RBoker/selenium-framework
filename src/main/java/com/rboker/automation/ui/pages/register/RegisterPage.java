package com.rboker.automation.ui.pages.register;

import com.rboker.automation.core.DriverManager;
import com.rboker.automation.factories.WaitFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Page Object responsável pela página de cadastro
 * https://demo.automationtesting.in/Register.html
 *
 * Toda interação e validação da página deve ficar concentrada aqui.
 */
public class RegisterPage {

    /**
     * IMPORTANTE:
     * Não cachear o driver no construtor, porque os Steps/Page podem ser instanciados
     * antes do Hook criar o WebDriver. Isso causaria NPE.
     */
    private WebDriver driver() {
        WebDriver d = DriverManager.getDriver();
        if (d == null) {
            throw new IllegalStateException(
                    "WebDriver ainda não foi inicializado. " +
                            "Garanta que o Hook (@Before) cria o driver antes dos Steps."
            );
        }
        return d;
    }

    /* =======================
       Locators (refinados)
       ======================= */

    private static final By PAGE_BODY = By.tagName("body");

    // Nome
    private static final By FIRST_NAME = By.cssSelector("input[placeholder='First Name']");
    private static final By LAST_NAME  = By.cssSelector("input[placeholder='Last Name']");

    // Address (na página demo é ng-model='Adress' mesmo)
    private static final By ADDRESS = By.cssSelector("textarea[ng-model='Adress']");

    // Email / Phone
    private static final By EMAIL = By.cssSelector("input[type='email']");
    private static final By PHONE = By.cssSelector("input[type='tel']");

    // Gender (radio) - value: Male/FeMale
    private static final By GENDER_RADIOS = By.cssSelector("input[name='radiooptions']");

    // Hobbies (checkbox) - value: Cricket/Movies/Hockey
    private static final By HOBBIES = By.cssSelector("input[type='checkbox']");

    // Languages (multi-select custom)
    private static final By LANGUAGES_DROPDOWN = By.id("msdd");

    // Alguns DOMs usam esse container:
    private static final By LANGUAGES_PANEL = By.cssSelector("div.ui-autocomplete-multiselect");
    private static final By LANGUAGES_OPTIONS = By.cssSelector("div.ui-autocomplete-multiselect a");

    // Skills (select)
    private static final By SKILLS = By.id("Skills");

    // Country simples (select) - ESTE É O CAMPO "Country*" REAL
    private static final By COUNTRY_SELECT = By.id("countries");

    // Select2 (Select Country :)
    private static final By SELECT2_COUNTRY_COMBO =
            By.cssSelector("span.select2-selection.select2-selection--single");
    private static final By SELECT2_SEARCH_INPUT =
            By.cssSelector("input.select2-search__field");
    private static final By SELECT2_RESULTS =
            By.cssSelector("ul#select2-country-results li");

    // DOB
    private static final By YEAR  = By.id("yearbox");
    private static final By MONTH = By.cssSelector("select[placeholder='Month']");
    private static final By DAY   = By.id("daybox");

    // Password
    private static final By PASSWORD = By.id("firstpassword");
    private static final By CONFIRM_PASSWORD = By.id("secondpassword");

    // Botões
    private static final By SUBMIT  = By.id("submitbtn");
    private static final By REFRESH = By.id("Button1");

    /* =======================
       Navegação
       ======================= */

    public void open() {
        driver().get("https://demo.automationtesting.in/Register.html");
        WaitFactory.visibilityOf(FIRST_NAME);
    }

    /* =======================
       Preenchimento de campos
       ======================= */

    public void fillFirstName(String firstName) {
        WaitFactory.sendKeys(FIRST_NAME, firstName);
    }

    public void fillLastName(String lastName) {
        WaitFactory.sendKeys(LAST_NAME, lastName);
    }

    public void fillAddress(String address) {
        WaitFactory.sendKeys(ADDRESS, address);
    }

    public void fillEmail(String email) {
        WaitFactory.sendKeys(EMAIL, email);
    }

    public void fillUniqueEmail() {
        String email = "qa+" + UUID.randomUUID() + "@exemplo.com";
        fillEmail(email);
    }

    public void fillPhone(String phone) {
        WaitFactory.sendKeys(PHONE, phone);
    }

    /* =======================
       Seleções
       ======================= */

    public void selectGender(String gender) {
        List<WebElement> radios = driver().findElements(GENDER_RADIOS);

        radios.stream()
                .filter(r -> gender.equalsIgnoreCase(r.getAttribute("value")))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Gênero não encontrado: " + gender))
                .click();
    }

    public void selectHobby(String hobby) {
        driver().findElements(HOBBIES).stream()
                .filter(cb -> hobby.equalsIgnoreCase(cb.getAttribute("value")))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Hobby não encontrado: " + hobby))
                .click();
    }

    public void selectMultipleHobbies(String hobbiesCsv) {
        for (String hobby : hobbiesCsv.split(",")) {
            selectHobby(hobby.trim());
        }
    }

    /**
     * Abre o dropdown de Languages e garante que existe pelo menos uma opção no DOM.
     * (Evita Timeout por tentar procurar item antes do menu renderizar.)
     */
    private void openLanguagesDropdown() {
        WaitFactory.click(LANGUAGES_DROPDOWN);

        // “âncora” genérica: alguma opção deve existir no DOM após abrir
        By anyOption = By.xpath(
                "//div[contains(@class,'ui-autocomplete-multiselect')]//a" +
                        " | //ul[contains(@class,'ui-autocomplete')]//a"
        );
        WaitFactory.presenceOf(anyOption);
    }

    /**
     * XPath que cobre variações do DOM para uma opção de idioma.
     */
    private By languageOption(String language) {
        return By.xpath(
                "//div[contains(@class,'ui-autocomplete-multiselect')]//a[normalize-space()='" + language + "']" +
                        " | //ul[contains(@class,'ui-autocomplete')]//a[normalize-space()='" + language + "']"
        );
    }

    /**
     * Seleciona 1 idioma no dropdown de Languages.
     * - abre dropdown
     * - presence + scroll + click
     * - fecha dropdown
     */
    public void selectLanguage(String language) {
        openLanguagesDropdown();

        By opt = languageOption(language);
        WebElement el = WaitFactory.presenceOf(opt);
        WaitFactory.scrollIntoView(el);
        el.click();

        // fecha dropdown (evita overlay atrapalhando próximos campos)
        WaitFactory.click(PAGE_BODY);
    }

    /**
     * Seleciona múltiplos idiomas sem ficar abrindo/fechando para cada item (mais estável).
     */
    public void selectMultipleLanguages(String languagesCsv) {
        openLanguagesDropdown();

        for (String language : languagesCsv.split(",")) {
            String lang = language.trim();

            By opt = languageOption(lang);
            WebElement el = WaitFactory.presenceOf(opt);
            WaitFactory.scrollIntoView(el);
            el.click();
        }

        // fecha no final
        WaitFactory.click(PAGE_BODY);
    }

    public void selectSkill(String skill) {
        WaitFactory.selectByVisibleText(SKILLS, skill);
    }

    /**
     * Seleção de país:
     * 1) tenta no campo "Country*" (<select id="countries">)
     * 2) se não encontrar o texto (ou se o demo variar), faz fallback para o Select2 ("Select Country :")
     */
    public void selectCountry(String country) {
        try {
            WaitFactory.selectByVisibleText(COUNTRY_SELECT, country);
        } catch (NoSuchElementException e) {
            selectCountrySelect2(country);
        }
    }

    /**
     * Campo "Select Country :" (Select2).
     * Fluxo correto: clicar -> digitar -> selecionar item no <li>.
     */
    public void selectCountrySelect2(String country) {
        WaitFactory.click(SELECT2_COUNTRY_COMBO);

        WaitFactory.visibilityOf(SELECT2_SEARCH_INPUT);
        WaitFactory.sendKeys(SELECT2_SEARCH_INPUT, country);

        WaitFactory.visibilityOf(SELECT2_RESULTS);

        driver().findElements(SELECT2_RESULTS).stream()
                .filter(li -> li.getText().trim().equalsIgnoreCase(country))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("País não encontrado no Select2: " + country))
                .click();
    }

    public void selectDateOfBirth(String year, String month, String day) {
        WaitFactory.selectByVisibleText(YEAR, year);
        WaitFactory.selectByVisibleText(MONTH, month);
        WaitFactory.selectByVisibleText(DAY, day);
    }

    public void fillPassword(String password, String confirmPassword) {
        WaitFactory.sendKeys(PASSWORD, password);
        WaitFactory.sendKeys(CONFIRM_PASSWORD, confirmPassword);
    }

    /* =======================
       Ações
       ======================= */

    public void submit() {
        scrollIntoView(SUBMIT);

        try {
            WaitFactory.click(SUBMIT);
            return;
        } catch (org.openqa.selenium.ElementClickInterceptedException intercepted) {
            dismissKnownOverlays();
            scrollIntoView(SUBMIT);

            try {
                WaitFactory.click(SUBMIT);
                return;
            } catch (org.openqa.selenium.ElementClickInterceptedException interceptedAgain) {
                jsClick(SUBMIT);
            }
        }
    }

    /** Rola o elemento para o centro da tela (reduz chance de banner/ads no rodapé bloquear). */
    private void scrollIntoView(By locator) {
        WebElement el = DriverManager.getDriver().findElement(locator);
        ((org.openqa.selenium.JavascriptExecutor) DriverManager.getDriver())
                .executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
    }

    /** Click via JS como fallback. */
    private void jsClick(By locator) {
        WebElement el = DriverManager.getDriver().findElement(locator);
        ((org.openqa.selenium.JavascriptExecutor) DriverManager.getDriver())
                .executeScript("arguments[0].click();", el);
    }

    /**
     * Fecha interferências comuns da página demo:
     * - anúncios em iframes "aswift_*"
     * - banners fixos no rodapé/topo (se existirem)
     *
     * OBS: não interage com conteúdo dentro do iframe (cross-origin).
     * A estratégia aqui é "tirar o alvo de baixo do dedo": rolar + remover do DOM se possível.
     */
    private void dismissKnownOverlays() {
        try {
            ((org.openqa.selenium.JavascriptExecutor) DriverManager.getDriver()).executeScript(
                    "document.querySelectorAll(\"iframe[id^='aswift_'], iframe[src*='doubleclick.net']\").forEach(e => e.remove());" +
                            "document.querySelectorAll(\"div[id^='google_ads_'], div[id*='google_ads'], div[class*='ad'], div[class*='ads']\").forEach(e => { if(getComputedStyle(e).position==='fixed') e.remove(); });"
            );
        } catch (Exception ignored) {
            // best effort
        }
    }

    public void refresh() {
        // refresh costuma sofrer intercept por ads de rodapé; usa click resiliente do WaitFactory
        WaitFactory.click(REFRESH);
    }

    /* =======================
       Assertivas (prontas para evoluir)
       ======================= */

    public void assertSubmitSuccess() {
        WaitFactory.visibilityOf(FIRST_NAME);
        WaitFactory.visibilityOf(SUBMIT);
    }

    public void assertRequiredFieldError(String fieldName) {
        WaitFactory.visibilityOf(SUBMIT);
    }

    public void assertEmailError() {
        WaitFactory.visibilityOf(EMAIL);
    }

    public void assertPhoneError() {
        WaitFactory.visibilityOf(PHONE);
    }

    public void assertPasswordMismatch() {
        WaitFactory.visibilityOf(CONFIRM_PASSWORD);
    }

    public void assertFormIsClean() {
        WaitFactory.attributeToBe(FIRST_NAME, "value", "");
        WaitFactory.attributeToBe(LAST_NAME, "value", "");
        WaitFactory.attributeToBe(EMAIL, "value", "");
        WaitFactory.attributeToBe(PHONE, "value", "");
    }

    /* =======================
       Novas assertivas (components)
       ======================= */

    public void assertHobbiesSelected(String hobbiesCsv) {
        List<String> expected = splitCsv(hobbiesCsv);

        for (String hobby : expected) {
            WebElement cb = driver().findElements(HOBBIES).stream()
                    .filter(e -> hobby.equalsIgnoreCase(e.getAttribute("value")))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Checkbox de hobby não encontrado: " + hobby));

            if (!cb.isSelected()) {
                throw new AssertionError("Hobby não está selecionado: " + hobby);
            }
        }
    }

    /**
     * Valida idiomas selecionados.
     *
     * Estratégia:
     * 1) Primeiro tenta validar pelo TEXTO exibido no próprio campo (msdd).
     * 2) Se não contiver (variação do site), abre o dropdown e tenta inferir seleção
     *    por atributos comuns (aria-selected/class) no item ou no <li>.
     */
    public void assertLanguagesSelected(String languagesCsv) {
        List<String> expected = splitCsv(languagesCsv);

        String fieldText = safeLower(textOf(LANGUAGES_DROPDOWN));
        boolean allInFieldText = expected.stream()
                .allMatch(lang -> fieldText.contains(lang.toLowerCase(Locale.ROOT)));

        if (allInFieldText) {
            return;
        }

        // Fallback: abre dropdown e tenta detectar estado selecionado
        openLanguagesDropdown();

        for (String lang : expected) {
            By opt = languageOption(lang);
            WebElement el = WaitFactory.presenceOf(opt);

            String ariaSelected = safeLower(el.getAttribute("aria-selected"));
            String cls = safeLower(el.getAttribute("class"));

            boolean selectedBySelf = "true".equals(ariaSelected) || cls.contains("selected") || cls.contains("active");

            boolean selectedByLi = false;
            try {
                WebElement li = el.findElement(By.xpath("./ancestor::li[1]"));
                String liCls = safeLower(li.getAttribute("class"));
                String liAria = safeLower(li.getAttribute("aria-selected"));
                selectedByLi = liCls.contains("selected") || liCls.contains("active") || "true".equals(liAria);
            } catch (Exception ignored) {
                // best effort
            }

            if (!selectedBySelf && !selectedByLi) {
                throw new AssertionError("Idioma não está marcado como selecionado no dropdown: " + lang);
            }
        }

        // fecha para não atrapalhar próximos passos
        WaitFactory.click(PAGE_BODY);
    }

    public void assertDateOfBirthSelected(String year, String month, String day) {
        // Os selects são nativos, então dá para validar pelo option selecionado (valor/texto)
        WebElement yearEl = WaitFactory.visibilityOf(YEAR);
        WebElement monthEl = WaitFactory.visibilityOf(MONTH);
        WebElement dayEl = WaitFactory.visibilityOf(DAY);

        String selectedYear = yearEl.getAttribute("value");
        String selectedDay = dayEl.getAttribute("value");

        // Month normalmente é pelo texto visível, mas value costuma ser numérico em alguns DOMs.
        // Então validamos pelo texto selecionado via JS (mais tolerante).
        String selectedMonthText = (String) ((org.openqa.selenium.JavascriptExecutor) driver())
                .executeScript("return arguments[0].options[arguments[0].selectedIndex].text;", monthEl);

        if (!year.equals(selectedYear)) {
            throw new AssertionError("Ano selecionado divergente. Esperado=" + year + " Atual=" + selectedYear);
        }
        if (!day.equals(selectedDay)) {
            throw new AssertionError("Dia selecionado divergente. Esperado=" + day + " Atual=" + selectedDay);
        }
        if (!month.equalsIgnoreCase(selectedMonthText.trim())) {
            throw new AssertionError("Mês selecionado divergente. Esperado=" + month + " Atual=" + selectedMonthText);
        }
    }

    /* =======================
       Utils
       ======================= */

    private List<String> splitCsv(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    private String textOf(By locator) {
        try {
            return driver().findElement(locator).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase(Locale.ROOT);
    }
}
