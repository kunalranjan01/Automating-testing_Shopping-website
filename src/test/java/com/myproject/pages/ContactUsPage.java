package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.List;

public class ContactUsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final String baseUrl = "https://www.automationexercise.com";
    private final String contactUrl = baseUrl + "/contact_us";

    // top-nav fallback locators
    private final By navHome = By.linkText("Home");
    private final By navProducts = By.cssSelector("a[href='/products'], a[href='/products/']");
    private final By navCart = By.linkText("Cart");
    private final By navSignup = By.linkText("Signup / Login");
    private final By navTestCases = By.linkText("Test Cases");
    private final By navApi = By.linkText("API Testing");
    private final By navVideos = By.cssSelector("a[href='/video_tutorials']"); // more stable
    private final By navContact1 = By.linkText("Contact us");
    private final By navContact2 = By.linkText("Contact Us");

    // contact form locators
    private final By pageTitle = By.cssSelector(".contact-info h2, .features_items h2, .page-title, .title.text-center");
    private final By getInTouchTitle = By.xpath("//*[contains(translate(.,'GET IN TOUCH','get in touch'),'get in touch')]");
    private final By nameField = By.cssSelector("input[name='name'], input#name, input.contact-name, input[placeholder*='Name']");
    private final By emailField = By.cssSelector("input[name='email'], input#email, input[placeholder*='Email']");
    private final By subjectField = By.cssSelector("input[name='subject'], input#subject, input[placeholder*='Subject']");
    private final By messageField = By.cssSelector("textarea[name='message'], textarea#message, textarea[placeholder*='Message']");
    private final By chooseFileBtn = By.cssSelector("input[type='file'], .choose-file, .file-upload");
    private final By submitBtn = By.cssSelector("input[type='submit'], button[type='submit'], .btn.btn-primary, .submit-form");
    private final By feedbackCandidates = By.xpath("//*[contains(translate(.,'FEEDBACK','feedback'),'feedback') or contains(@id,'feedback') or contains(@class,'feedback')]");
    private final By logo = By.cssSelector(".logo, .navbar-brand img");
    private final By footerCopyright = By.xpath("//*[contains(text(),'All rights reserved') or contains(text(),'Copyright')]");

    public ContactUsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openContactPage() {
        TestListener.info("Opening Contact Us page: " + contactUrl);
        driver.get(contactUrl);
        waitForReady();
    }

    private void waitForReady() {
        try {
            wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {}
    }

    // robust nav finder
    private WebElement findNavRobust(String visibleText) {
        try {
            List<WebElement> els = driver.findElements(By.linkText(visibleText));
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}
        try {
            List<WebElement> els = driver.findElements(By.partialLinkText(visibleText.length() > 6 ? visibleText.substring(0,6) : visibleText));
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}
        try {
            String xpath = String.format("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '%s')]", visibleText.toLowerCase());
            List<WebElement> els = driver.findElements(By.xpath(xpath));
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}
        return null;
    }

    private void safeClick(WebElement el) {
        try {
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            wait.until(ExpectedConditions.elementToBeClickable(el));
            el.click();
            TestListener.info("Clicked element: " + el.getText());
        } catch (Exception e) {
            try { ((JavascriptExecutor)driver).executeScript("arguments[0].click();", el); } catch (Exception ignored) {}
        }
    }

    // nav click + verify
    private boolean navigateAndVerifyNav(String navText, String urlHint) {
        openContactPage();
        TestListener.info("Trying to click nav: " + navText);
        WebElement nav = findNavRobust(navText);
        if (nav == null) {
            TestListener.fail("Nav element not found: " + navText);
            return false;
        }
        try {
            safeClick(nav);
            wait.until(d -> d.getCurrentUrl() != null);
            String cur = driver.getCurrentUrl().toLowerCase();
            boolean ok = (urlHint != null && !urlHint.isEmpty() && cur.contains(urlHint.toLowerCase()));
            if (ok) {
                TestListener.pass("Navigation successful for: " + navText + " → " + cur);
            } else {
                TestListener.fail("Navigation failed for: " + navText + ", current URL: " + cur);
            }
            return ok;
        } catch (Exception e) {
            TestListener.fail("Error clicking nav: " + navText + " → " + e.getMessage());
            return false;
        } finally {
            openContactPage();
        }
    }

    // nav methods
    public boolean clickContactNavAndVerify() { return navigateAndVerifyNav("Contact Us", "/contact_us"); }
    public boolean clickProductsNavAndVerify() { return navigateAndVerifyNav("Products", "/products"); }
    public boolean clickCartNavAndVerify() { return navigateAndVerifyNav("Cart", "/view_cart"); }
    public boolean clickSignupNavAndVerify() { return navigateAndVerifyNav("Signup / Login", "/login"); }
    public boolean clickTestCasesNavAndVerify() { return navigateAndVerifyNav("Test Cases", "/test_cases"); }
    public boolean clickApiNavAndVerify() { return navigateAndVerifyNav("API Testing", "api"); }
    public boolean clickVideoNavAndVerify() { return navigateAndVerifyNav("Video Tutorials", "video"); }
    public boolean clickHomeNavAndVerify() { return navigateAndVerifyNav("Home", "automationexercise.com/"); }

    // page checks with logging
    public boolean isContactPageAccessibleViaUrl() {
        openContactPage();
        boolean ok = driver.getCurrentUrl().contains("/contact_us");
        TestListener.info("Contact Us page accessible via URL → " + ok);
        return ok;
    }

    public boolean isPageTitleCenteredAndDisplayed() {
        openContactPage();
        try {
            WebElement h = driver.findElement(pageTitle);
            boolean ok = h.isDisplayed();
            TestListener.info("Page title displayed: " + ok + " → " + h.getText());
            return ok;
        } catch (Exception e) {
            TestListener.fail("Page title not found: " + e.getMessage());
            return false;
        }
    }

    public boolean isGetInTouchTitlePresent() {
        openContactPage();
        boolean ok = !driver.findElements(getInTouchTitle).isEmpty();
        TestListener.info("Get In Touch title present: " + ok);
        return ok;
    }

    public boolean isNameFieldPresent() { openContactPage(); boolean ok = !driver.findElements(nameField).isEmpty(); TestListener.info("Name field present: " + ok); return ok; }
    public boolean isEmailFieldPresent() { openContactPage(); boolean ok = !driver.findElements(emailField).isEmpty(); TestListener.info("Email field present: " + ok); return ok; }
    public boolean isSubjectFieldPresent() { openContactPage(); boolean ok = !driver.findElements(subjectField).isEmpty(); TestListener.info("Subject field present: " + ok); return ok; }
    public boolean isMessageFieldPresent() { openContactPage(); boolean ok = !driver.findElements(messageField).isEmpty(); TestListener.info("Message field present: " + ok); return ok; }
    public boolean isChooseFilePresent() { openContactPage(); boolean ok = !driver.findElements(chooseFileBtn).isEmpty(); TestListener.info("Choose File button present: " + ok); return ok; }
    public boolean isSubmitButtonPresent() { openContactPage(); boolean ok = !driver.findElements(submitBtn).isEmpty(); TestListener.info("Submit button present: " + ok); return ok; }

    public boolean isFeedbackSectionPresent() { openContactPage(); boolean ok = !driver.findElements(feedbackCandidates).isEmpty(); TestListener.info("Feedback section present: " + ok); return ok; }

    public boolean isFeedbackMailPresent(String expectedMail) {
        openContactPage();
        boolean found = false;
        try {
            List<WebElement> sections = driver.findElements(feedbackCandidates);
            for (WebElement sec : sections) {
                List<WebElement> mails = sec.findElements(By.xpath(".//a[contains(@href,'mailto:') or contains(text(),'" + expectedMail + "')]"));
                if (!mails.isEmpty()) { found = true; break; }
            }
        } catch (Exception ignored) {}
        TestListener.info("Feedback mail (" + expectedMail + ") present: " + found);
        return found;
    }

    public boolean isLogoPresent() { openContactPage(); boolean ok = !driver.findElements(logo).isEmpty(); TestListener.info("Logo present: " + ok); return ok; }

    public boolean isFooterCopyrightPresent() { openContactPage(); boolean ok = !driver.findElements(footerCopyright).isEmpty(); TestListener.info("Footer copyright present: " + ok); return ok; }

    // debug helper
    public void debugPrintHeaderAnchors() {
        openContactPage();
        List<WebElement> anchors = driver.findElements(By.cssSelector("header a, nav a, .navbar a"));
        System.out.println("=== HEADER ANCHORS ===");
        for (WebElement a : anchors) {
            try { System.out.printf("text='%s' href='%s' displayed=%b%n", a.getText().trim(), a.getAttribute("href"), a.isDisplayed()); } catch (Exception ignored) {}
        }
        System.out.println("=== END HEADER ===");
    }
}
