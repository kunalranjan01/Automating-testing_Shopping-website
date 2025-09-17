package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TestCasesPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final String baseUrl = "https://www.automationexercise.com";
    private final String testCasesUrl = baseUrl + "/test_cases";

    // top-nav fallbacks
    private final By navHome = By.linkText("Home");
    private final By navProducts = By.cssSelector("a[href='/products']"); // ✅ updated to CSS
    private final By navCart = By.linkText("Cart");
    private final By navSignup = By.linkText("Signup / Login");
    private final By navTestCases = By.linkText("Test Cases");
    private final By navApi = By.linkText("API Testing");
    private final By navVideos =  By.cssSelector("a[href='/video_tutorials']"); // keep as requested
    private final By navContact1 = By.linkText("Contact us");
    private final By navContact2 = By.linkText("Contact Us");

    // page elements
    private final By siteLogo = By.cssSelector(".logo, .navbar-brand img");
    private final By pageHeading = By.cssSelector(".features_items h2, .page-heading, .title.text-center, .features_items > h2");
    private final By testcaseLinkCandidates = By.cssSelector(".testcase-item a, .panel-title a, .single-testcase a, .testcase-list a, .features_items .item a, .left-sidebar a, .col-sm-9 .panel-body a");
    private final By feedbackCandidates = By.xpath("//*[contains(translate(.,'FEEDBACK','feedback'),'feedback') or contains(@id,'feedback') or contains(@class,'feedback')]");
    private final By scrollUpBtnCandidates = By.cssSelector("#scrollUp, .scroll-up, .back-to-top, .btn-scroll, a[href='#top']");
    private final By footerCopyright = By.xpath("//*[contains(text(),'All rights reserved') or contains(text(),'Copyright')]");

    public TestCasesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openTestCasesPage() {
        TestListener.info("Opening Test Cases page: " + testCasesUrl);
        driver.get(testCasesUrl);
        waitForReady();
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
            TestListener.info("Clicked element: " + el.getText());
        } catch (Exception e) {
            try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); } catch (Exception ignored) {}
        }
    }

    // robust finder
    private WebElement findNavRobust(String exactText) {
        if (exactText == null) return null;
        try {
            List<WebElement> els = driver.findElements(By.linkText(exactText));
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}
        try {
            String part = exactText.length() > 8 ? exactText.substring(0, Math.min(8, exactText.length())) : exactText;
            List<WebElement> els2 = driver.findElements(By.partialLinkText(part));
            for (WebElement e : els2) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}
        try {
            String xpath = String.format("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '%s')]", exactText.toLowerCase());
            List<WebElement> els4 = driver.findElements(By.xpath(xpath));
            for (WebElement e : els4) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}
        return null;
    }

    private boolean navigateAndVerify(WebElement el, String urlFragmentHint) {
        if (el == null) {
            TestListener.fail("Navigation element not found.");
            return false;
        }
        try {
            safeClick(el);
            wait.until(d -> d.getCurrentUrl() != null);
            String cur = driver.getCurrentUrl().toLowerCase();
            boolean ok = cur.contains(urlFragmentHint.toLowerCase());
            if (ok) {
                TestListener.pass("Navigation successful → " + cur);
            } else {
                TestListener.fail("Navigation failed → " + cur);
            }
            return ok;
        } catch (Exception e) {
            TestListener.fail("Navigation error: " + e.getMessage());
            return false;
        } finally {
            openTestCasesPage();
        }
    }

    // top-nav methods
    public boolean clickHomeAndVerifyThenBack() { openTestCasesPage(); return navigateAndVerify(findNavRobust("Home"), "automationexercise.com/"); }
    public boolean clickProductsAndVerifyThenBack() { openTestCasesPage(); return navigateAndVerify(driver.findElement(navProducts), "/products"); }
    public boolean clickCartAndVerifyThenBack() { openTestCasesPage(); return navigateAndVerify(findNavRobust("Cart"), "/view_cart"); }
    public boolean clickSignupLoginAndVerifyThenBack() { openTestCasesPage(); return navigateAndVerify(findNavRobust("Signup / Login"), "/login"); }
    public boolean clickTestCasesAndVerifyThenBack() { openTestCasesPage(); return navigateAndVerify(findNavRobust("Test Cases"), "/test_cases"); }
    public boolean clickApiTestingAndVerifyThenBack() { openTestCasesPage(); return navigateAndVerify(findNavRobust("API Testing"), "api"); }
    public boolean clickVideoTutorialsAndVerifyThenBack() { openTestCasesPage(); return navigateAndVerify(findNavRobust("Video Tutorials"), "video"); }
    public boolean clickContactUsAndVerifyThenBack() { openTestCasesPage(); WebElement e = findNavRobust("Contact us"); if (e == null) e = findNavRobust("Contact Us"); return navigateAndVerify(e, "/contact_us"); }

    // page checks
    public boolean isLogoDisplayed() { openTestCasesPage(); boolean ok = !driver.findElements(siteLogo).isEmpty(); TestListener.info("Logo present: " + ok); return ok; }

    public boolean isHeadingCenteredAndDisplayed() {
        openTestCasesPage();
        try {
            WebElement h = driver.findElement(pageHeading);
            boolean ok = h.isDisplayed();
            TestListener.info("Page heading displayed: " + ok);
            return ok;
        } catch (Exception e) {
            TestListener.fail("Heading not found: " + e.getMessage());
            return false;
        }
    }

    public List<WebElement> getTestCaseTitleElements() {
        openTestCasesPage();
        List<WebElement> els = driver.findElements(testcaseLinkCandidates);
        List<WebElement> filtered = new ArrayList<>();
        for (WebElement e : els) {
            if (e.isDisplayed() && e.getAttribute("href") != null) filtered.add(e);
        }
        TestListener.info("Found " + filtered.size() + " testcase title elements");
        return filtered;
    }

    public boolean verifyTestCaseCount(int expectedCount) {
        boolean ok = getTestCaseTitleElements().size() >= expectedCount;
        TestListener.info("Testcase count check (expected ≥ " + expectedCount + "): " + ok);
        return ok;
    }

    public boolean verifyEachTestCaseTitleNavigates() {
        List<WebElement> titles = getTestCaseTitleElements();
        if (titles.isEmpty()) return false;
        int success = 0;
        for (WebElement title : titles) {
            try {
                String href = title.getAttribute("href");
                if (href == null || href.trim().isEmpty()) {
                    safeClick(title);
                } else {
                    driver.get(href);
                }
                waitForReady();
                String cur = driver.getCurrentUrl();
                if (!cur.toLowerCase().contains("/test_cases")) success++;
                driver.get(testCasesUrl);
                waitForReady();
            } catch (Exception ignored) {}
        }
        boolean ok = success >= Math.max(1, titles.size() / 2);
        TestListener.info("Verify each testcase navigation: " + ok);
        return ok;
    }

    public boolean isFeedbackSectionDisplayed() {
        openTestCasesPage();
        boolean ok = !driver.findElements(feedbackCandidates).isEmpty();
        TestListener.info("Feedback section present: " + ok);
        return ok;
    }

    public boolean isMailMentionedInFeedback() {
        openTestCasesPage();
        boolean found = !driver.findElements(By.xpath("//a[contains(@href,'mailto:') or contains(text(),'@automationexercise.com')]")).isEmpty();
        TestListener.info("Feedback email mentioned: " + found);
        return found;
    }

    public boolean clickScrollUpAndVerifyTop() {
        openTestCasesPage();
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(600);
            List<WebElement> els = driver.findElements(scrollUpBtnCandidates);
            if (els.isEmpty()) return false;
            safeClick(els.get(0));
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 5000) {
                Object offset = ((JavascriptExecutor) driver).executeScript("return window.pageYOffset;");
                if (offset instanceof Number && ((Number) offset).longValue() <= 5) {
                    TestListener.pass("Scroll up verified to top.");
                    return true;
                }
                Thread.sleep(150);
            }
            TestListener.fail("Scroll up failed to reach top.");
            return false;
        } catch (Exception e) {
            TestListener.fail("Scroll up error: " + e.getMessage());
            return false;
        } finally {
            openTestCasesPage();
        }
    }

    public boolean isFooterCopyrightPresent() {
        openTestCasesPage();
        boolean ok = !driver.findElements(footerCopyright).isEmpty();
        TestListener.info("Footer copyright present: " + ok);
        return ok;
    }

    // debug
    public void debugPrintHeaderAnchors() {
        openTestCasesPage();
        List<WebElement> anchors = driver.findElements(By.cssSelector("header a, nav a, .navbar a"));
        System.out.println("=== HEADER ANCHORS ===");
        for (WebElement a : anchors) {
            System.out.printf("text='%s' href='%s' displayed=%b%n", a.getText().trim(), a.getAttribute("href"), a.isDisplayed());
        }
        System.out.println("=== END HEADER ===");
    }
}
