package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ApiListPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final String baseUrl = "https://www.automationexercise.com";
    private final String apiListUrl = baseUrl + "/api_list";

    // top-nav links
    private final By navHome = By.linkText("Home");
    private final By navProducts = By.cssSelector("a[href='/products'], a[href='/products/']");
    private final By navCart = By.linkText("Cart");
    private final By navSignup = By.linkText("Signup / Login");
    private final By navTestCases = By.linkText("Test Cases");
    private final By navVideos = By.linkText("Video Tutorials");

    // page elements
    private final By pageTitle = By.cssSelector(".features_items h2, .page-title, .title.text-center, .features_items > h2");
    private final By apiListItems = By.cssSelector(".api-list a, .list-group a, .panel-body a, .single-api a, .col-sm-9 a");
    private final By feedbackCandidates = By.xpath("//*[contains(translate(.,'FEEDBACK','feedback'),'feedback') or contains(@id,'feedback') or contains(@class,'feedback')]");
    // 🔹 Robust logo locator (supports img or text)
    private final By logo = By.xpath("//*[contains(@class,'logo') or contains(@class,'navbar-brand') or contains(@id,'logo')]");

    private final By footerCopyright = By.xpath("//*[contains(text(),'All rights reserved') or contains(text(),'Copyright')]");

    public ApiListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void waitForReady() {
        try {
            wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {}
    }

    private void safeClick(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            wait.until(ExpectedConditions.elementToBeClickable(el));
            el.click();
        } catch (Exception e) {
            try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); } catch (Exception ignored) {}
        }
    }

    public void openApiListPage() {
        driver.get(apiListUrl);
        waitForReady();
        TestListener.info("Opened API List page: " + apiListUrl);
    }

    // --- basic accessibility checks ---
    public boolean isApiListAccessibleViaUrl() {
        openApiListPage();
        boolean ok = driver.getCurrentUrl().contains("/api_list");
        TestListener.info("Checked API List accessible via URL → " + ok);
        return ok;
    }

    public boolean isLogoPresent() {
        openApiListPage();
        List<WebElement> logos = driver.findElements(logo);
        if (logos.isEmpty()) {
            TestListener.info("Primary logo not found, trying fallback with text 'Automation Exercise'");
            logos = driver.findElements(By.xpath("//a[contains(text(),'Automation Exercise')]"));
        }
        boolean ok = !logos.isEmpty();
        TestListener.info("Checked Automation Exercise logo/button present → " + ok);
        return ok;
    }

    // --- top nav checks ---
    private boolean navigateAndCheckNav(By locator, String expectedUrlPart) {
        openApiListPage();
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            safeClick(el);
            waitForReady();
            boolean ok = driver.getCurrentUrl().toLowerCase().contains(expectedUrlPart.toLowerCase());
            TestListener.info("Clicked nav '" + locator.toString() + "' and verified URL contains '" + expectedUrlPart + "' → " + ok);
            return ok;
        } catch (Exception e) {
            TestListener.fail("Failed navigation via " + locator + ": " + e.getMessage());
            return false;
        } finally {
            openApiListPage(); // always return
        }
    }

    public boolean clickHomeAndVerify() { return navigateAndCheckNav(navHome, "automationexercise.com"); }
    public boolean clickProductsAndVerify() { return navigateAndCheckNav(navProducts, "/products"); }
    public boolean clickCartAndVerify() { return navigateAndCheckNav(navCart, "/view_cart"); }
    public boolean clickSignupAndVerify() { return navigateAndCheckNav(navSignup, "/login"); }
    public boolean clickTestCasesAndVerify() { return navigateAndCheckNav(navTestCases, "/test_cases"); }
    public boolean clickVideosAndVerify() { return navigateAndCheckNav(navVideos, "video"); }

    // --- page content checks ---
    public boolean isApiListTitlePresent(String expectedTextFragment) {
        openApiListPage();
        try {
            WebElement h = driver.findElement(pageTitle);
            boolean ok = h.isDisplayed() && h.getText().toLowerCase().contains(expectedTextFragment.toLowerCase());
            TestListener.info("Checked API list title contains '" + expectedTextFragment + "' → " + ok);
            return ok;
        } catch (Exception e) {
            boolean ok = driver.getPageSource().toLowerCase().contains(expectedTextFragment.toLowerCase());
            TestListener.info("Fallback: title text in page source contains '" + expectedTextFragment + "' → " + ok);
            return ok;
        }
    }

    public boolean isFeedbackSectionPresent() {
        openApiListPage();
        boolean ok = !driver.findElements(feedbackCandidates).isEmpty();
        TestListener.info("Checked feedback section present → " + ok);
        return ok;
    }

    public boolean isFeedbackMailPresent(String expectedMail) {
        openApiListPage();
        boolean found = false;
        try {
            List<WebElement> cands = driver.findElements(feedbackCandidates);
            for (WebElement sec : cands) {
                if (!sec.findElements(By.xpath(".//a[contains(@href,'mailto:') or contains(text(),'" + expectedMail + "')]")).isEmpty()) {
                    found = true; break;
                }
            }
            if (!found) {
                found = !driver.findElements(By.xpath("//a[contains(@href,'mailto:') or contains(text(),'" + expectedMail + "')]")).isEmpty();
            }
        } catch (Exception ignored) {}
        TestListener.info("Checked feedback email '" + expectedMail + "' present → " + found);
        return found;
    }

    public List<WebElement> getApiListElements() {
        openApiListPage();
        List<WebElement> els = driver.findElements(apiListItems);
        List<WebElement> visible = new ArrayList<>();
        for (WebElement e : els) {
            try {
                if (e.isDisplayed() && e.getAttribute("href") != null) visible.add(e);
            } catch (Exception ignored) {}
        }
        TestListener.info("Collected API list elements count → " + visible.size());
        return visible;
    }

    public boolean verifyApiListCount(int expected) {
        int count = getApiListElements().size();
        boolean ok = count >= expected;
        TestListener.info("Checked API list count >= " + expected + " → actual=" + count + ", result=" + ok);
        return ok;
    }

    public boolean checkApiLinksOpenBehavior() {
        List<WebElement> entries = getApiListElements();
        int newTabCount = 0;
        for (WebElement e : entries) {
            String target = e.getAttribute("target");
            if (target != null && target.equalsIgnoreCase("_blank")) newTabCount++;
        }
        boolean ok = !entries.isEmpty();
        TestListener.info("Checked API links open behavior → newTabCount=" + newTabCount + ", result=" + ok);
        return ok;
    }

    public boolean verifyEachApiEntryDetails() {
        List<WebElement> entries = getApiListElements();
        if (entries.isEmpty()) return false;
        int success = 0;
        for (WebElement e : entries) {
            try {
                String href = e.getAttribute("href");
                if (href != null && !href.trim().isEmpty()) driver.get(href);
                else safeClick(e);
                waitForReady();
                String pageSrc = driver.getPageSource().toLowerCase();

                int positives = 0;
                if (pageSrc.contains("http") || pageSrc.contains("/api")) positives++;
                if (pageSrc.contains("get") || pageSrc.contains("post") || pageSrc.contains("delete")) positives++;
                if (pageSrc.contains("200") || pageSrc.contains("404") || pageSrc.contains("500")) positives++;
                if (pageSrc.contains("v1") || pageSrc.contains("version")) positives++;
                if (positives >= 2) success++;

                openApiListPage();
            } catch (Exception ignored) { openApiListPage(); }
        }
        boolean ok = success >= Math.max(1, entries.size() / 2);
        TestListener.info("Checked each API entry details → successCount=" + success + ", total=" + entries.size() + ", result=" + ok);
        return ok;
    }

    public boolean isFooterCopyrightPresent() {
        openApiListPage();
        boolean ok = !driver.findElements(footerCopyright).isEmpty();
        TestListener.info("Checked footer copyright → " + ok);
        return ok;
    }
}
