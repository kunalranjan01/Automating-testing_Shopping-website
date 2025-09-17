package com.myproject.tests;

import com.myproject.base.BaseTest;
import com.myproject.listeners.TestListener;
import com.myproject.pages.ContactUsPageFunctional;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ContactUsPageFunctionalTest extends BaseTest {

    private ContactUsPageFunctional contactPage;

    @BeforeMethod(alwaysRun = true)
    public void setUpPages() {
        contactPage = new ContactUsPageFunctional(getDriver());
        TestListener.info("Navigating to Contact Us page.");
        contactPage.open();
        TestListener.info("On Contact Us page.");
        contactPage.clearForm(); // reset for each test
    }

    @Test(priority = 1, description = "Submit contact form successfully with valid data")
    public void testCase01_submitContactUsFormSuccessfully() {
        TestListener.info("Executing testCase01_submitContactUsFormSuccessfully.");
        boolean filled = contactPage.fillContactUsForm("John Doe", "john.doe@test.com", "Test Subject", "This is a test message.");
        Assert.assertTrue(filled, "Form should be filled without errors");
        contactPage.clickSubmit();

        boolean isSuccess = contactPage.isSuccessMessageDisplayed();
        if (isSuccess) {
            TestListener.pass("Success message is displayed. Test passed.");
        } else {
            TestListener.fail("Success message is NOT displayed. Test failed.");
        }
        Assert.assertTrue(isSuccess, "Success message should be displayed after valid submission");
    }

    @Test(priority = 2, description = "Contact form submission fails with an invalid email format")
    public void testCase02_submitWithInvalidEmailFormat() {
        TestListener.info("Executing testCase02_submitWithInvalidEmailFormat.");
        contactPage.fillContactUsForm("Jane Doe", "invalid-email", "Invalid Email Test", "Testing invalid email.");
        contactPage.clickSubmit();

        boolean isSuccess = contactPage.isSuccessMessageDisplayed();
        if (isSuccess) {
            TestListener.fail("Success message shown for invalid email. Test failed.");
        } else {
            TestListener.pass("No success message for invalid email. Test passed.");
        }
        Assert.assertFalse(isSuccess, "Success message should NOT be displayed for invalid email format");
    }

    @Test(priority = 3, description = "Contact form submission fails with empty name field")
    public void testCase03_submitWithEmptyName() {
        TestListener.info("Executing testCase03_submitWithEmptyName.");
        contactPage.fillContactUsForm("", "test@test.com", "Empty Name Test", "Testing empty name.");
        contactPage.clickSubmit();

        boolean isSuccess = contactPage.isSuccessMessageDisplayed();
        if (isSuccess) {
            TestListener.fail("Success message displayed for empty name. Test failed.");
        } else {
            TestListener.pass("No success message for empty name. Test passed.");
        }
        Assert.assertFalse(isSuccess, "Success message should NOT be displayed for empty name");
    }

    @Test(priority = 4, description = "Contact form submission fails with empty message field")
    public void testCase04_submitWithEmptyMessage() {
        TestListener.info("Executing testCase04_submitWithEmptyMessage.");
        contactPage.fillContactUsForm("Test User", "test@test.com", "Empty Message Test", "");
        contactPage.clickSubmit();

        boolean isSuccess = contactPage.isSuccessMessageDisplayed();
        if (isSuccess) {
            TestListener.fail("Success message displayed for empty message. Test failed.");
        } else {
            TestListener.pass("No success message for empty message. Test passed.");
        }
        Assert.assertFalse(isSuccess, "Success message should NOT be displayed for empty message");
    }

    @Test(priority = 5, description = "File upload functionality on contact form")
    public void testCase05_uploadFileToContactForm() {
        TestListener.info("Executing testCase05_uploadFileToContactForm.");
        String filePath = System.getProperty("user.dir") + "/src/test/resources/testfile.txt";
        contactPage.fillContactUsForm("File Uploader", "file@upload.com", "File Upload Test", "Testing file upload.");
        boolean uploaded = contactPage.uploadFile(filePath);
        TestListener.info("Upload attempted: " + uploaded);
        contactPage.clickSubmit();

        boolean isSuccess = contactPage.isSuccessMessageDisplayed();
        if (isSuccess) {
            TestListener.pass("File uploaded and success message is displayed. Test passed.");
        } else {
            TestListener.fail("File upload failed or success message NOT displayed. Test failed.");
        }
        Assert.assertTrue(isSuccess, "File upload should succeed and success message displayed (or adjust test if file not required)");
    }

    @Test(priority = 6, description = "Submit with only required fields")
    public void testCase06_submitWithOnlyRequiredFields() {
        TestListener.info("Executing testCase06_submitWithOnlyRequiredFields.");
        contactPage.fillContactUsForm("Required User", "required@test.com", "Required Fields Test", "This is a message.");
        contactPage.clickSubmit();

        boolean isSuccess = contactPage.isSuccessMessageDisplayed();
        if (isSuccess) {
            TestListener.pass("Form submitted successfully with only required fields. Test passed.");
        } else {
            TestListener.fail("Form submission failed with only required fields. Test failed.");
        }
        Assert.assertTrue(isSuccess, "Form should submit successfully with required fields");
    }

    @Test(priority = 7, description = "Submit and then navigate to home page")
    public void testCase07_submitAndNavigateHome() {
        TestListener.info("Executing testCase07_submitAndNavigateHome.");
        contactPage.fillContactUsForm("Navigate Home User", "home@test.com", "Navigation Test", "This is a navigation test.");
        contactPage.clickSubmit();

        // attempt to navigate home via POM
        boolean clicked = contactPage.clickHome();
        TestListener.info("Home link clicked => " + clicked);

        String currentTitle = getDriver().getTitle();
        if ("Automation Exercise".equals(currentTitle)) {
            TestListener.pass("Successfully navigated to Home Page. Test passed.");
        } else {
            TestListener.fail("Failed to navigate to Home Page. Current title: " + currentTitle);
        }
        Assert.assertEquals(currentTitle, "Automation Exercise", "Title should match Home Page after navigation.");
    }

    @Test(priority = 8, description = "Fields are cleared after a successful submission (verified by reload)")
    public void testCase08_fieldsAreClearedAfterSubmission() {
        TestListener.info("Executing testCase08_fieldsAreClearedAfterSubmission.");
        contactPage.fillContactUsForm("Clear User", "clear@test.com", "Clear Test", "This should clear.");
        contactPage.clickSubmit();

        TestListener.info("Re-navigating to Contact Us page to verify fields are empty.");
        getDriver().get(getBaseURL() + "/contact_us"); // using helper getBaseURL() method if you have; else replace with ConfigReader or hardcoded
        // small wait for page to load
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        String nameValue = getDriver().findElement(By.name("name")).getAttribute("value").trim();
        String emailValue = getDriver().findElement(By.name("email")).getAttribute("value").trim();

        if (nameValue.isEmpty() && emailValue.isEmpty()) {
            TestListener.pass("Name and email fields are empty after reload. Test passed.");
        } else {
            TestListener.fail("Name or email fields are not empty after reload. name='" + nameValue + "' email='" + emailValue + "'");
        }
        Assert.assertEquals(nameValue, "", "Name field should be empty after reload");
        Assert.assertEquals(emailValue, "", "Email field should be empty after reload");
    }

    @Test(priority = 9, description = "Verify the page title is correct")
    public void testCase09_verifyPageTitle() {
        TestListener.info("Executing testCase09_verifyPageTitle.");
        String pageTitle = getDriver().getTitle();
        String expected = "Automation Exercise - Contact Us";
        if (expected.equals(pageTitle)) {
            TestListener.pass("Correct page title found. Test passed.");
        } else {
            TestListener.fail("Incorrect page title. Found: " + pageTitle);
        }
        Assert.assertEquals(pageTitle, expected, "Page title should be '" + expected + "'");
    }

    @Test(priority = 10, description = "Verify 'GET IN TOUCH' heading is displayed")
    public void testCase10_verifyGetInTouchHeading() {
        TestListener.info("Executing testCase10_verifyGetInTouchHeading.");
        boolean isDisplayed = getDriver().findElement(By.cssSelector(".contact-form > .title, .contact-form .title")).isDisplayed();
        if (isDisplayed) {
            TestListener.pass("'GET IN TOUCH' heading is displayed. Test passed.");
        } else {
            TestListener.fail("'GET IN TOUCH' heading is not displayed. Test failed.");
        }
        Assert.assertTrue(isDisplayed, "'GET IN TOUCH' heading should be displayed.");
    }

    // helper to retrieve base URL from config or fallback
    private String getBaseURL() {
        try {
            java.lang.reflect.Method m = BaseTest.class.getDeclaredMethod("getDriver");
            // not actually used; just to satisfy static analysis - real base url read from config if available
        } catch (Exception ignored) {}
        // fallback to config reader if available, otherwise default
        try {
            Class<?> cr = Class.forName("com.myproject.config.ConfigReader");
            java.lang.reflect.Method gm = cr.getMethod("get", String.class);
            Object v = gm.invoke(null, "base.url");
            if (v != null) return v.toString().replaceAll("/$", "");
        } catch (Exception ignored) {}
        return "https://www.automationexercise.com";
    }
}
