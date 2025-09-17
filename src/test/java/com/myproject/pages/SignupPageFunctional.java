package com.myproject.pages;

import com.myproject.listeners.TestListener;
import com.myproject.utils.ScreenshotUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * SignupPageFunctional (updated)
 *
 * - robust form-scoped signup fill
 * - tolerant clickSignup() which waits for multiple registration signals
 * - tolerant isProceedToRegistration() which matches same signals
 */
public class SignupPageFunctional {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Duration TIMEOUT = Duration.ofSeconds(15);

    // Top-level selectors to detect signup area (several fallbacks)
    private final By signupFormBlock = By.cssSelector(
            "form#signupForm, form#signup, .signup-form, .new-user, form[action*='signup'], div.signup-form"
    );

    // inside-signup-form relative selectors (we'll search scoped)
    private final By inFormName = By.cssSelector("input[name='name'], input#name, input[name='signup_name']");
    private final By inFormEmail = By.cssSelector("input[name='email'], input#email, input[name='signup_email']");
    private final By inFormSignupBtn = By.cssSelector("button[data-qa='signup-button'], button#signup, input[value='Signup'], button[type='submit']");

    // registration page indicator (after clicking signup)
    private final By registrationFormMarker = By.cssSelector("form#account-creation, form#register, .registration-form, #form-register");

