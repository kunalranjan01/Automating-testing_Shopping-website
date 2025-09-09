package com.myproject.tests;

import com.myproject.base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SampleSmokeTest extends BaseTest {

    @Test
    public void homePageLoads() {
        String title = getDriver().getTitle();
        System.out.println("Page title: " + title);
        // Basic assertion: title not empty
        Assert.assertTrue(title != null && title.length() > 0, "Expected non-empty page title");

        // Basic element check - site layout may change; this is a soft check
        boolean logoPresent = getDriver().findElements(By.cssSelector(".logo")).size() > 0;
        System.out.println("Logo present? " + logoPresent);
    }
}
