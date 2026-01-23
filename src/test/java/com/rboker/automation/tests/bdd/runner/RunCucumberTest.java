package com.rboker.automation.tests.bdd.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.rboker.automation.tests.bdd.steps",
                "com.rboker.automation.tests.bdd.hooks"
        },
        plugin = {
                "pretty",
                "html:target/cucumber-report.html",
                "json:target/cucumber-report.json"
        },
        monochrome = true
)
public class RunCucumberTest extends AbstractTestNGCucumberTests {
}
