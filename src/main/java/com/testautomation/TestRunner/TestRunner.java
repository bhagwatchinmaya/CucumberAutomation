package com.testautomation.TestRunner;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = ".//features/", glue = "com.testautomation.StepDef"
        ,tags = {"@Verivox"}
        ,plugin = { "html:target/cucumber-html-report" }
)
public class TestRunner {
}