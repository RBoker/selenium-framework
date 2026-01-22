package com.rboker.automation.tests.ui;

import com.rboker.automation.core.ScreenshotUtil;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;

/**
 * Listener do TestNG para capturar evidência automaticamente em falha.
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Path screenshot = ScreenshotUtil.capture(testName);

        if (screenshot != null) {
            // Aparece no console do IntelliJ e no log do CI
            System.out.println("[EVIDENCE] Screenshot saved at: " + screenshot.toAbsolutePath());
        }
    }
}
