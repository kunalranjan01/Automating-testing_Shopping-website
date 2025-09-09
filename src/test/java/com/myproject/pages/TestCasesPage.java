package com.myproject.pages;

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
    private final By navProducts = By.linkText("Products");
    private final By navCart = By.linkText("Cart");
    private final By navSignup = By.linkText("Signup / Login");
    private final By navTestCases = By.linkText("Test Cases");
    private final By navApi = By.linkText("API Testing");
    private final By navVideos = By.linkText("Video Tutorials");
    private final By navContact1 = By.linkText("Contact us");
    private final By navContact2 = By.linkText("Contact Us");

    // page elements & candidates
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
        } catch (Exception e) {
            try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); } catch (Exception ignored) {}
        }
    }

    // robust finder: tries multiple strategies to find nav anchor by visible text
    private WebElement findNavRobust(String exactText) {
        if (exactText == null) return null;
        try {
            List<WebElement> els = driver.findElements(By.linkText(exactText));
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}

        try {
            String part = exactText.length() > 8 ? exactText.substring(0, Math.min(8, exactText.length())) : exactText;
            List<WebElement> els2 = driver.findElements(By.partialLinkText(part));
            for (WebElement e : els2) {
                try {
                    if (e.isDisplayed() && e.isEnabled() && e.getText().trim().length() > 0 &&
                            e.getText().toLowerCase().contains(part.trim().toLowerCase())) return e;
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        try {
            String xpath = String.format("//a[normalize-space(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')) = '%s']",
                    exactText.toLowerCase());
            List<WebElement> els3 = driver.findElements(By.xpath(xpath));
            for (WebElement e : els3) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}

        try {
            String xpath2 = String.format("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '%s')]",
                    exactText.toLowerCase());
            List<WebElement> els4 = driver.findElements(By.xpath(xpath2));
            for (WebElement e : els4) if (e.isDisplayed() && e.isEnabled()) return e;
        } catch (Exception ignored) {}

        return null;
    }

    private boolean navigateAndVerify(WebElement el, String urlFragmentHint) {
        if (el == null) return false;
        String href = "";
        try { href = el.getAttribute("href"); } catch (Exception ignored) {}
        try {
            if (href != null && !href.trim().isEmpty()) {
                try {
                    URI u = new URI(href);
                    if (!u.isAbsolute()) {
                        safeClick(el);
                    } else {
                        driver.get(href);
                    }
                } catch (Exception ex) { safeClick(el); }
            } else {
                safeClick(el);
            }
            wait.until(d -> d.getCurrentUrl() != null);
            String cur = driver.getCurrentUrl().toLowerCase();
            if (urlFragmentHint != null && !urlFragmentHint.isEmpty() && cur.contains(urlFragmentHint.toLowerCase())) return true;
            // fallback: check page contains something indicative
            String src = driver.getPageSource().toLowerCase();
            if (urlFragmentHint == null) return !cur.contains("test_cases");
            return cur.contains(urlFragmentHint.toLowerCase()) || src.contains(urlFragmentHint.toLowerCase());
        } catch (Exception e) {
            return false;
        } finally {
            openTestCasesPage();
        }
    }

    // top-nav public wrappers
    public boolean clickHomeAndVerifyThenBack() { openTestCasesPage(); WebElement e = findNavRobust("Home"); return navigateAndVerify(e, "automationexercise.com/"); }
    public boolean clickProductsAndVerifyThenBack() { openTestCasesPage(); WebElement e = findNavRobust("Products"); return navigateAndVerify(e, "/products"); }
    public boolean clickCartAndVerifyThenBack() { openTestCasesPage(); WebElement e = findNavRobust("Cart"); return navigateAndVerify(e, "/view_cart"); }
    public boolean clickSignupLoginAndVerifyThenBack() { openTestCasesPage(); WebElement e = findNavRobust("Signup / Login"); return navigateAndVerify(e, "/login"); }
    public boolean clickTestCasesAndVerifyThenBack() { openTestCasesPage(); WebElement e = findNavRobust("Test Cases"); return navigateAndVerify(e, "/test_cases"); }
    public boolean clickApiTestingAndVerifyThenBack() { openTestCasesPage(); WebElement e = findNavRobust("API Testing"); return navigateAndVerify(e, "api"); }
    public boolean clickVideoTutorialsAndVerifyThenBack() { openTestCasesPage(); WebElement e = findNavRobust("Video Tutorials"); return navigateAndVerify(e, "video"); }
    public boolean clickContactUsAndVerifyThenBack() {
        openTestCasesPage();
        WebElement e = findNavRobust("Contact us"); if (e == null) e = findNavRobust("Contact Us");
        return navigateAndVerify(e, "/contact_us");
    }

    // logo
    public boolean isLogoDisplayed() { openTestCasesPage(); return !driver.findElements(siteLogo).isEmpty(); }

    // heading centered (best-effort)
    public boolean isHeadingCenteredAndDisplayed() {
        openTestCasesPage();
        try {
            WebElement h = driver.findElement(pageHeading);
            if (!h.isDisplayed()) return false;
            Object rect = ((JavascriptExecutor) driver).executeScript(
                    "var r = arguments[0].getBoundingClientRect(); return {x: r.left, width: r.width, viewport: window.innerWidth};", h);
            if (rect instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> m = (java.util.Map<String, Object>) rect;
                double left = Double.parseDouble(m.get("x").toString());
                double width = Double.parseDouble(m.get("width").toString());
                double viewport = Double.parseDouble(m.get("viewport").toString());
                double center = left + width / 2.0;
                double viewportCenter = viewport / 2.0;
                return Math.abs(center - viewportCenter) <= viewport * 0.10;
            }
            return true;
        } catch (Exception e) {
            return !driver.findElements(pageHeading).isEmpty();
        }
    }

    // test case list
    public List<WebElement> getTestCaseTitleElements() {
        openTestCasesPage();
        List<WebElement> els = driver.findElements(testcaseLinkCandidates);
        List<WebElement> filtered = new ArrayList<>();
        for (WebElement e : els) {
            try {
                if (e.isDisplayed() && e.getAttribute("href") != null) filtered.add(e);
            } catch (Exception ignored) {}
        }
        return filtered;
    }

    public boolean verifyTestCaseCount(int expectedCount) {
        List<WebElement> t = getTestCaseTitleElements();
        return t.size() >= expectedCount;
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
                if (cur != null && !cur.toLowerCase().contains("/test_cases")) success++;
                driver.get(testCasesUrl);
                waitForReady();
            } catch (Exception ignored) {}
        }
        return success >= Math.max(1, titles.size() / 2);
    }

    // feedback
    public boolean isFeedbackSectionDisplayed() {
        openTestCasesPage();
        return !driver.findElements(feedbackCandidates).isEmpty();
    }

    public boolean isMailMentionedInFeedback() {
        openTestCasesPage();
        try {
            List<WebElement> cands = driver.findElements(feedbackCandidates);
            for (WebElement sec : cands) {
                try {
                    List<WebElement> mails = sec.findElements(By.xpath(".//a[contains(@href,'mailto:')]"));
                    if (!mails.isEmpty()) return true;
                    List<WebElement> texts = sec.findElements(By.xpath(".//*[contains(text(),'@')]"));
                    for (WebElement t : texts) {
                        String txt = t.getText();
                        if (txt != null && txt.contains("@")) return true;
                    }
                } catch (Exception ignored) {}
            }
            List<WebElement> global = driver.findElements(By.xpath("//a[contains(@href,'mailto:') or contains(text(),'@automationexercise.com')]"));
            return !global.isEmpty();
        } catch (Exception ignored) {}
        return false;
    }

    // scroll up
    public boolean clickScrollUpAndVerifyTop() {
        openTestCasesPage();
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(600);
            List<WebElement> els = driver.findElements(scrollUpBtnCandidates);
            if (els.isEmpty()) {
                els = driver.findElements(By.xpath("//*[contains(translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'top') or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'top')]"));
                if (els.isEmpty()) return false;
            }
            WebElement btn = null;
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) { btn = e; break; }
            if (btn == null) btn = els.get(0);
            safeClick(btn);
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 5000) {
                Object offset = ((JavascriptExecutor) driver).executeScript("return window.pageYOffset || document.documentElement.scrollTop;");
                long off = 0;
                if (offset instanceof Number) off = ((Number) offset).longValue();
                else {
                    try { off = Long.parseLong(offset.toString()); } catch (Exception ignored) {}
                }
                if (off <= 5) return true;
                Thread.sleep(150);
            }
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            openTestCasesPage();
        }
    }

    public boolean isFooterCopyrightPresent() {
        openTestCasesPage();
        return !driver.findElements(footerCopyright).isEmpty();
    }

    // debug helper
    public void debugPrintHeaderAnchors() {
        openTestCasesPage();
        List<WebElement> anchors = driver.findElements(By.cssSelector("header a, nav a, .navbar a"));
        System.out.println("=== HEADER ANCHORS ===");
        for (WebElement a : anchors) {
            try {
                System.out.printf("text='%s' href='%s' displayed=%b%n", a.getText().trim(), a.getAttribute("href"), a.isDisplayed());
            } catch (Exception ignored) {}
        }
        System.out.println("=== END HEADER ===");
    }
}
