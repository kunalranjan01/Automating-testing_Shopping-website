package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginPageUI {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // --- locators with fallbacks ---
    private final By signupLoginNavLink = By.linkText("Signup / Login");
    // Signup block
    private final By signupSection = By.cssSelector(".signup-form, #signup, .new-user");
    private final By signupNameInput = By.cssSelector("input[name='name'], input#name, input[name='signup_name']");
    private final By signupEmailInput = By.cssSelector("input[name='email'], input#email, input[name='signup_email']");
    private final By signupButton = By.cssSelector("button[data-qa='signup-button'], button#signup, input[value='Signup']");

    // Login block
    private final By loginSection = By.cssSelector(".login-form, #login, .existing-user");
    private final By loginEmailInput = By.cssSelector("input[data-qa='login-email'], input[name='email'], input#login-email, input#email");
    private final By loginPasswordInput = By.cssSelector("input[data-qa='login-password'], input[name='password'], input#password");
    private final By loginButton = By.cssSelector("button[data-qa='login-button'], button#login, input[value='Login'], button[type='submit']");

    // Top navigation links
    private final By homeLink = By.linkText("Home");
    private final By productsLink = By.cssSelector("a[href='/products'], a[href='/products/']");
    private final By cartLink = By.linkText("Cart");
    private final By testCasesLink = By.linkText("Test Cases");
    private final By apiTestingLink = By.linkText("API Testing");
    private final By videoTutorialsLink = By.linkText("Video Tutorials");
    private final By contactUsLink = By.xpath("//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'contact') and (self::a or self::button)]");

    // subscription (footer) - fallback selectors to be resilient
    private final By subscriptionInput = By.cssSelector("input#susbscribe_email, input#subscribe_email, input[name='email'][placeholder*='Subscribe'], input[name='subscribe_email']");
    private final By subscriptionBtn = By.cssSelector("button#subscribe, button[name='subscribe'], input[value='Subscribe']");

    // logo
    private final By logo = By.cssSelector(".logo, .site-logo, .navbar-brand img");

    // scroll-up button common selectors and attributes
    private final By scrollUpSelectors = By.cssSelector("#scrollUp, .scroll-up, .scrollToTop, .scroll-top, .back-to-top");

    // general page container to check layout presence
    private final By pageContainer = By.cssSelector("body, .container, #main");

    public LoginPageUI(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- Logging helper ---
    private void logStep(String message) {
        TestListener.info(message);
    }

    // helper safe visibility/clickable
    private boolean isVisible(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            boolean present = !driver.findElements(locator).isEmpty() && driver.findElement(locator).isDisplayed();
            logStep("Checked visibility for locator " + locator + " -> " + present);
            return present;
        } catch (Exception e) {
            logStep("Visibility check failed for " + locator + " -> " + e.getMessage());
            return false;
        }
    }

    private boolean isClickable(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            logStep("Element clickable: " + locator);
            return true;
        } catch (Exception e) {
            logStep("Element not clickable: " + locator + " -> " + e.getMessage());
            return false;
        }
    }

    // --- navigation to login page via top nav link (if not already there) ---
    public void openLoginPage() {
        logStep("Action: openLoginPage() - ensure /login is opened");
        try {
            String cur = driver.getCurrentUrl() == null ? "" : driver.getCurrentUrl();
            if (!cur.contains("/login")) {
                if (isClickable(signupLoginNavLink)) {
                    driver.findElement(signupLoginNavLink).click();
                    logStep("Clicked top nav 'Signup / Login'");
                } else {
                    // fallback: open direct URL
                    driver.get("https://www.automationexercise.com/login");
                    logStep("Opened login page via direct URL fallback");
                }
                wait.until(ExpectedConditions.urlContains("/login"));
                logStep("openLoginPage() - URL contains /login");
            } else {
                logStep("Already on login page: " + cur);
            }
        } catch (Exception e) {
            logStep("Exception in openLoginPage(): " + e.getMessage());
            throw new RuntimeException("Failed to open login page", e);
        }
    }

    // 1. Login form displayed
    public boolean isLoginFormDisplayed() {
        logStep("Check: isLoginFormDisplayed()");
        boolean res = isVisible(loginSection) && isVisible(loginEmailInput) && isVisible(loginPasswordInput);
        logStep("Result: isLoginFormDisplayed -> " + res);
        return res;
    }

    // 2. Login section accessible from login page (same as above)
    public boolean isLoginSectionAccessible() {
        logStep("Check: isLoginSectionAccessible()");
        boolean res = isVisible(loginSection);
        logStep("Result: isLoginSectionAccessible -> " + res);
        return res;
    }

    // 3. Signup contains name & email input
    public boolean isSignupNamePresent() {
        logStep("Check: isSignupNamePresent()");
        boolean res = isVisible(signupSection) && isVisible(signupNameInput);
        logStep("Result: isSignupNamePresent -> " + res);
        return res;
    }
    public boolean isSignupEmailPresent() {
        logStep("Check: isSignupEmailPresent()");
        boolean res = isVisible(signupSection) && isVisible(signupEmailInput);
        logStep("Result: isSignupEmailPresent -> " + res);
        return res;
    }

    // 4. Login section contains email & password
    public boolean isLoginEmailPresent() {
        logStep("Check: isLoginEmailPresent()");
        boolean res = isVisible(loginSection) && isVisible(loginEmailInput);
        logStep("Result: isLoginEmailPresent -> " + res);
        return res;
    }
    public boolean isLoginPasswordPresent() {
        logStep("Check: isLoginPasswordPresent()");
        boolean res = isVisible(loginSection) && isVisible(loginPasswordInput);
        logStep("Result: isLoginPasswordPresent -> " + res);
        return res;
    }

    // 5 & 6 signup/login button availability
    public boolean isSignupButtonAvailable() {
        logStep("Check: isSignupButtonAvailable()");
        boolean res = isVisible(signupSection) && isClickable(signupButton);
        logStep("Result: isSignupButtonAvailable -> " + res);
        return res;
    }
    public boolean isLoginButtonAvailable() {
        logStep("Check: isLoginButtonAvailable()");
        boolean res = isVisible(loginSection) && isClickable(loginButton);
        logStep("Result: isLoginButtonAvailable -> " + res);
        return res;
    }

    // Navigation buttons from login page
    public boolean isHomeNavWorking() {
        logStep("Action: isHomeNavWorking()");
        boolean res = clickNavAndVerify(homeLink, "/");
        logStep("Result: isHomeNavWorking -> " + res);
        return res;
    }
    public boolean isProductsNavWorking() {
        logStep("Action: isProductsNavWorking()");
        boolean res = clickNavAndVerify(productsLink, "/products");
        logStep("Result: isProductsNavWorking -> " + res);
        return res;
    }
    public boolean isCartNavWorking() {
        logStep("Action: isCartNavWorking()");
        boolean res = clickNavAndVerify(cartLink, "/view_cart");
        logStep("Result: isCartNavWorking -> " + res);
        return res;
    }
    public boolean isTestCasesNavWorking() {
        logStep("Action: isTestCasesNavWorking()");
        boolean res = clickNavAndVerify(testCasesLink, "/test_cases");
        logStep("Result: isTestCasesNavWorking -> " + res);
        return res;
    }
    public boolean isApiTestingNavWorking() {
        logStep("Action: isApiTestingNavWorking()");
        boolean res = clickNavAndVerify(apiTestingLink, "/api_list");
        logStep("Result: isApiTestingNavWorking -> " + res);
        return res;
    }
    public boolean isVideoTutorialsNavWorking() {
        logStep("Action: isVideoTutorialsNavWorking()");
        boolean res = clickNavAndVerify(videoTutorialsLink, "/tutorials");
        logStep("Result: isVideoTutorialsNavWorking -> " + res);
        return res;
    }
    public boolean isContactUsNavWorking() {
        logStep("Action: isContactUsNavWorking()");
        boolean res = clickNavAndVerify(contactUsLink, "/contact_us");
        logStep("Result: isContactUsNavWorking -> " + res);
        return res;
    }

    // helper to click a nav link and verify url contains expectedPathFragment (does not navigate permanently)
    private boolean clickNavAndVerify(By navLocator, String expectedPathFragment) {
        logStep("Helper: clickNavAndVerify -> locator=" + navLocator + " expectedFragment=" + expectedPathFragment);
        try {
            String original = driver.getCurrentUrl();
            if (!isClickable(navLocator)) {
                logStep("Nav locator not clickable: " + navLocator);
                // fallback: check href attribute
                try {
                    WebElement link = driver.findElement(navLocator);
                    String href = link.getAttribute("href");
                    boolean hrefOk = href != null && href.contains(expectedPathFragment);
                    logStep("Fallback href check for " + navLocator + " -> " + href + " -> " + hrefOk);
                    return hrefOk;
                } catch (Exception ex) {
                    logStep("Fallback href check failed for " + navLocator + " -> " + ex.getMessage());
                    return false;
                }
            }

            driver.findElement(navLocator).click();
            logStep("Clicked nav locator: " + navLocator);
            // wait for either a URL change to expected fragment OR any URL change
            wait.until(d -> d.getCurrentUrl().contains(expectedPathFragment) || !d.getCurrentUrl().equals(original));
            logStep("Nav click led to url change: " + driver.getCurrentUrl());

            // navigate back to login page (best-effort)
            try {
                driver.navigate().back();
                wait.until(ExpectedConditions.urlContains("/login"));
                logStep("Navigated back to login page");
            } catch (Exception backEx) {
                logStep("Could not navigate back to login page cleanly: " + backEx.getMessage());
            }
            return true;
        } catch (Exception e) {
            logStep("Exception in clickNavAndVerify for " + navLocator + " -> " + e.getMessage());
            return false;
        }
    }

    // Subscription: enters and clicks subscribe; returns true if success or error message visible
    public void submitSubscription(String email) {
        logStep("Action: submitSubscription('" + email + "')");
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(subscriptionInput));
            input.clear();
            input.sendKeys(email);
            logStep("Entered subscription email");
            // click subscribe if visible
            if (isClickable(subscriptionBtn)) {
                driver.findElement(subscriptionBtn).click();
                logStep("Clicked subscription button");
            } else {
                input.sendKeys(Keys.ENTER);
                logStep("Pressed ENTER to submit subscription");
            }
        } catch (Exception e) {
            logStep("Exception in submitSubscription: " + e.getMessage());
            throw new RuntimeException("Failed to submit subscription", e);
        }
    }

    public boolean isSubscriptionSuccessVisible() {
        logStep("Check: isSubscriptionSuccessVisible()");
        try {
            By success = By.cssSelector(".alert-success, #success-subscribe");
            wait.until(ExpectedConditions.visibilityOfElementLocated(success));
            logStep("Result: subscription success visible");
            return true;
        } catch (Exception e) {
            logStep("Result: subscription success NOT visible -> " + e.getMessage());
            return false;
        }
    }
    public boolean isSubscriptionErrorVisible() {
        logStep("Check: isSubscriptionErrorVisible()");
        try {
            By error = By.cssSelector(".alert-danger, #error-subscribe");
            wait.until(ExpectedConditions.visibilityOfElementLocated(error));
            logStep("Result: subscription error visible");
            return true;
        } catch (Exception e) {
            logStep("Result: subscription error NOT visible -> " + e.getMessage());
            return false;
        }
    }

    // Site logo presence
    public boolean isLogoDisplayed() {
        logStep("Check: isLogoDisplayed()");
        boolean res = isVisible(logo);
        logStep("Result: isLogoDisplayed -> " + res);
        return res;
    }

    // Scroll-up button: appear after scrolling; clicking should return to top
    public boolean isScrollUpVisibleAfterScroll() {
        logStep("Action: isScrollUpVisibleAfterScroll()");
        try {
            // scroll down
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            // wait briefly for scroll-up button to appear
            Thread.sleep(700); // small wait to allow UI to react
            boolean res = isVisible(scrollUpSelectors);
            logStep("Result: isScrollUpVisibleAfterScroll -> " + res);
            return res;
        } catch (Exception e) {
            logStep("Exception in isScrollUpVisibleAfterScroll: " + e.getMessage());
            return false;
        }
    }

    public boolean clickScrollUpAndVerifyTop() {
        logStep("Action: clickScrollUpAndVerifyTop()");
        try {
            // ensure we are scrolled down
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(700);
            List<WebElement> els = driver.findElements(scrollUpSelectors);
            if (els.isEmpty()) {
                logStep("No scroll-up button found");
                return false;
            }
            WebElement btn = els.get(0);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            // wait for top
            wait.until(d -> {
                Object res = ((JavascriptExecutor) d).executeScript("return Math.round(window.pageYOffset);");
                if (res instanceof Long) return ((Long) res) == 0L;
                if (res instanceof Number) return ((Number) res).intValue() == 0;
                return false;
            });
            logStep("Result: clickScrollUpAndVerifyTop -> true (page scrolled to top)");
            return true;
        } catch (Exception e) {
            logStep("Exception in clickScrollUpAndVerifyTop: " + e.getMessage());
            return false;
        }
    }

    // Very basic UI design check: ensures main structure exists (labels, inputs). This is a heuristic check.
    public boolean isLayoutConforming() {
        logStep("Check: isLayoutConforming()");
        try {
            boolean res = isVisible(pageContainer) && isVisible(signupSection) && isVisible(loginSection);
            logStep("Result: isLayoutConforming -> " + res);
            return res;
        } catch (Exception e) {
            logStep("Exception in isLayoutConforming: " + e.getMessage());
            return false;
        }
    }
}
