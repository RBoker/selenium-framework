package com.rboker.automation.tests.bdd.hooks;

import com.rboker.automation.config.FrameworkConfig;
import com.rboker.automation.core.DriverFactory;
import com.rboker.automation.core.DriverManager;
import com.rboker.automation.core.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

public class Hooks {


    @Before("@ui")
    public void beforeScenario() {
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
                ScreenshotUtil.capture(scenario.getName());
            }
        } finally {
            DriverManager.quitDriver();
        }
    }
}