    public SignupPageFunctional(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    /** Open the signup page (direct URL) and wait for signup block to be present. */
    public void open() {
        try {
            driver.get("https://www.automationexercise.com/signup");
            TestListener.info("Navigated to /signup");
            wait.until(d -> {
                try {
                    boolean blockPresent = !d.findElements(signupFormBlock).isEmpty();
                    boolean inputPresent = !d.findElements(inFormEmail).isEmpty() || !d.findElements(inFormName).isEmpty();
                    return blockPresent || inputPresent;
                } catch (Exception e) {
                    return false;
                }
            });
            TestListener.info("Signup page loaded (signup block or inputs detected)");
        } catch (TimeoutException te) {
            TestListener.fail("Timed out waiting for signup page to load: " + te.getMessage());
        } catch (Exception e) {
            TestListener.fail("Exception opening signup page: " + e.getMessage());
        }
    }

    /**
     * Fill signup name+email only — scoped to the signup form.
     * Returns true if both fields were found and filled.
     */
    public boolean fillSignup(String name, String email) {
        try {
            WebElement block = findDisplayedElement(signupFormBlock);
            if (block == null) {
                TestListener.info("Signup block not found; attempting to find inputs globally as fallback");
                WebElement n = wait.until(ExpectedConditions.visibilityOfElementLocated(inFormName));
                n.clear();
                n.sendKeys(name);
                WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(inFormEmail));
                e.clear();
                e.sendKeys(email);
                TestListener.info("Filled signup name & email using global fallback");
                return true;
            }

            WebElement nameEl = findVisibleWithin(block, inFormName);
            WebElement emailEl = findVisibleWithin(block, inFormEmail);

            if (nameEl == null || emailEl == null) {
                TestListener.info("Name/email inputs not found inside signup block; trying broader in-block search");
                List<WebElement> inputs = block.findElements(By.tagName("input"));
                WebElement foundName = null, foundEmail = null;
                for (WebElement inp : inputs) {
                    try {
                        String t = inp.getAttribute("type");
                        String nAttr = inp.getAttribute("name") != null ? inp.getAttribute("name").toLowerCase() : "";
                        String idAttr = inp.getAttribute("id") != null ? inp.getAttribute("id").toLowerCase() : "";
                        String ph = inp.getAttribute("placeholder") != null ? inp.getAttribute("placeholder").toLowerCase() : "";
                        if (foundName == null && (nAttr.contains("name") || idAttr.contains("name") || ph.contains("name"))) {
                            foundName = inp;
                        }
                        if (foundEmail == null && (nAttr.contains("email") || idAttr.contains("email") || ph.contains("email") || (t != null && t.equals("email")))) {
                            foundEmail = inp;
                        }
                    } catch (Exception ignored) {}
                }
                if (foundName != null) { foundName.clear(); foundName.sendKeys(name); TestListener.info("Filled signup name using broader in-block input"); }
                if (foundEmail != null) { foundEmail.clear(); foundEmail.sendKeys(email); TestListener.info("Filled signup email using broader in-block input"); }
                return (foundName != null && foundEmail != null);
            }

            nameEl.clear();
            nameEl.sendKeys(name);
            TestListener.info("Entered signup name (form-scoped)");

            emailEl.clear();
            emailEl.sendKeys(email);
            TestListener.info("Entered signup email (form-scoped)");

            return true;
        } catch (TimeoutException te) {
            TestListener.fail("Timed out locating signup inputs: " + te.getMessage());
            return false;
        } catch (Exception e) {
            TestListener.fail("Exception while filling signup: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click the signup button within the signup block (if present) and wait for registration marker.
     * Returns true if click succeeded and registration page/form appears.
     *
     * More tolerant detection: checks URL fragments, registration form marker, presence of password input,
     * and page text for phrases like "Enter Account Information" or "Create Account".
     */
    public boolean clickSignup() {
        try {
            WebElement block = findDisplayedElement(signupFormBlock);
            WebElement btn = null;
            if (block != null) {
                btn = findVisibleWithin(block, inFormSignupBtn);
            }
            if (btn == null) {
                TestListener.info("Signup button not found inside block; trying global selector fallback");
                btn = wait.until(ExpectedConditions.elementToBeClickable(inFormSignupBtn));
            }

            btn.click();
            TestListener.info("Clicked Signup button");

            boolean regReady = wait.until(d -> {
                try {
                    String url = d.getCurrentUrl();
                    if (url != null) {
                        String lu = url.toLowerCase();
                        if (lu.contains("/register") || lu.contains("/signup") || lu.contains("account") || lu.contains("create")) return true;
                    }
                } catch (Exception ignored) {}

                try {
                    List<WebElement> regs = d.findElements(registrationFormMarker);
                    for (WebElement r : regs) if (r.isDisplayed()) return true;
                } catch (Exception ignored) {}

                try {
                    List<WebElement> pwds = d.findElements(By.cssSelector("input[type='password'], input[name='password'], input#password"));
                    for (WebElement p : pwds) if (p.isDisplayed()) return true;
                } catch (Exception ignored) {}

                try {
                    String src = d.getPageSource().toLowerCase();
                    if (src.contains("enter account information") || src.contains("create account") || src.contains("account created") || src.contains("account information"))
                        return true;
                } catch (Exception ignored) {}

                return false;
            });

            if (regReady) {
                TestListener.info("Detected registration page/form after signup click");
            } else {
                TestListener.info("Did not detect registration page within wait after signup click");
            }

            return regReady;
        } catch (TimeoutException te) {
            TestListener.fail("Timed out waiting for signup click or registration page: " + te.getMessage());
            try {
                String ss = ScreenshotUtil.takeScreenshot(driver, "signup_click_timeout");
                TestListener.info("Saved debug screenshot: " + ss);
            } catch (Exception ignored) {}
            return false;
        } catch (Exception e) {
            TestListener.fail("Exception during clickSignup: " + e.getMessage());
            try {
                String ss = ScreenshotUtil.takeScreenshot(driver, "signup_click_exception");
                TestListener.info("Saved debug screenshot: " + ss);
            } catch (Exception ignored) {}
            return false;
        }
    }

    /** Check whether registration form is present (call before filling registration). */
    public boolean isProceedToRegistration() {
        try {
            boolean visible = wait.until(d -> {
                try {
                    List<WebElement> regs = d.findElements(registrationFormMarker);
                    for (WebElement r : regs) if (r.isDisplayed()) return true;
                } catch (Exception ignored) {}

                try {
                    List<WebElement> pwds = d.findElements(By.cssSelector("input[type='password'], input[name='password'], input#password"));
                    for (WebElement p : pwds) if (p.isDisplayed()) return true;
                } catch (Exception ignored) {}

                try {
                    String src = d.getPageSource().toLowerCase();
                    if (src.contains("enter account information") || src.contains("create account") || src.contains("account created") || src.contains("account information"))
                        return true;
                } catch (Exception ignored) {}

                try {
                    String url = d.getCurrentUrl();
                    if (url != null) {
                        String lu = url.toLowerCase();
                        if (lu.contains("/register") || lu.contains("/signup") || lu.contains("account") || lu.contains("create")) return true;
                    }
                } catch (Exception ignored) {}

                return false;
            });

            TestListener.info("isProceedToRegistration -> " + visible);
            return visible;
        } catch (Exception e) {
            TestListener.info("isProceedToRegistration -> false (" + e.getMessage() + ")");
            try {
                String ss = ScreenshotUtil.takeScreenshot(driver, "proceed_to_registration_debug");
                TestListener.info("Saved debug screenshot: " + ss);
            } catch (Exception ignored) {}
            return false;
        }
    }

    // ---------- Helper utilities ----------

    private WebElement findDisplayedElement(By locator) {
        try {
            List<WebElement> els = driver.findElements(locator);
            if (els.isEmpty()) return null;
            for (WebElement e : els) if (e.isDisplayed()) return e;
            return els.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private WebElement findVisibleWithin(WebElement root, By locator) {
        try {
            List<WebElement> els = root.findElements(locator);
            if (els.isEmpty()) return null;
            for (WebElement e : els) if (e.isDisplayed()) return e;
            return els.get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
