package com.rboker.automation.tests.bdd.steps;

import com.rboker.automation.core.DriverManager;
import com.rboker.automation.tests.ui.pages.WikipediaHomePage;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class WikipediaSteps {

    @Quando("eu pesquiso por {string}")
    public void eu_pesquiso_por(String termo) {
        WebDriver driver = DriverManager.getDriver();
        new WikipediaHomePage(driver).search(termo);
    }

    @Entao("devo ver resultados relacionados a {string}")
    public void devo_ver_resultados_relacionados_a(String termo) {
        WebDriver driver = DriverManager.getDriver();

        String title = driver.getTitle();
        Assert.assertTrue(title.toLowerCase().contains(termo.toLowerCase()),
                "Título não contém o termo esperado. Title=" + title);
    }
}
