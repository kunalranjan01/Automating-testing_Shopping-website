package com.myproject.tests;

import com.myproject.pages.ContactUsPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class ContactUsPageUITest {

    private static WebDriver driver;
    private static ContactUsPage page;

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
        page = new ContactUsPage(driver);
        page.openContactPage();
    }

    @Test(description = "Contact Us page loads correctly on Chrome")
    public void tc_contact_page_loads() {
        Assert.assertTrue(page.isContactPageAccessibleViaUrl(), "Contact Us page should load via direct URL");
    }

    // Top nav buttons (click from contact page and verify nav then return)
    @Test(description = "Contact Us nav is present & working")
    public void tc_contact_nav() {
        Assert.assertTrue(page.clickContactNavAndVerify(), "Contact nav should work");
    }

    @Test(description = "Products nav is present & working")
    public void tc_products_nav() {
        Assert.assertTrue(page.clickProductsNavAndVerify(), "Products nav should work");
    }

    @Test(description = "Cart nav is present & working")
    public void tc_cart_nav() {
        Assert.assertTrue(page.clickCartNavAndVerify(), "Cart nav should work");
    }

    @Test(description = "Signup/Login nav is present & working")
    public void tc_signup_nav() {
        Assert.assertTrue(page.clickSignupNavAndVerify(), "Signup/Login nav should work");
    }

    @Test(description = "Test Cases nav is present & working")
    public void tc_testcases_nav() {
        Assert.assertTrue(page.clickTestCasesNavAndVerify(), "Test Cases nav should work");
    }

    @Test(description = "API Testing nav is present & working")
    public void tc_api_nav() {
        Assert.assertTrue(page.clickApiNavAndVerify(), "API Testing nav should work");
    }

    @Test(description = "Video Tutorials nav is present & working")
    public void tc_video_nav() {
        Assert.assertTrue(page.clickVideoNavAndVerify(), "Video Tutorials nav should work");
    }

    @Test(description = "Home nav is present & working")
    public void tc_home_nav() {
        Assert.assertTrue(page.clickHomeNavAndVerify(), "Home nav should work");
    }

    // Page-specific UI checks
    @Test(description = "Contact Us title present in center of page")
    public void tc_contact_title_centered() {
        Assert.assertTrue(page.isPageTitleCenteredAndDisplayed(), "Contact page title should be centered and displayed");
    }

    @Test(description = "Get In Touch title present in centre of form section")
    public void tc_get_in_touch_title() {
        Assert.assertTrue(page.isGetInTouchTitlePresent(), "Get In Touch title should be present");
    }

    @Test(description = "Name field box is present")
    public void tc_name_field_present() { Assert.assertTrue(page.isNameFieldPresent()); }

    @Test(description = "Email field box is present")
    public void tc_email_field_present() { Assert.assertTrue(page.isEmailFieldPresent()); }

    @Test(description = "Subject field box is present")
    public void tc_subject_field_present() { Assert.assertTrue(page.isSubjectFieldPresent()); }

    @Test(description = "Your message field is present")
    public void tc_message_field_present() { Assert.assertTrue(page.isMessageFieldPresent()); }

    @Test(description = "Choose file button is present")
    public void tc_choose_file_present() { Assert.assertTrue(page.isChooseFilePresent()); }

    @Test(description = "Submit button is present")
    public void tc_submit_button_present() { Assert.assertTrue(page.isSubmitButtonPresent()); }

    @Test(description = "Feedback for Us section is present")
    public void tc_feedback_section_present() { Assert.assertTrue(page.isFeedbackSectionPresent()); }

    @Test(description = "feedback@automationexercise.com mail is present in feedback section")
    public void tc_feedback_mail_present() {
        Assert.assertTrue(page.isFeedbackMailPresent("feedback@automationexercise.com"), "Expected feedback email in feedback section");
    }

    @Test(description = "AutomationExercise logo/button present")
    public void tc_logo_present() { Assert.assertTrue(page.isLogoPresent()); }

    @Test(description = "Footer copyright present")
    public void tc_footer_present() { Assert.assertTrue(page.isFooterCopyrightPresent()); }

    // expose driver for TestListener screenshots
    public WebDriver getDriver() { return driver; }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            driver = null;
        }
    }
}
