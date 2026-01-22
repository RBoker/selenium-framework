package com.rboker.automation.tests.debug;

import com.rboker.automation.tests.ui.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailingEvidenceUiTest extends BaseTest {

    @Test
    public void shouldCaptureScreenshotOnFailure() {
        // Falha proposital para testar evidência
        Assert.fail("Falha proposital para validar screenshot.");
    }
}
