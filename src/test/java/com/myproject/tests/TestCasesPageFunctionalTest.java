package com.myproject.tests;

import com.myproject.pages.TestCasesPageFunctional;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class TestCasesPageFunctionalTest {
    private static WebDriver driver;
    private static TestCasesPageFunctional page;

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
        page = new TestCasesPageFunctional(driver);
        page.open();
    }

    @Test(priority = 1, description = "Test Cases page loads")
    public void tc_page_loads() {
        Assert.assertTrue(page.isPageLoaded());
    }

    @Test(priority = 2, description = "Logo present on Test Cases page")
    public void tc_logo_present() {
        Assert.assertTrue(page.isLogoPresent());
    }

    @Test(priority = 3, description = "At least 5 testcases listed (best-effort)")
    public void tc_at_least_5_testcases() {
        Assert.assertTrue(page.isAtLeastNTestcases(5));
    }

    @Test(priority = 4, description = "Test case anchors list is not empty")
    public void tc_testcase_anchors_not_empty() {
        Assert.assertTrue(page.getTestcaseAnchors().size() > 0, "Expect at least one testcase anchor");
    }

    @Test(priority = 5, description = "First testcase opens and contains content")
    public void tc_first_testcase_open() {
        Assert.assertTrue(page.doesFirstTestcaseOpenAndHaveContent());
    }

    @Test(priority = 6, description = "Home nav from test cases works")
    public void tc_home_nav() {
        Assert.assertTrue(page.clickHomeNav(), "Home nav should work and return");
    }

    @Test(priority = 7, description = "Products nav from test cases works")
    public void tc_products_nav() {
        Assert.assertTrue(page.clickProductsNav(), "Products nav should work and return");
    }

    @Test(priority = 8, description = "Cart nav from test cases works")
    public void tc_cart_nav() {
        Assert.assertTrue(page.clickCartNav(), "Cart nav should work and return");
    }

    @Test(priority = 9, description = "Signup/Login nav from test cases works")
    public void tc_signup_nav() {
        Assert.assertTrue(page.clickSignupNav(), "Signup/Login nav should work and return");
    }

    @Test(priority = 10, description = "API nav from test cases works")
    public void tc_api_nav() {
        Assert.assertTrue(page.clickApiNav(), "API nav should work and return");
    }

    @Test(priority = 11, description = "Video Tutorials nav from test cases works")
    public void tc_videos_nav() {
        Assert.assertTrue(page.clickVideosNav(), "Videos nav should work and return");
    }

    @Test(priority = 12, description = "Contact link works")
    public void tc_contact_nav() {
        Assert.assertTrue(page.clickContactNav(), "Contact link should navigate");
    }

    @Test(priority = 13, description = "Footer is present")
    public void tc_footer_present() {
        Assert.assertTrue(page.isFooterPresent());
    }

    @Test(priority = 14, description = "Scroll-up works on Test Cases page")
    public void tc_scroll_up() {
        Assert.assertTrue(page.clickScrollUpAndVerifyTop());
    }

    @Test(priority = 15, description = "Debug print first 10 testcase anchors")
    public void tc_debug_print_anchors() {
        page.debugPrintTestcaseAnchors(10);
        Assert.assertTrue(true);
    }

    // Provide driver for TestListener screenshots
    public WebDriver getDriver() { return driver; }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            driver = null;
        }
    }
}
