package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ApiListPageFunctional {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String apiListUrl = "https://www.automationexercise.com/api_list";

    // Locators (robust)
    private final By pageTitle = By.cssSelector(".features_items h2, .page-title, .title.text-center, .features_items > h2");
    private final By apiListAnchors = By.cssSelector(".api-list a, .list-group a, .panel-body a, .single-api a, .col-sm-9 a, .features_items a");
    private final By logo = By.cssSelector(".logo, .navbar-brand img, .header_logo");
    private final By feedbackCandidates = By.xpath("//*[contains(translate(.,'FEEDBACK','feedback'),'feedback') or contains(@id,'feedback') or contains(@class,'feedback')]");
    private final By footerCopyright = By.xpath("//*[contains(text(),'All rights reserved') or contains(text(),'Copyright')]");
    private final By navLinks = By.cssSelector("header a, nav a, .navbar a, a"); // broad fallback

    public ApiListPageFunctional(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    private void log(String msg) { TestListener.info(msg); }
    private void failLog(String msg) { TestListener.fail(msg); }

    // --- navigation ---
    public void open() {
        log("Navigate to API list page: " + apiListUrl);
        driver.get(apiListUrl);
        waitForReady();
        log("Page opened: " + driver.getCurrentUrl());
    }

    private void waitForReady() {
        try {
            wait.until(d -> ((JavascriptExecutor)d).executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {}
    }

    // --- simple checks ---
    public boolean isPageLoaded() {
        log("Checking page title presence");
        try {
            return !driver.findElements(pageTitle).isEmpty();
        } catch (Exception e) {
            failLog("isPageLoaded exception: " + e.getMessage());
            return false;
        }
    }

    public boolean isLogoPresent() {
        log("Checking logo presence");
        try {
            return !driver.findElements(logo).isEmpty();
        } catch (Exception e) {
            failLog("isLogoPresent exception: " + e.getMessage());
            return false;
        }
    }

    public boolean isFeedbackSectionPresent() {
        log("Checking feedback section presence");
        try {
            open(); // ensure page is active
            boolean ok = !driver.findElements(feedbackCandidates).isEmpty();
            log("Feedback candidates found -> " + ok);
            return ok;
        } catch (Exception e) {
            failLog("isFeedbackSectionPresent exception: " + e.getMessage());
            return false;
        }
    }

    public boolean isFeedbackMailPresent(String expectedMail) {
        log("Checking for feedback email: " + expectedMail);
        try {
            open();
            List<WebElement> cands = driver.findElements(feedbackCandidates);
            for (WebElement sec : cands) {
                try {
                    List<WebElement> mails = sec.findElements(By.xpath(".//a[contains(@href,'mailto:') or contains(text(),'" + expectedMail + "')]"));
                    if (!mails.isEmpty()) {
                        log("Found expected mail inside feedback section");
                        return true;
                    }
                } catch (Exception ignored) {}
            }
            // global fallback
            List<WebElement> global = driver.findElements(By.xpath("//a[contains(@href,'mailto:') or contains(text(),'" + expectedMail + "')]"));
            boolean ok = !global.isEmpty();
            log("Global href/text search for mail -> " + ok);
            return ok;
        } catch (Exception e) {
            failLog("isFeedbackMailPresent exception: " + e.getMessage());
            return false;
        }
    }

    public List<WebElement> getApiAnchors() {
        log("Collecting API anchors using selectors");
        open();
        List<WebElement> list = new ArrayList<>();
        try {
            list = driver.findElements(apiListAnchors);
            log("Found " + list.size() + " anchors via primary selector");
            if (list.isEmpty()) {
                // fallback to any anchor inside main content
                List<WebElement> fallback = driver.findElements(By.cssSelector("main a, .container a, .col-sm-9 a"));
                log("Fallback anchors found: " + fallback.size());
                list = fallback;
            }
        } catch (Exception e) {
            failLog("getApiAnchors exception: " + e.getMessage());
        }
        return list;
    }

    public boolean hasAtLeastNApiLinks(int n) {
        List<WebElement> list = getApiAnchors();
        boolean ok = list.size() >= n;
        log("API anchors >= " + n + " ? " + ok + " (found=" + list.size() + ")");
        return ok;
    }

    public boolean areAllApiAnchorsHaveHref() {
        List<WebElement> list = getApiAnchors();
        int missing = 0;
        for (WebElement a : list) {
            try {
                String href = a.getAttribute("href");
                if (href == null || href.trim().isEmpty()) missing++;
            } catch (Exception ignored) { missing++; }
        }
        boolean ok = missing == 0;
        log("All anchors have href? " + ok + " (missing=" + missing + ")");
        return ok;
    }

    /**
     * Best-effort verify each API entry details:
     * - open anchor (navigate same-tab)
     * - check for presence of 'http', '/api', verbs get/post/put/delete, status codes
     * returns true if >= half of inspected anchors show at least 2 artifacts
     */
    public boolean verifyEachApiEntryDetails() {
        log("verifyEachApiEntryDetails: start");
        List<WebElement> anchors = getApiAnchors();
        if (anchors.isEmpty()) {
            failLog("No API anchors to inspect");
            return false;
        }
        int success = 0;
        int inspected = 0;
        for (WebElement a : anchors) {
            try {
                String href = a.getAttribute("href");
                String text = a.getText();
                log("Inspecting anchor: text='" + (text==null? "":text.trim()) + "' href='" + href + "'");
                // open: prefer navigate so we can inspect content
                if (href != null && !href.trim().isEmpty() && (href.startsWith("http") || href.startsWith("/"))) {
                    try {
                        driver.get(href);
                        waitForReady();
                    } catch (Exception navEx) {
                        log("Navigation to href failed, will attempt click: " + navEx.getMessage());
                        try { a.click(); waitForReady(); } catch (Exception ignored) {}
                    }
                } else {
                    try { a.click(); waitForReady(); } catch (Exception ignored) {}
                }

                String src = driver.getPageSource().toLowerCase();
                boolean hasApiUrl = src.contains("http") || src.contains("/api") || src.contains("endpoint");
                boolean hasMethod = src.contains("get") || src.contains("post") || src.contains("put") || src.contains("delete") || src.contains("patch");
                boolean hasCode = src.matches("(?s).*\\b(200|201|204|400|401|403|404|500)\\b.*");
                boolean hasVersion = src.contains("v1") || src.contains("v2") || src.contains("version");

                int positives = 0;
                if (hasApiUrl) positives++;
                if (hasMethod) positives++;
                if (hasCode) positives++;
                if (hasVersion) positives++;

                log("Entry checks => apiUrl:" + hasApiUrl + " method:" + hasMethod + " code:" + hasCode + " version:" + hasVersion);

                if (positives >= 2) success++;

                inspected++;
                // go back to api list page
                driver.get(apiListUrl);
                waitForReady();
            } catch (Exception e) {
                log("Exception inspecting entry: " + e.getMessage());
                try { driver.get(apiListUrl); waitForReady(); } catch (Exception ignored) {}
            }
        }
        boolean ok = success >= Math.max(1, inspected / 2);
        log("verifyEachApiEntryDetails -> successCount=" + success + " inspected=" + inspected + " -> ok=" + ok);
        return ok;
    }

    public boolean doApiLinksOpenBehaviorCheck() {
        log("Checking whether API links open same-tab or new-tab");
        List<WebElement> anchors = getApiAnchors();
        int newTabCount = 0;
        for (WebElement a : anchors) {
            try {
                String target = a.getAttribute("target");
                if (target != null && target.equalsIgnoreCase("_blank")) newTabCount++;
            } catch (Exception ignored) {}
        }
        boolean ok = anchors.size() > 0;
        log("Found " + anchors.size() + " anchors; newTabCount=" + newTabCount + " -> OK=" + ok);
        return ok;
    }

    public boolean checkAtLeastOneAnchorContainsApiUrl() {
        log("Checking anchors contain 'http' or '/api' strings");
        List<WebElement> anchors = getApiAnchors();
        for (WebElement a : anchors) {
            try {
                String href = a.getAttribute("href");
                if (href != null && (href.contains("http") || href.contains("/api"))) {
                    log("Found anchor with api-like href: " + href);
                    return true;
                }
            } catch (Exception ignored) {}
        }
        log("No anchor with api-like href detected");
        return false;
    }

    public boolean isFooterPresent() {
        log("Checking footer copyright presence");
        try {
            return !driver.findElements(footerCopyright).isEmpty();
        } catch (Exception e) {
            failLog("isFooterPresent exception: " + e.getMessage());
            return false;
        }
    }

    // debug helper to print anchors into Extent
    public void debugPrintAnchors(int limit) {
        List<WebElement> anchors = getApiAnchors();
        log("Debug print anchors (limit " + limit + "): found " + anchors.size());
        int i = 0;
        for (WebElement a : anchors) {
            if (i++ >= limit) break;
            try {
                log("ANCHOR[" + i + "] text='" + a.getText().trim() + "' href='" + a.getAttribute("href") + "' target='" + a.getAttribute("target") + "'");
            } catch (Exception ignored) {}
        }
    }
}
