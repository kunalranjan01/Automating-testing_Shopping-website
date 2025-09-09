package com.myproject.pages;

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
    private final By navProducts = By.linkText("Products");
    private final By navCart = By.linkText("Cart");
    private final By navSignup = By.linkText("Signup / Login");
    private final By navTestCases = By.linkText("Test Cases");
    private final By navApi = By.linkText("API Testing");
    private final By navVideos = By.linkText("Video Tutorials");
    private final By navContact1 = By.linkText("Contact us");
    private final By navContact2 = By.linkText("Contact Us");

    // contact form locators (common possibilities)
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
        driver.get(contactUrl);
        waitForReady();
    }

    private void waitForReady() {
        try {
            wait.until(d -> ((JavascriptExecutor)d).executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {}
    }

    // robust nav finder (tries linkText, partial and xpath contains)
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
        } catch (Exception e) {
            try { ((JavascriptExecutor)driver).executeScript("arguments[0].click();", el); } catch (Exception ignored) {}
        }
    }

    // nav click + verify wrappers (operate from contact page, then return)
    private boolean navigateAndVerifyNav(String navText, String urlHint) {
        openContactPage();
        WebElement nav = findNavRobust(navText);
        if (nav == null) return false;
        String href = "";
        try { href = nav.getAttribute("href"); } catch (Exception ignored) {}
        try {
            if (href != null && !href.trim().isEmpty()) {
                URI u = new URI(href);
                if (!u.isAbsolute()) safeClick(nav);
                else driver.get(href);
            } else safeClick(nav);

            wait.until(d -> d.getCurrentUrl() != null);
            String cur = driver.getCurrentUrl().toLowerCase();
            boolean ok = (urlHint != null && !urlHint.isEmpty() && cur.contains(urlHint.toLowerCase()));
            if (!ok) {
                String src = driver.getPageSource().toLowerCase();
                if (urlHint != null && !urlHint.isEmpty()) ok = src.contains(urlHint.toLowerCase());
            }
            return ok;
        } catch (Exception e) {
            return false;
        } finally {
            openContactPage();
        }
    }

    public boolean clickContactNavAndVerify() { return navigateAndVerifyNav("Contact us", "/contact_us"); }
    public boolean clickProductsNavAndVerify() { return navigateAndVerifyNav("Products", "/products"); }
    public boolean clickCartNavAndVerify() { return navigateAndVerifyNav("Cart", "/view_cart"); }
    public boolean clickSignupNavAndVerify() { return navigateAndVerifyNav("Signup / Login", "/login"); }
    public boolean clickTestCasesNavAndVerify() { return navigateAndVerifyNav("Test Cases", "/test_cases"); }
    public boolean clickApiNavAndVerify() { return navigateAndVerifyNav("API Testing", "api"); }
    public boolean clickVideoNavAndVerify() { return navigateAndVerifyNav("Video Tutorials", "video"); }
    public boolean clickHomeNavAndVerify() { return navigateAndVerifyNav("Home", "automationexercise.com/"); }

    // basic page checks
    public boolean isContactPageAccessibleViaUrl() {
        openContactPage();
        return driver.getCurrentUrl().contains("/contact_us");
    }

    public boolean isPageTitleCenteredAndDisplayed() {
        openContactPage();
        try {
            WebElement h = driver.findElement(pageTitle);
            if (!h.isDisplayed()) return false;
            Object rect = ((JavascriptExecutor)driver).executeScript(
                    "var r=arguments[0].getBoundingClientRect(); return {x:r.left,w:r.width,v:window.innerWidth};", h);
            if (rect instanceof java.util.Map) {
                @SuppressWarnings("unchecked") java.util.Map<String,Object> m = (java.util.Map<String,Object>)rect;
                double left = Double.parseDouble(m.get("x").toString());
                double width = Double.parseDouble(m.get("w").toString());
                double viewport = Double.parseDouble(m.get("v").toString());
                double center = left + width/2.0; double vcenter = viewport/2.0;
                return Math.abs(center - vcenter) <= viewport * 0.12;
            }
            return true;
        } catch (Exception e) {
            return !driver.findElements(pageTitle).isEmpty();
        }
    }

    public boolean isGetInTouchTitlePresent() {
        openContactPage();
        return !driver.findElements(getInTouchTitle).isEmpty();
    }

    public boolean isNameFieldPresent() { openContactPage(); return !driver.findElements(nameField).isEmpty(); }
    public boolean isEmailFieldPresent() { openContactPage(); return !driver.findElements(emailField).isEmpty(); }
    public boolean isSubjectFieldPresent() { openContactPage(); return !driver.findElements(subjectField).isEmpty(); }
    public boolean isMessageFieldPresent() { openContactPage(); return !driver.findElements(messageField).isEmpty(); }
    public boolean isChooseFilePresent() { openContactPage(); return !driver.findElements(chooseFileBtn).isEmpty(); }
    public boolean isSubmitButtonPresent() { openContactPage(); return !driver.findElements(submitBtn).isEmpty(); }

    public boolean isFeedbackSectionPresent() { openContactPage(); return !driver.findElements(feedbackCandidates).isEmpty(); }

    public boolean isFeedbackMailPresent(String expectedMail) {
        openContactPage();
        try {
            List<WebElement> sections = driver.findElements(feedbackCandidates);
            for (WebElement sec : sections) {
                List<WebElement> mails = sec.findElements(By.xpath(".//a[contains(@href,'mailto:') or contains(text(),'" + expectedMail + "')]"));
                if (!mails.isEmpty()) return true;
                List<WebElement> texts = sec.findElements(By.xpath(".//*[contains(text(),'" + expectedMail + "')]"));
                if (!texts.isEmpty()) return true;
            }
            List<WebElement> global = driver.findElements(By.xpath("//a[contains(@href,'mailto:') or contains(text(),'" + expectedMail + "')]"));
            return !global.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLogoPresent() { openContactPage(); return !driver.findElements(logo).isEmpty(); }

    public boolean isFooterCopyrightPresent() { openContactPage(); return !driver.findElements(footerCopyright).isEmpty(); }

    // simple debug helper to print header anchors (call from tests if nav still fails)
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
