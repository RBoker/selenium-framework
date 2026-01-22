package com.rboker.automation.tests.ui;

import com.rboker.automation.core.DriverManager;
import com.rboker.automation.tests.ui.pages.WikipediaHomePage;
import com.rboker.automation.tests.ui.pages.WikipediaResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WikipediaSearchUiTest extends BaseTest {

    @Test
    public void shouldSearchOnWikipedia() {
        WikipediaResultsPage results = new WikipediaHomePage(DriverManager.getDriver())
                .search("Selenium (software)");

        String headingText = results.heading();

        Assert.assertTrue(
                headingText != null && headingText.toLowerCase().contains("selenium"),
                "Título/Heading deveria conter 'selenium'. Heading encontrado: " + headingText
        );

        Assert.assertTrue(
                results.isWikiOrSearchUrl(),
                "URL não parece ser de resultado/página da Wikipedia. URL: " + results.currentUrl()
        );
    }
}
