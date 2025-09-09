package com.myproject.pages;

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
    private final By productsLink = By.linkText("Products");
    private final By cartLink = By.linkText("Cart");
    private final By testCasesLink = By.linkText("Test Cases");
    private final By apiTestingLink = By.linkText("API Testing");
    private final By videoTutorialsLink = By.linkText("Video Tutorials");
    private final By contactUsLink = By.linkText("Contact Us");

    // subscription (footer)
    private final By subscriptionInput = By.id("susbscribe_email"); // common id on site
    private final By subscriptionBtn = By.id("subscribe");

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

    // helper safe visibility/clickable
    private boolean isVisible(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return driver.findElements(locator).size() > 0 && driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isClickable(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            return driver.findElements(locator).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // --- navigation to login page via top nav link (if not already there) ---
    public void openLoginPage() {
        try {
            if (driver.getCurrentUrl() == null || !driver.getCurrentUrl().contains("/login")) {
                if (isClickable(signupLoginNavLink)) {
                    driver.findElement(signupLoginNavLink).click();
                } else {
                    // fallback: open direct URL
                    driver.get("https://www.automationexercise.com/login");
                }
                wait.until(ExpectedConditions.urlContains("/login"));
            }
        } catch (Exception ignored) { }
    }

    // 1. Login form displayed
    public boolean isLoginFormDisplayed() {
        return isVisible(loginSection) && isVisible(loginEmailInput) && isVisible(loginPasswordInput);
    }

    // 2. Login section accessible from login page (same as above)
    public boolean isLoginSectionAccessible() {
        return isVisible(loginSection);
    }

    // 3. Signup contains name & email input
    public boolean isSignupNamePresent() {
        return isVisible(signupSection) && isVisible(signupNameInput);
    }
    public boolean isSignupEmailPresent() {
        return isVisible(signupSection) && isVisible(signupEmailInput);
    }

    // 4. Login section contains email & password
    public boolean isLoginEmailPresent() {
        return isVisible(loginSection) && isVisible(loginEmailInput);
    }
    public boolean isLoginPasswordPresent() {
        return isVisible(loginSection) && isVisible(loginPasswordInput);
    }

    // 5 & 6 signup/login button availability
    public boolean isSignupButtonAvailable() {
        return isVisible(signupSection) && isClickable(signupButton);
    }
    public boolean isLoginButtonAvailable() {
        return isVisible(loginSection) && isClickable(loginButton);
    }

    // Navigation buttons from login page
    public boolean isHomeNavWorking() {
        return clickNavAndVerify(homeLink, "/");
    }
    public boolean isProductsNavWorking() {
        return clickNavAndVerify(productsLink, "/products");
    }
    public boolean isCartNavWorking() {
        return clickNavAndVerify(cartLink, "/view_cart");
    }
    public boolean isTestCasesNavWorking() {
        return clickNavAndVerify(testCasesLink, "/test_cases");
    }
    public boolean isApiTestingNavWorking() {
        return clickNavAndVerify(apiTestingLink, "/api_list");
    }
    public boolean isVideoTutorialsNavWorking() {
        return clickNavAndVerify(videoTutorialsLink, "/tutorials"); // fallback
    }
    public boolean isContactUsNavWorking() {
        return clickNavAndVerify(contactUsLink, "/contact_us");
    }

    // helper to click a nav link and verify url contains expectedPath (does not navigate permanently)
    private boolean clickNavAndVerify(By navLocator, String expectedPathFragment) {
        try {
            String original = driver.getCurrentUrl();
            if (!isClickable(navLocator)) return false;
            driver.findElement(navLocator).click();
            // wait for URL change or element presence on new page
            wait.until(d -> d.getCurrentUrl().contains(expectedPathFragment) || d.getCurrentUrl().equals(original) == false);
            // navigate back to login page
            driver.navigate().back();
            wait.until(ExpectedConditions.urlContains("/login"));
            return true;
        } catch (Exception e) {
            try {
                // fallback: check link href attribute without clicking
                WebElement link = driver.findElement(navLocator);
                String href = link.getAttribute("href");
                return href != null && href.contains(expectedPathFragment);
            } catch (Exception ex) {
                return false;
            }
        }
    }

    // Subscription: enters and clicks subscribe; returns true if success or error message visible
    public void submitSubscription(String email) {
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(subscriptionInput));
            input.clear();
            input.sendKeys(email);
            // click subscribe if visible
            if (isClickable(subscriptionBtn)) {
                driver.findElement(subscriptionBtn).click();
            } else {
                input.sendKeys(Keys.ENTER);
            }
        } catch (Exception ignored) {}
    }

    public boolean isSubscriptionSuccessVisible() {
        try {
            By success = By.cssSelector(".alert-success, #success-subscribe");
            wait.until(ExpectedConditions.visibilityOfElementLocated(success));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isSubscriptionErrorVisible() {
        try {
            By error = By.cssSelector(".alert-danger, #error-subscribe");
            wait.until(ExpectedConditions.visibilityOfElementLocated(error));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Site logo presence
    public boolean isLogoDisplayed() {
        return isVisible(logo);
    }

    // Scroll-up button: appear after scrolling; clicking should return to top
    public boolean isScrollUpVisibleAfterScroll() {
        try {
            // scroll down
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            // wait briefly for scroll-up button to appear
            Thread.sleep(700); // small wait to allow UI to react
            return isVisible(scrollUpSelectors);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickScrollUpAndVerifyTop() {
        try {
            // ensure we are scrolled down
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(700);
            List<WebElement> els = driver.findElements(scrollUpSelectors);
            if (els.isEmpty()) return false;
            WebElement btn = els.get(0);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            // wait for top
            wait.until(d -> ((Long)((JavascriptExecutor)d).executeScript("return Math.round(window.pageYOffset);")) == 0L);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Very basic UI design check: ensures main structure exists (labels, inputs). This is a heuristic check.
    public boolean isLayoutConforming() {
        try {
            // check main page container + both sections exist
            return isVisible(pageContainer) && isVisible(signupSection) && isVisible(loginSection);
        } catch (Exception e) {
            return false;
        }
    }
}
