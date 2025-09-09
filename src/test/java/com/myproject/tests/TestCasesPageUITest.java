package com.myproject.tests;

import com.myproject.pages.TestCasesPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class TestCasesPageUITest {

    private static WebDriver driver;
    private static TestCasesPage page;

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
        page = new TestCasesPage(driver);
        page.openTestCasesPage();
    }

    @Test(description = "Home icon available and working in Test Cases page")
    public void tc_home_icon() {
        Assert.assertTrue(page.clickHomeAndVerifyThenBack(), "Home nav should work");
    }

    @Test(description = "Products icon available and working in Test Cases page")
    public void tc_products_icon() {
        Assert.assertTrue(page.clickProductsAndVerifyThenBack(), "Products nav should work");
    }

    @Test(description = "Cart icon available and working in Test Cases page")
    public void tc_cart_icon() {
        Assert.assertTrue(page.clickCartAndVerifyThenBack(), "Cart nav should work");
    }

    @Test(description = "Signup/Login icon available and working in Test Cases page")
    public void tc_signup_icon() {
        Assert.assertTrue(page.clickSignupLoginAndVerifyThenBack(), "Signup/Login nav should work");
    }

    @Test(description = "Test Cases icon available and working in Test Cases page")
    public void tc_testcases_icon() {
        Assert.assertTrue(page.clickTestCasesAndVerifyThenBack(), "Test Cases nav should work");
    }

    @Test(description = "API Testing icon available and working in Test Cases page")
    public void tc_api_icon() {
        Assert.assertTrue(page.clickApiTestingAndVerifyThenBack(), "API Testing nav should work");
    }

    @Test(description = "Video Tutorials icon available and working in Test Cases page")
    public void tc_videos_icon() {
        Assert.assertTrue(page.clickVideoTutorialsAndVerifyThenBack(), "Video Tutorials nav should work");
    }

    @Test(description = "Contact Us icon available and working in Test Cases page")
    public void tc_contactus_icon() {
        Assert.assertTrue(page.clickContactUsAndVerifyThenBack(), "Contact Us nav should work");
    }

    @Test(description = "Site logo is displayed on Test Cases page")
    public void tc_logo_displayed() {
        Assert.assertTrue(page.isLogoDisplayed(), "Logo should be displayed");
    }

    @Test(description = "Test Cases heading is centered and displayed")
    public void tc_heading_centered() {
        Assert.assertTrue(page.isHeadingCenteredAndDisplayed(), "Heading should be centered (best-effort)");
    }

    @Test(description = "All 26 test case titles are listed on Test Cases page")
    public void tc_all_26_testcase_titles_listed() {
        Assert.assertTrue(page.verifyTestCaseCount(26), "Expected at least 26 test case titles listed");
    }

    @Test(description = "Each test case title is clickable and navigates to details")
    public void tc_each_title_clickable_navigates() {
        Assert.assertTrue(page.verifyEachTestCaseTitleNavigates(), "Test case titles should navigate (best-effort)");
    }

    @Test(description = "Feedback for Us section is displayed on the Test Cases page")
    public void tc_feedback_section_displayed() {
        Assert.assertTrue(page.isFeedbackSectionDisplayed(), "Feedback section should be displayed");
    }

    @Test(description = "A mail is mentioned in the feedback section")
    public void tc_mail_in_feedback() {
        Assert.assertTrue(page.isMailMentionedInFeedback(), "Feedback section should contain an email address (mail or text)");
    }

    @Test(description = "Clicking the upside arrow scrolls page to top")
    public void tc_scroll_up_button() {
        Assert.assertTrue(page.clickScrollUpAndVerifyTop(), "Scroll-up button should scroll to top");
    }

    @Test(description = "Footer copyright text is present on Test Cases page")
    public void tc_footer_copyright() {
        Assert.assertTrue(page.isFooterCopyrightPresent(), "Footer copyright should be present");
    }

    // expose driver for your TestListener (screenshots)
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
