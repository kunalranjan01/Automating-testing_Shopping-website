package com.myproject.tests;

import com.myproject.pages.ApiListPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class ApiListPageUITest {

    private static WebDriver driver;
    private static ApiListPage page;

    @BeforeClass(alwaysRun = true)
    @Parameters({"browser"})
    public void beforeClass(@Optional("chrome") String browser) {
        if ("edge".equalsIgnoreCase(browser)) {
            WebDriverManager.edgedriver().setup();
            driver = new org.openqa.selenium.edge.EdgeDriver();
        } else if ("firefox".equalsIgnoreCase(browser)) {
            WebDriverManager.firefoxdriver().setup();
            driver = new org.openqa.selenium.firefox.FirefoxDriver();
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions opts = new ChromeOptions();
            opts.addArguments("--remote-allow-origins=*");
            driver = new ChromeDriver(opts);
        }
        driver.manage().window().maximize();
        page = new ApiListPage(driver);
    }

    @Test(priority = 1, description = "Api_list page accessible via direct URL")
    public void tc_api_list_accessible_via_url() {
        Assert.assertTrue(page.isApiListAccessibleViaUrl());
    }

    @Test(priority = 2, description = "Home nav available and working")
    public void tc_home_nav() {
        Assert.assertTrue(page.clickHomeAndVerify());
    }

    @Test(priority = 3, description = "Products nav available and working")
    public void tc_products_nav() {
        Assert.assertTrue(page.clickProductsAndVerify());
    }

    @Test(priority = 4, description = "Cart nav available and working")
    public void tc_cart_nav() {
        Assert.assertTrue(page.clickCartAndVerify());
    }

    @Test(priority = 5, description = "Signup/Login nav available and working")
    public void tc_signup_nav() {
        Assert.assertTrue(page.clickSignupAndVerify());
    }

    @Test(priority = 6, description = "Test Cases nav available and working")
    public void tc_testcases_nav() {
        Assert.assertTrue(page.clickTestCasesAndVerify());
    }

    @Test(priority = 7, description = "Video Tutorials nav available and working")
    public void tc_videos_nav() {
        Assert.assertTrue(page.clickVideosAndVerify());
    }

    @Test(priority = 8, description = "API List title 'APIs List for practice' present in center")
    public void tc_api_list_title_present() {
        Assert.assertTrue(page.isApiListTitlePresent("apis list") || page.isApiListTitlePresent("api list") , "API list page title should mention API list");
    }

    @Test(priority = 9, description = "Feedback for us section is present")
    public void tc_feedback_section_present() {
        Assert.assertTrue(page.isFeedbackSectionPresent());
    }

    @Test(priority = 10, description = "feedback@automationexercise.com present in feedback section")
    public void tc_feedback_mail_present() {
        Assert.assertTrue(page.isFeedbackMailPresent("feedback@automationexercise.com"));
    }

    @Test(priority = 11, description = "Automation Exercise logo/button is present")
    public void tc_logo_present() {
        Assert.assertTrue(page.isAutomationExerciseButtonPresent());
    }

    @Test(priority = 12, description = "API list page loads with endpoints listed")
    public void tc_api_list_loads_with_endpoints() {
        Assert.assertTrue(page.getApiListElements().size() > 0, "Expect at least one API endpoint listed");
    }

    @Test(priority = 13, description = "All 14 API endpoints are displayed on the API list page")
    public void tc_14_api_endpoints_listed() {
        Assert.assertTrue(page.verifyApiListCount(14), "Expected at least 14 API endpoints (adjust selector if site differs)");
    }

    @Test(priority = 14, description = "API links open correctly (same tab or new tab)")
    public void tc_api_links_open_behavior() {
        Assert.assertTrue(page.checkApiLinksOpenBehavior(), "API link open behavior detected");
    }

    @Test(priority = 15, description = "Verify each API entry shows URL, request method, request code, and version info (best-effort)")
    public void tc_each_api_entry_details() {
        Assert.assertTrue(page.verifyEachApiEntryDetails(), "Each API entry should show URL/method/code/version details (best-effort)");
    }

    @Test(priority = 16, description = "API version numbers (if any) displayed")
    public void tc_api_versions_displayed() {
        // verifyEachApiEntryDetails already checks for version presence — we reuse it (best-effort)
        Assert.assertTrue(page.verifyEachApiEntryDetails());
    }

    @Test(priority = 17, description = "Footer copyright shown")
    public void tc_footer_copyright() {
        Assert.assertTrue(page.isFooterCopyrightPresent());
    }

    // Provide getDriver for your TestListener (screenshots)
    public WebDriver getDriver() {
        return driver;
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            driver = null;
        }
    }
}
