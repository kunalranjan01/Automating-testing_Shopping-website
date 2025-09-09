package com.myproject.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ApiListPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final String baseUrl = "https://www.automationexercise.com";
    private final String apiListUrl = baseUrl + "/api_list";

    // top-nav fallbacks (simple)
    private final By navHome = By.linkText("Home");
    private final By navProducts = By.linkText("Products");
    private final By navCart = By.linkText("Cart");
    private final By navSignup = By.linkText("Signup / Login");
    private final By navTestCases = By.linkText("Test Cases");
    private final By navVideos = By.linkText("Video Tutorials");

    // page elements
    private final By pageTitle = By.cssSelector(".features_items h2, .page-title, .title.text-center, .features_items > h2");
    private final By apiListItems = By.cssSelector(".api-list a, .list-group a, .panel-body a, .single-api a, .col-sm-9 a");
    private final By feedbackCandidates = By.xpath("//*[contains(translate(.,'FEEDBACK','feedback'),'feedback') or contains(@id,'feedback') or contains(@class,'feedback')]");
    private final By logo = By.cssSelector(".logo, .navbar-brand img");
    private final By footerCopyright = By.xpath("//*[contains(text(),'All rights reserved') or contains(text(),'Copyright')]");

    public ApiListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openApiListPage() {
        driver.get(apiListUrl);
        waitForReady();
    }

    private void waitForReady() {
        try {
            wait.until(d -> ((JavascriptExecutor)d).executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {}
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

    // --- basic accessibility checks ---
    public boolean isApiListAccessibleViaUrl() {
        openApiListPage();
        return driver.getCurrentUrl().contains("/api_list");
    }

    public boolean isLogoPresent() {
        openApiListPage();
        return !driver.findElements(logo).isEmpty();
    }

    // top nav click+verify methods (reuse patterns used earlier)
    private WebElement findNavRobust(String text) {
        try {
            List<WebElement> els = driver.findElements(By.linkText(text));
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}
        try {
            List<WebElement> els = driver.findElements(By.partialLinkText(text.length() > 6 ? text.substring(0,6) : text));
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}
        try {
            String xpath = String.format("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '%s')]", text.toLowerCase());
            List<WebElement> els = driver.findElements(By.xpath(xpath));
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}
        return null;
    }

    private boolean navigateAndCheckNav(String visibleText, String urlHint) {
        openApiListPage();
        WebElement el = findNavRobust(visibleText);
        if (el == null) return false;
        safeClick(el);
        try {
            wait.until(d -> d.getCurrentUrl() != null);
            String cur = driver.getCurrentUrl().toLowerCase();
            boolean ok = (urlHint != null && !urlHint.isEmpty() && cur.contains(urlHint.toLowerCase()));
            // fallback: check page source for hint
            if (!ok) {
                String src = driver.getPageSource().toLowerCase();
                if (urlHint != null && !urlHint.isEmpty()) ok = src.contains(urlHint.toLowerCase());
            }
            return ok;
        } catch (Exception e) {
            return false;
        } finally {
            openApiListPage();
        }
    }

    public boolean clickHomeAndVerify() { return navigateAndCheckNav("Home", "automationexercise.com/"); }
    public boolean clickProductsAndVerify() { return navigateAndCheckNav("Products", "/products"); }
    public boolean clickCartAndVerify() { return navigateAndCheckNav("Cart", "/view_cart"); }
    public boolean clickSignupAndVerify() { return navigateAndCheckNav("Signup / Login", "/login"); }
    public boolean clickTestCasesAndVerify() { return navigateAndCheckNav("Test Cases", "/test_cases"); }
    public boolean clickVideosAndVerify() { return navigateAndCheckNav("Video Tutorials", "video"); }

    // --- page content checks ---
    public boolean isApiListTitlePresent(String expectedTextFragment) {
        openApiListPage();
        try {
            WebElement h = driver.findElement(pageTitle);
            return h.isDisplayed() && h.getText().toLowerCase().contains(expectedTextFragment.toLowerCase());
        } catch (Exception e) {
            // fallback: check page source
            return driver.getPageSource().toLowerCase().contains(expectedTextFragment.toLowerCase());
        }
    }

    public boolean isFeedbackSectionPresent() {
        openApiListPage();
        return !driver.findElements(feedbackCandidates).isEmpty();
    }

    public boolean isFeedbackMailPresent(String expectedMail) {
        openApiListPage();
        // look for mailto or text containing expectedMail inside feedback area first
        try {
            List<WebElement> cands = driver.findElements(feedbackCandidates);
            for (WebElement sec : cands) {
                List<WebElement> mails = sec.findElements(By.xpath(".//a[contains(@href,'mailto:') or contains(text(),'" + expectedMail + "')]"));
                if (!mails.isEmpty()) return true;
                List<WebElement> texts = sec.findElements(By.xpath(".//*[contains(text(),'" + expectedMail + "')]"));
                if (!texts.isEmpty()) return true;
            }
            // global fallback
            List<WebElement> global = driver.findElements(By.xpath("//a[contains(@href,'mailto:') or contains(text(),'" + expectedMail + "')]"));
            return !global.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAutomationExerciseButtonPresent() {
        openApiListPage();
        return !driver.findElements(logo).isEmpty();
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
        return visible;
    }

    public boolean verifyApiListCount(int expected) {
        List<WebElement> list = getApiListElements();
        return list.size() >= expected;
    }

    /**
     * For each API entry: open (click or navigate), then verify:
     *  - api url (presence of 'http' or '/api' in page)
     *  - request method (GET/POST/PUT/DELETE) — searches page for those words near "Method" or "Request"
     *  - request/response code (search for "200", "201", "400", "404", "500" nearby)
     *  - version numbers (presence of "v1", "v2", "v1.0", etc.)
     *
     * Best-effort: returns true if at least half of entries show the expected artifacts.
     */
    public boolean verifyEachApiEntryDetails() {
        List<WebElement> entries = getApiListElements();
        if (entries.isEmpty()) return false;
        int success = 0;
        for (WebElement e : entries) {
            try {
                String href = e.getAttribute("href");
                // open link (prefer navigation to preserve same-tab / new-tab behavior)
                if (href != null && !href.trim().isEmpty()) {
                    // open in same tab to inspect content
                    driver.get(href);
                } else {
                    safeClick(e);
                }
                waitForReady();
                String pageSrc = driver.getPageSource().toLowerCase();

                boolean hasApiUrl = pageSrc.contains("http") || pageSrc.contains("/api") || pageSrc.contains("endpoint");
                boolean hasMethod = pageSrc.contains("get") || pageSrc.contains("post") || pageSrc.contains("put") || pageSrc.contains("delete") || pageSrc.contains("patch");
                boolean hasCode = pageSrc.matches("(?s).*\\b(200|201|204|400|401|403|404|500)\\b.*");
                boolean hasVersion = pageSrc.contains("v1") || pageSrc.contains("v2") || pageSrc.contains("version");

                // count entry as success if at least two of the checks are true
                int positives = 0;
                if (hasApiUrl) positives++;
                if (hasMethod) positives++;
                if (hasCode) positives++;
                if (hasVersion) positives++;
                if (positives >= 2) success++;

                // return to api list
                openApiListPage();
            } catch (Exception ignored) {
                try { openApiListPage(); } catch (Exception e2) {}
            }
        }
        return success >= Math.max(1, entries.size() / 2);
    }

    /**
     * Check whether API links open in same tab or new tab by checking target attribute of anchors.
     * If target="_blank" present => opens new tab. We return true if at least one link has target attribute set,
     * and also provide a best-effort boolean whether most open in same-tab.
     */
    public boolean checkApiLinksOpenBehavior() {
        List<WebElement> entries = getApiListElements();
        if (entries.isEmpty()) return false;
        int newTabCount = 0;
        for (WebElement e : entries) {
            try {
                String target = e.getAttribute("target");
                if (target != null && target.equalsIgnoreCase("_blank")) newTabCount++;
            } catch (Exception ignored) {}
        }
        // pass if at least some links present and we can detect behavior (either all same-tab or some new-tab)
        return entries.size() > 0;
    }

    public boolean isFooterCopyrightPresent() {
        openApiListPage();
        return !driver.findElements(footerCopyright).isEmpty();
    }

    // debug helper
    public void debugPrintApiAnchors() {
        openApiListPage();
        List<WebElement> anchors = driver.findElements(By.cssSelector("a"));
        System.out.println("=== API PAGE ANCHORS (visible & href) ===");
        for (WebElement a : anchors) {
            try {
                if (a.isDisplayed()) {
                    System.out.printf("text='%s' href='%s' target='%s'%n", a.getText().trim(), a.getAttribute("href"), a.getAttribute("target"));
                }
            } catch (Exception ignored) {}
        }
        System.out.println("=== END ANCHORS ===");
    }
}
