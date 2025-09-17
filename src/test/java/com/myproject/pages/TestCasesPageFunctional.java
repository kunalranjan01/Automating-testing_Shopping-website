package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TestCasesPageFunctional {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String testCasesUrl = "https://www.automationexercise.com/test_cases";

    // --- Robust locators ---
    private final By pageTitle = By.cssSelector(".features_items h2, .page-title, .title.text-center");
    private final By testcaseAnchors = By.cssSelector(".testcase-list a, .features_items a, .single-testcase a, .col-sm-9 a");
    private final By testcaseItems = By.cssSelector(".testcase-list li, .single-testcase, .features_items .col-sm-4, .testcase");
    private final By logo = By.cssSelector(".logo, .navbar-brand img");
    private final By footer = By.cssSelector("footer, .footer");
    private final By contactLinks = By.xpath("//a[contains(translate(.,'CONTACT','contact'),'contact')]");
    private final By navProducts = By.xpath("//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'products')]");
    private final By navHome = By.xpath("//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'home')]");
    private final By navCart = By.xpath("//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cart')]");
    private final By navSignup = By.xpath("//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'signup')]");
    private final By navApi = By.xpath("//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'api')]");
    private final By navVideos = By.xpath("//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'video')]");
    private final By scrollUp = By.cssSelector("#scrollUp, .scroll-up, .back-to-top");

    public TestCasesPageFunctional(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    private void info(String msg) { TestListener.info(msg); }
    private void fail(String msg) { TestListener.fail(msg); }

    public void open() {
        info("Opening Test Cases page: " + testCasesUrl);
        driver.get(testCasesUrl);
        waitForReady();
        info("Page opened: " + driver.getCurrentUrl());
    }

    private void waitForReady() {
        try {
            wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {}
    }

    public boolean isPageLoaded() {
        info("Checking test cases page title presence");
        try {
            boolean ok = !driver.findElements(pageTitle).isEmpty();
            info("Page title present -> " + ok);
            return ok;
        } catch (Exception e) {
            fail("isPageLoaded exception: " + e.getMessage());
            return false;
        }
    }

    public boolean isLogoPresent() {
        info("Checking logo presence on Test Cases page");
        try {
            boolean ok = !driver.findElements(logo).isEmpty();
            info("Logo present -> " + ok);
            return ok;
        } catch (Exception e) {
            fail("isLogoPresent exception: " + e.getMessage());
            return false;
        }
    }

    public int getTestcaseCount() {
        info("Counting testcase items on page");
        try {
            open();
            List<WebElement> list = driver.findElements(testcaseItems);
            int count = list.size();
            info("Testcase items found: " + count);
            return count;
        } catch (Exception e) {
            fail("getTestcaseCount exception: " + e.getMessage());
            return 0;
        }
    }

    public List<WebElement> getTestcaseAnchors() {
        info("Collecting testcase anchors");
        try {
            open();
            List<WebElement> anchors = driver.findElements(testcaseAnchors);
            info("Anchors found: " + anchors.size());
            return anchors;
        } catch (Exception e) {
            fail("getTestcaseAnchors exception: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean isAtLeastNTestcases(int n) {
        int count = getTestcaseCount();
        boolean ok = count >= n;
        info("Is at least " + n + " testcases? " + ok);
        return ok;
    }

    public boolean doesFirstTestcaseOpenAndHaveContent() {
        info("Opening first testcase anchor (best-effort) and verifying content");
        try {
            List<WebElement> anchors = getTestcaseAnchors();
            if (anchors.isEmpty()) {
                fail("No testcase anchors found");
                return false;
            }
            WebElement first = anchors.get(0);
            String href = first.getAttribute("href");
            info("First anchor text='" + first.getText().trim() + "' href='" + href + "'");
            if (href != null && !href.trim().isEmpty()) {
                driver.get(href);
                waitForReady();
                boolean hasContent = driver.getPageSource().length() > 100;
                info("Opened first anchor -> page content length >100 ? " + hasContent);
                driver.get(testCasesUrl); // return
                waitForReady();
                return hasContent;
            } else {
                // try clicking
                try {
                    first.click();
                    waitForReady();
                    boolean hasContent = driver.getPageSource().length() > 100;
                    info("Clicked first anchor -> content ok? " + hasContent);
                    driver.get(testCasesUrl);
                    waitForReady();
                    return hasContent;
                } catch (Exception e) {
                    fail("Clicking first anchor failed: " + e.getMessage());
                    return false;
                }
            }
        } catch (Exception e) {
            fail("doesFirstTestcaseOpenAndHaveContent exception: " + e.getMessage());
            try { driver.get(testCasesUrl); waitForReady(); } catch (Exception ignored) {}
            return false;
        }
    }

    // Navigation link checks (click & verify fragment or url change then navigate back)
    private boolean clickNavAndVerify(By navLocator, String expectedFragment) {
        info("Click nav and verify for locator: " + navLocator + " expected fragment: " + expectedFragment);
        try {
            List<WebElement> els = driver.findElements(navLocator);
            for (WebElement e : els) {
                if (!e.isDisplayed()) continue;
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", e);
                    wait.until(ExpectedConditions.elementToBeClickable(e));
                    e.click();
                    waitForReady();
                    boolean ok = driver.getCurrentUrl().toLowerCase().contains(expectedFragment.toLowerCase())
                            || driver.getPageSource().toLowerCase().contains(expectedFragment.toLowerCase());
                    info("Nav click resulted in ok=" + ok + " currentUrl=" + driver.getCurrentUrl());
                    // return back
                    driver.get(testCasesUrl);
                    waitForReady();
                    return ok;
                } catch (Exception clickEx) {
                    // try next element
                }
            }
            info("Nav element not found or clickable: " + navLocator);
            return false;
        } catch (Exception e) {
            fail("clickNavAndVerify exception: " + e.getMessage());
            return false;
        }
    }

    public boolean clickHomeNav() { return clickNavAndVerify(navHome, "/"); }
    public boolean clickProductsNav() { return clickNavAndVerify(navProducts, "/products"); }
    public boolean clickCartNav() { return clickNavAndVerify(navCart, "/view_cart"); }
    public boolean clickSignupNav() { return clickNavAndVerify(navSignup, "/login"); }
    public boolean clickApiNav() { return clickNavAndVerify(navApi, "/api_list"); }
    public boolean clickVideosNav() { return clickNavAndVerify(navVideos, "video"); }
    public boolean clickContactNav() {
        info("Click contact nav (multiple possible locators)");
        try {
            List<WebElement> els = driver.findElements(contactLinks);
            for (WebElement e : els) {
                if (!e.isDisplayed()) continue;
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", e);
                    wait.until(ExpectedConditions.elementToBeClickable(e));
                    e.click();
                    waitForReady();
                    boolean ok = driver.getCurrentUrl().toLowerCase().contains("contact") || driver.getPageSource().toLowerCase().contains("contact");
                    driver.get(testCasesUrl); waitForReady();
                    info("Clicked contact nav -> ok=" + ok);
                    return ok;
                } catch (Exception ignored) {}
            }
            info("Contact link not found");
            return false;
        } catch (Exception e) {
            fail("clickContactNav exception: " + e.getMessage());
            return false;
        }
    }

    public boolean isFooterPresent() {
        info("Checking footer presence");
        try {
            boolean ok = !driver.findElements(footer).isEmpty();
            info("Footer present -> " + ok);
            return ok;
        } catch (Exception e) {
            fail("isFooterPresent exception: " + e.getMessage());
            return false;
        }
    }

    public boolean clickScrollUpAndVerifyTop() {
        info("Attempting scroll-up and verify top");
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
            List<WebElement> btns = driver.findElements(scrollUp);
            if (!btns.isEmpty()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btns.get(0));
            } else {
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0);");
            }
            // wait until near top
            long end = System.currentTimeMillis() + 8000;
            while (System.currentTimeMillis() < end) {
                Object off = ((JavascriptExecutor) driver).executeScript("return Math.round(window.pageYOffset);");
                int y = 0;
                if (off instanceof Number) y = ((Number) off).intValue();
                if (y <= 5) return true;
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            }
            return false;
        } catch (Exception e) {
            fail("clickScrollUpAndVerifyTop exception: " + e.getMessage());
            return false;
        }
    }

    // Debug helper
    public void debugPrintTestcaseAnchors(int limit) {
        info("DEBUG: printing testcase anchors (limit " + limit + ")");
        List<WebElement> anchors = getTestcaseAnchors();
        int i = 0;
        for (WebElement a : anchors) {
            if (i++ >= limit) break;
            try { info("ANCHOR[" + i + "] text='" + a.getText().trim() + "' href='" + a.getAttribute("href") + "'"); } catch (Exception ignored) {}
        }
    }
}
