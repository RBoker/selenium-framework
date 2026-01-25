package com.rboker.automation.tests.bdd.hooks;

import com.rboker.automation.config.FrameworkConfig;
import com.rboker.automation.factories.DriverFactory;
import com.rboker.automation.core.DriverManager;
import com.rboker.automation.core.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.rboker.automation.support.AllureEnvironmentWriter;

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


    @After("@ui")
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario != null && scenario.isFailed()) {

                // 1) Mantém sua evidência em arquivo (como já está hoje)
                ScreenshotUtil.capture(scenario.getName());

                // 2) Adiciona evidência no Allure (fica embutido no relatório)
                WebDriver driver = DriverManager.getDriver();
                if (driver != null) {
                    byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    Allure.addAttachment(
                            "Screenshot - Falha: " + scenario.getName(),
                            "image/png",
                            new ByteArrayInputStream(png),
                            ".png"
                    );
                }
            }
        } finally {
            DriverManager.quitDriver();
        }
    }

}
