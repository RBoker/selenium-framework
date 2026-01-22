package com.rboker.automation.tests.ui;

import com.rboker.automation.core.DriverManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SanityUiTest extends BaseTest {

    @Test
    public void shouldOpenBaseUrl() {
        String title = DriverManager.getDriver().getTitle();
        // Pode ser vazio dependendo do site, então validamos pelo menos que abriu e não travou
        Assert.assertNotNull(title);
    }
}
