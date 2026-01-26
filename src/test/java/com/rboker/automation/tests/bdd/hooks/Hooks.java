package com.rboker.automation.tests.bdd.hooks;

import com.rboker.automation.config.FrameworkConfig;
import com.rboker.automation.core.DriverManager;
import com.rboker.automation.core.ScreenshotUtil;
import com.rboker.automation.factories.DriverFactory;
import com.rboker.automation.support.AllureEnvironmentWriter;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

public class Hooks {

    @Before("@ui")
    public void beforeScenario() {
        AllureEnvironmentWriter.writeOnce();

        WebDriver driver = DriverFactory.createDriver();
        DriverManager.setDriver(driver);

        String baseUrl = FrameworkConfig.baseUrl();
        if (baseUrl != null && !baseUrl.isBlank()) {
            driver.get(baseUrl);
        }
    }

    /**
     * Captura screenshot após CADA step e anexa no Allure.
     * Best-effort: não deve quebrar execução se falhar.
     */
    @AfterStep("@ui")
    public void afterEachStep(Scenario scenario) {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null || !(driver instanceof TakesScreenshot)) {
                return;
            }

            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            // Nome simples e consistente no relatório
            String stepLabel = (scenario != null ? scenario.getName() : "Scenario");

            try {
                Allure.addAttachment(
                        "Step Screenshot - " + stepLabel,
                        "image/png",
                        new ByteArrayInputStream(png),
                        ".png"
                );
            } catch (Exception ignored) {
                // best-effort: Allure pode dizer "no test is running"
            }
        } catch (Exception ignored) {
            // best-effort: screenshot pode falhar em alguns drivers/ambientes
        }
    }

    @After("@ui")
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario != null && scenario.isFailed()) {

                // 1) Mantém sua evidência em arquivo (como já está hoje)
                ScreenshotUtil.capture(scenario.getName());

                // 2) Adiciona evidência no Allure (fica embutido no relatório) - com proteções
                WebDriver driver = DriverManager.getDriver();
                if (driver != null && driver instanceof TakesScreenshot) {
                    try {
                        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

                        try {
                            Allure.addAttachment(
                                    "Screenshot - Falha: " + scenario.getName(),
                                    "image/png",
                                    new ByteArrayInputStream(png),
                                    ".png"
                            );
                        } catch (Exception ignored) {
                            // best-effort: não deixamos o teardown falhar por conta do Allure
                        }
                    } catch (Exception ignored) {
                        // best-effort: screenshot pode falhar em alguns drivers/ambientes
                    }
                }
            }
        } finally {
            DriverManager.quitDriver();
        }
    }
}
