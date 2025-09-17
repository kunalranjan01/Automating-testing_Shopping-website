package com.myproject.tests;

import com.myproject.pages.ApiListPageFunctional;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class ApiListPageFunctionalTest {
    private static WebDriver driver;
    private static ApiListPageFunctional page;

    @BeforeClass(alwaysRun = true)
    @Parameters({"browser"})
    public void beforeClass(@Optional("chrome") String browser) {
        if ("chrome".equalsIgnoreCase(browser)) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions opts = new ChromeOptions();
            opts.addArguments("--remote-allow-origins=*");
            driver = new ChromeDriver(opts);
        } else if ("firefox".equalsIgnoreCase(browser)) {
            WebDriverManager.firefoxdriver().setup();
            driver = new org.openqa.selenium.firefox.FirefoxDriver();
        } else {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        }
        driver.manage().window().maximize();
        page = new ApiListPageFunctional(driver);
        page.open();
    }

    @Test(priority = 1, description = "API list page loads and has title")
    public void tc_api_list_page_loads() {
        Assert.assertTrue(page.isPageLoaded(), "API List page should display a title/header");
    }

    @Test(priority = 2, description = "Logo/button is present")
    public void tc_logo_present() {
        Assert.assertTrue(page.isLogoPresent(), "Logo should be present");
    }

    @Test(priority = 3, description = "Feedback section is present")
    public void tc_feedback_section() {
        Assert.assertTrue(page.isFeedbackSectionPresent(), "Feedback section should be present");
    }

    @Test(priority = 4, description = "Feedback email present")
    public void tc_feedback_email() {
        Assert.assertTrue(page.isFeedbackMailPresent("feedback@automationexercise.com"), "Feedback email should be present");
    }

    @Test(priority = 5, description = "API anchors list not empty")
    public void tc_api_anchors_not_empty() {
        Assert.assertTrue(page.getApiAnchors().size() > 0, "Expect at least one API anchor");
    }

    @Test(priority = 6, description = "Anchors have href attributes")
    public void tc_anchors_have_href() {
        Assert.assertTrue(page.areAllApiAnchorsHaveHref(), "All anchors should have href attributes (or adjust selector)");
    }

    @Test(priority = 7, description = "At least 14 API endpoints listed (best-effort)")
    public void tc_at_least_14_endpoints() {
        Assert.assertTrue(page.hasAtLeastNApiLinks(14), "Expect at least 14 API endpoints (best-effort)");
    }

    @Test(priority = 8, description = "API links open behavior detected")
    public void tc_api_links_open_behavior() {
        Assert.assertTrue(page.doApiLinksOpenBehaviorCheck(), "API links open behavior should be detectable");
    }

    @Test(priority = 9, description = "At least one anchor contains api-like href")
    public void tc_anchor_contains_api_url() {
        Assert.assertTrue(page.checkAtLeastOneAnchorContainsApiUrl(), "At least one anchor should contain api-like href");
    }

    @Test(priority = 10, description = "Verify each API entry details (best-effort)")
    public void tc_verify_each_api_entry_details() {
        Assert.assertTrue(page.verifyEachApiEntryDetails(), "Each API entry details should be visible for at least half of entries (best-effort)");
    }

    @Test(priority = 11, description = "Debug print first 10 anchors")
    public void tc_debug_print_anchors() {
        page.debugPrintAnchors(10);
        Assert.assertTrue(true);
    }

    @Test(priority = 12, description = "Footer copyright present")
    public void tc_footer_present() {
        Assert.assertTrue(page.isFooterPresent(), "Footer copyright should be present");
    }

    @Test(priority = 13, description = "Open first anchor and return to list (sanity)")
    public void tc_open_first_anchor_and_return() {
        // reuse verifyEachApiEntryDetails behavior - but do a lightweight open/return
        var anchors = page.getApiAnchors();
        if (anchors.isEmpty()) {
            Assert.assertTrue(false, "No anchors to open");
        } else {
            // attempt to open first anchor and then navigate back
            try {
                String href = anchors.get(0).getAttribute("href");
                driver.get(href);
                Assert.assertTrue(driver.getPageSource().length() > 0, "Opened first anchor page has content");
                driver.get("https://www.automationexercise.com/api_list");
                Assert.assertTrue(driver.getCurrentUrl().contains("/api_list"));
            } catch (Exception e) {
                Assert.fail("Opening first anchor failed: " + e.getMessage());
            }
        }
    }

    @Test(priority = 14, description = "Anchors contain some method keywords (best-effort)")
    public void tc_anchors_contain_method_keywords() {
        Assert.assertTrue(page.verifyEachApiEntryDetails(), "Entry pages should include method keywords for at least half entries");
    }

    @Test(priority = 15, description = "Reload api list page")
    public void tc_reload_api_list_page() {
        page.open();
        Assert.assertTrue(page.isPageLoaded());
    }

    // provide for TestListener screenshots
    public WebDriver getDriver() { return driver; }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
        }
    }
}
