package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginPageFunctional {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Duration TIMEOUT = Duration.ofSeconds(15);

    private final By signupLoginNav = By.linkText("Signup / Login");
    private final By emailInput = By.cssSelector("input[data-qa='login-email'], input[name='email'], input#login-email, input#email");
    private final By passwordInput = By.cssSelector("input[data-qa='login-password'], input[name='password'], input#password");
    private final By loginBtn = By.cssSelector("button[data-qa='login-button'], button#login, button[type='submit'], input[value='Login']");
    private final By loggedInMarker = By.xpath("//*[contains(normalize-space(.),'Logged in as') or contains(normalize-space(.),'Logout') or contains(normalize-space(.),'My Account')]");
    private final By loginError = By.cssSelector(".alert-danger, .text-danger, .login-error, .help-block");

    public LoginPageFunctional(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    /** Ensure login page opened (tries nav link then direct URL) */
    public boolean openLoginPage() {
        TestListener.info("Open login page");
        try {
            List<WebElement> nav = driver.findElements(signupLoginNav);
            if (!nav.isEmpty()) {
                for (WebElement n : nav) {
                    if (n.isDisplayed()) {
                        try { n.click(); } catch (Exception ex) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", n); }
                        waitUntilLoginReady();
                        return true;
                    }
                }
            }
            driver.get("https://www.automationexercise.com/login");
            waitUntilLoginReady();
            return true;
        } catch (Exception e) {
            TestListener.fail("Failed to open login page: " + e.getMessage());
            return false;
        }
    }

    private void waitUntilLoginReady() {
        try {
            wait.until(d -> {
                try {
                    if (!d.findElements(emailInput).isEmpty() && d.findElement(emailInput).isDisplayed()) return true;
                } catch (Exception ignored) {}
                try {
                    String cur = d.getCurrentUrl();
                    if (cur != null && cur.contains("/login")) return true;
                } catch (Exception ignored) {}
                return false;
            });
        } catch (Exception ignored) {}
    }

    /** Perform login with given email & password. Returns true when logged-in marker appears. */
    public boolean login(String email, String password) {
        TestListener.info("Login: email=" + mask(email));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput)).clear();
            driver.findElement(emailInput).sendKeys(email);
            TestListener.info("Filled email");

            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput)).clear();
            driver.findElement(passwordInput).sendKeys(password);
            TestListener.info("Filled password");

            WebElement btn = null;
            try { btn = driver.findElement(loginBtn); } catch (Exception ignored) {}
            if (btn != null && btn.isDisplayed()) {
                try { btn.click(); } catch (Exception ex) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
                TestListener.info("Clicked login button");
            } else {
                // fallback: press Enter on password input
                driver.findElement(passwordInput).sendKeys(Keys.ENTER);
                TestListener.info("Submitted via ENTER fallback");
            }

            // wait for success marker or explicit error
            boolean result = wait.until(d -> {
                try {
                    List<WebElement> ok = d.findElements(loggedInMarker);
                    for (WebElement e : ok) if (e.isDisplayed()) return true;
                } catch (Exception ignored) {}
                try {
                    List<WebElement> err = d.findElements(loginError);
                    for (WebElement e : err) if (e.isDisplayed()) return false;
                } catch (Exception ignored) {}
                return null;
            }) == Boolean.TRUE;

            if (result) {
                TestListener.pass("Login succeeded for " + mask(email));
                return true;
            } else {
                TestListener.fail("Login failed for " + mask(email));
                return false;
            }
        } catch (Exception e) {
            TestListener.fail("Exception during login for " + mask(email) + " : " + e.getMessage());
            return false;
        }
    }

    private String mask(String email) {
        if (email == null) return "";
        int at = email.indexOf('@');
        if (at <= 1) return "****";
        return email.substring(0,1) + "****" + email.substring(at);
    }
}
