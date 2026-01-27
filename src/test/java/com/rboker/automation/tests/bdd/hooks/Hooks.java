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

    private enum ScreenshotMode {
        NONE,
        FAILED_ONLY,
        EACH_STEP,
        EACH_SCENARIO;

        static ScreenshotMode from(String raw) {
            if (raw == null) return FAILED_ONLY;
            try {
                return ScreenshotMode.valueOf(raw.trim().toUpperCase());
            } catch (Exception ignored) {
                return FAILED_ONLY;
            }
        }
    }

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
     * Screenshot após CADA step (se configurado).
     */
    @AfterStep("@ui")
    public void afterEachStep(Scenario scenario) {
        ScreenshotMode mode = ScreenshotMode.from(FrameworkConfig.screenshotMode());
        if (mode != ScreenshotMode.EACH_STEP) return;

        captureAndAttach("Step Screenshot", scenario);
    }

    /**
     * Screenshot no fim do cenário (se configurado) e/ou em falha (se configurado).
     */
    @After("@ui")
    public void afterScenario(Scenario scenario) {
        try {
            ScreenshotMode mode = ScreenshotMode.from(FrameworkConfig.screenshotMode());

            boolean failed = scenario != null && scenario.isFailed();

            boolean shouldCapture =
                    (mode == ScreenshotMode.EACH_SCENARIO) ||
                            (mode == ScreenshotMode.FAILED_ONLY && failed);

            if (shouldCapture) {
                captureAndAttach(failed ? "Screenshot - Falha" : "Screenshot - Cenário", scenario);
            }

            // Mantém evidência em arquivo APENAS se configurado e em falha (boa prática)
            if (failed && FrameworkConfig.screenshotSaveToFile()) {
                ScreenshotUtil.capture(scenario.getName());
            }
        } finally {
            DriverManager.quitDriver();
        }
    }

    private void captureAndAttach(String label, Scenario scenario) {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null || !(driver instanceof TakesScreenshot)) return;

            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            if (FrameworkConfig.screenshotAttachToAllure()) {
                try {
                    String name = (scenario != null ? scenario.getName() : "Scenario");
                    Allure.addAttachment(label + " - " + name, "image/png", new ByteArrayInputStream(png), ".png");
                } catch (Exception ignored) {
                    // best-effort: Allure pode dizer "no test is running"
                }
            }
        } catch (Exception ignored) {
            // best-effort
        }
    }
}
