package com.rboker.automation.tests.ui;

import com.rboker.automation.config.FrameworkConfig;
import com.rboker.automation.core.DriverFactory;
import com.rboker.automation.core.DriverManager;
import com.rboker.automation.core.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * BaseTest para testes UI.
 * - Cria driver por teste
 * - (Opcional) Abre baseUrl se configurada via -DbaseUrl
 * - Garante encerramento do driver SEMPRE
 * - Em falha, captura evidência
 */
public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriver driver = DriverFactory.createDriver();
        DriverManager.setDriver(driver);

        // Se baseUrl foi informada (-DbaseUrl=...), abre automaticamente.
        String baseUrl = safeBaseUrl();
        if (baseUrl != null) {
            driver.get(baseUrl);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (result != null && !result.isSuccess()) {
                ScreenshotUtil.capture(result.getName());
            }
        } finally {
            DriverManager.quitDriver();
        }
    }

    private String safeBaseUrl() {
        try {
            String url = FrameworkConfig.baseUrl(); // se não existir no seu Config, me diga o método equivalente
            return (url == null || url.isBlank()) ? null : url;
        } catch (Exception ignored) {
            // Se seu Config ainda não tem baseUrl(), não quebra a execução.
            return null;
        }
    }
}
