package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ContactUsPageFunctional {

    private final WebDriver driver;
    private final String contactUrl = "https://www.automationexercise.com/contact_us";

    public ContactUsPageFunctional(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        TestListener.info("ContactUsPageFunctional initialized.");
    }

    // ========== Elements ==========
    @FindBy(name = "name")
    private WebElement nameField;

    @FindBy(name = "email")
    private WebElement emailField;

    @FindBy(name = "subject")
    private WebElement subjectField;

    @FindBy(id = "message")
    private WebElement messageField;

    @FindBy(name = "upload_file")
    private WebElement uploadFileButton;

    @FindBy(name = "submit")
    private WebElement submitButton;

    @FindBy(xpath = "//div[@class='status alert alert-success']")
    private WebElement successMessage;

    @FindBy(xpath = "//a[text()=' Home']")
    private WebElement homeLink;

    // ========== Actions ==========

    /** Open Contact Us page directly */
    public void open() {
        driver.get(contactUrl);
        TestListener.info("Navigated to Contact Us page: " + contactUrl);
    }

    /** Fill contact us form */
    public boolean fillContactUsForm(String name, String email, String subject, String message) {
        try {
            nameField.clear();
            nameField.sendKeys(name);
            TestListener.info("Name entered: " + name);

            emailField.clear();
            emailField.sendKeys(email);
            TestListener.info("Email entered: " + email);

            subjectField.clear();
            subjectField.sendKeys(subject);
            TestListener.info("Subject entered: " + subject);

            messageField.clear();
            messageField.sendKeys(message);
            TestListener.info("Message entered: " + message);
            return true;
        } catch (Exception e) {
            TestListener.fail("Failed to fill form: " + e.getMessage());
            return false;
        }
    }

    /** Upload file */
    public boolean uploadFile(String filePath) {
        try {
            uploadFileButton.sendKeys(filePath);
            TestListener.info("File uploaded: " + filePath);
            return true;
        } catch (Exception e) {
            TestListener.fail("File upload failed: " + e.getMessage());
            return false;
        }
    }

    /** Click submit and accept alert */
    public void clickSubmit() {
        try {
            submitButton.click();
            TestListener.info("Clicked Submit button.");
            driver.switchTo().alert().accept();
            TestListener.info("Accepted alert after submission.");
        } catch (Exception e) {
            TestListener.fail("Submit failed: " + e.getMessage());
        }
    }

    /** Success message visible */
    public boolean isSuccessMessageDisplayed() {
        try {
            boolean visible = successMessage.isDisplayed();
            TestListener.info("Success message visible: " + visible);
            return visible;
        } catch (Exception e) {
            TestListener.info("Success message not found: " + e.getMessage());
            return false;
        }
    }

    /** Click Home link */
    public boolean clickHome() {
        try {
            homeLink.click();
            TestListener.info("Clicked Home link.");
            return true;
        } catch (Exception e) {
            TestListener.fail("Failed to click Home link: " + e.getMessage());
            return false;
        }
    }

    /** Clear all fields */
    public void clearForm() {
        try {
            nameField.clear();
            emailField.clear();
            subjectField.clear();
            messageField.clear();
            TestListener.info("Cleared all form fields.");
        } catch (Exception e) {
            TestListener.fail("Could not clear form fields: " + e.getMessage());
        }
    }
}
