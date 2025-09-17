package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * RegistrationPageFunctional - robust registration form filler and submitter.
 * Improved detection of password/input fields and clear failure if password is empty.
 */
public class RegistrationPageFunctional {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Duration TIMEOUT = Duration.ofSeconds(20);

    private final By registrationFormMarker = By.cssSelector("form#account-creation, form#register, .registration-form, #form-register");
    private final By insidePassword = By.cssSelector("input[type='password'], input#password, input[name='password'], input[data-qa='password']");
    private final By insideFirstName = By.cssSelector("input[name='first_name'], input#first_name, input[name='firstname'], input#firstname");
    private final By insideLastName = By.cssSelector("input[name='last_name'], input#last_name, input[name='lastname'], input#lastname");
    private final By insideAddress = By.cssSelector("input[name='address1'], textarea[name='address1'], input[name='address']");
    private final By insideCountry = By.cssSelector("select[name='country'], select#country");
    private final By insideState = By.cssSelector("input[name='state'], input#state, input[name='region']");
    private final By insideCity = By.cssSelector("input[name='city'], input#city");
    private final By insideZipcode = By.cssSelector("input[name='zipcode'], input[name='postalcode'], input#zipcode");
    private final By insideMobile = By.cssSelector("input[name='mobile_number'], input[name='mobile'], input#mobile");
    private final By insideCreateAcctBtn = By.cssSelector("button[data-qa='create-account'], button#create-account, input[value='Create Account'], button[type='submit']");

    // DOB and checkboxes (broad fallbacks)
    private final By dobDay = By.cssSelector("select[name='days'], select#days, select[name='day']");
    private final By dobMonth = By.cssSelector("select[name='months'], select#months, select[name='month']");
    private final By dobYear = By.cssSelector("select[name='years'], select#years, select[name='year']");

    private final By checkboxNewsletter = By.cssSelector("input[type='checkbox'][name*='news'], input[type='checkbox'][id*='news'], input[type='checkbox'][name*='newsletter']");
    private final By checkboxOffers = By.cssSelector("input[type='checkbox'][name*='offer'], input[type='checkbox'][id*='offer'], input[type='checkbox'][name*='offers']");

    private final By successAlert = By.cssSelector(".alert-success, .status.alert.alert-success, .success-message");
    private final By logoutLink = By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'logout')]");
    private final By accountArea = By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'my account') or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'account created')]");

    public RegistrationPageFunctional(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    private WebElement waitForRegistrationFormOrPassword() {
        try {
            WebElement element = wait.until(d -> {
                try {
                    // look for registration form container displayed
                    List<WebElement> forms = d.findElements(registrationFormMarker);
                    for (WebElement f : forms) if (f.isDisplayed()) return f;

                    // look for any visible password input (good indicator of registration)
                    List<WebElement> pwds = d.findElements(insidePassword);
                    for (WebElement p : pwds) if (p.isDisplayed()) return p;

                    // fallback: look for create-account button in page
                    List<WebElement> cbtns = d.findElements(insideCreateAcctBtn);
                    for (WebElement b : cbtns) if (b.isDisplayed()) return b;

                } catch (Exception ignored) {}
                return null;
            });
            TestListener.info("Registration form element found.");
            return element;
        } catch (TimeoutException te) {
            TestListener.fail("Timed out waiting for registration form: " + te.getMessage());
            return null;
        } catch (Exception e) {
            TestListener.fail("Error waiting for registration form: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fill remaining registration fields (EXCLUDING name & email which come from signup).
     *
     * Returns true if at least one expected field was filled (best-effort).
     */
    public boolean fillRegistration(String password,
                                    String firstName,
                                    String lastName,
                                    String address,
                                    String country,
                                    String state,
                                    String city,
                                    String zipcode,
                                    String mobile,
                                    String day, String month, String year,
                                    boolean newsletterFlag, boolean offersFlag) {

        // fail fast if password param is empty — registration normally requires password
        if (password == null || password.trim().isEmpty()) {
            TestListener.info("Password parameter is empty — cannot proceed with registration fill.");
            return false;
        }

        WebElement formOrAnchor = waitForRegistrationFormOrPassword();
        if (formOrAnchor == null) {
            TestListener.fail("Registration form not available to fill.");
            return false;
        }

        // attempt to find the registration form container if possible (to scope searches)
        WebElement form = null;
        try {
            List<WebElement> forms = driver.findElements(registrationFormMarker);
            for (WebElement f : forms) if (f.isDisplayed()) { form = f; break; }
        } catch (Exception ignored) {}

        boolean anyFilled = false;
        try {
            // password
            WebElement pwd = (form != null) ? findVisibleWithin(form, insidePassword) : findFirstVisible(insidePassword);
            if (pwd != null) { try { pwd.clear(); pwd.sendKeys(password); TestListener.info("Filled password"); anyFilled = true; } catch (Exception ex) { TestListener.info("Failed to fill password: " + ex.getMessage()); } }
            else { TestListener.info("Password input not found though password provided"); }

            // first name
            WebElement fn = (form != null) ? findVisibleWithin(form, insideFirstName) : findFirstVisible(insideFirstName);
            if (fn != null && firstName != null) { fn.clear(); fn.sendKeys(firstName); TestListener.info("Filled first name"); anyFilled = true; }

            // last name
            WebElement ln = (form != null) ? findVisibleWithin(form, insideLastName) : findFirstVisible(insideLastName);
            if (ln != null && lastName != null) { ln.clear(); ln.sendKeys(lastName); TestListener.info("Filled last name"); anyFilled = true; }

            // address
            WebElement addr = (form != null) ? findVisibleWithin(form, insideAddress) : findFirstVisible(insideAddress);
            if (addr != null && address != null) { addr.clear(); addr.sendKeys(address); TestListener.info("Filled address"); anyFilled = true; }

            // country (select)
            WebElement ctry = (form != null) ? findVisibleWithin(form, insideCountry) : findFirstVisible(insideCountry);
            if (ctry != null && country != null && !country.isEmpty()) {
                try { ctry.sendKeys(country); TestListener.info("Selected country: " + country); anyFilled = true; } catch (Exception ex) { TestListener.info("Country select sendKeys failed: " + ex.getMessage()); }
            }

            // state
            WebElement st = (form != null) ? findVisibleWithin(form, insideState) : findFirstVisible(insideState);
            if (st != null && state != null) { st.clear(); st.sendKeys(state); TestListener.info("Filled state"); anyFilled = true; }

            // city
            WebElement ct = (form != null) ? findVisibleWithin(form, insideCity) : findFirstVisible(insideCity);
            if (ct != null && city != null) { ct.clear(); ct.sendKeys(city); TestListener.info("Filled city"); anyFilled = true; }

            // zipcode
            WebElement zip = (form != null) ? findVisibleWithin(form, insideZipcode) : findFirstVisible(insideZipcode);
            if (zip != null && zipcode != null) { zip.clear(); zip.sendKeys(zipcode); TestListener.info("Filled zipcode"); anyFilled = true; }

            // mobile
            WebElement mob = (form != null) ? findVisibleWithin(form, insideMobile) : findFirstVisible(insideMobile);
            if (mob != null && mobile != null) { mob.clear(); mob.sendKeys(mobile); TestListener.info("Filled mobile"); anyFilled = true; }

            // DOB selects: try to set if present
            WebElement dEl = (form != null) ? findVisibleWithin(form, dobDay) : findFirstVisible(dobDay);
            WebElement mEl = (form != null) ? findVisibleWithin(form, dobMonth) : findFirstVisible(dobMonth);
            WebElement yEl = (form != null) ? findVisibleWithin(form, dobYear) : findFirstVisible(dobYear);
            if (dEl != null && day != null && !day.isEmpty()) { try { dEl.sendKeys(day); TestListener.info("Set DOB day -> " + day); anyFilled = true; } catch (Exception ex) { TestListener.info("DOB day set failed: " + ex.getMessage()); } }
            if (mEl != null && month != null && !month.isEmpty()) { try { mEl.sendKeys(month); TestListener.info("Set DOB month -> " + month); anyFilled = true; } catch (Exception ex) { TestListener.info("DOB month set failed: " + ex.getMessage()); } }
            if (yEl != null && year != null && !year.isEmpty()) { try { yEl.sendKeys(year); TestListener.info("Set DOB year -> " + year); anyFilled = true; } catch (Exception ex) { TestListener.info("DOB year set failed: " + ex.getMessage()); } }

            // checkboxes: newsletter/offers
            if (newsletterFlag) {
                WebElement newsBox = (form != null) ? findVisibleWithin(form, checkboxNewsletter) : findFirstVisible(checkboxNewsletter);
                if (newsBox != null) {
                    try { if (!newsBox.isSelected()) newsBox.click(); TestListener.info("Checked newsletter checkbox"); anyFilled = true; } catch (Exception ex) { TestListener.info("Newsletter click failed: " + ex.getMessage()); }
                } else {
                    TestListener.info("Newsletter checkbox not found in form.");
                }
            }

            if (offersFlag) {
                WebElement offBox = (form != null) ? findVisibleWithin(form, checkboxOffers) : findFirstVisible(checkboxOffers);
                if (offBox != null) {
                    try { if (!offBox.isSelected()) offBox.click(); TestListener.info("Checked offers checkbox"); anyFilled = true; } catch (Exception ex) { TestListener.info("Offers click failed: " + ex.getMessage()); }
                } else {
                    TestListener.info("Offers checkbox not found in form.");
                }
            }

            if (!anyFilled) TestListener.info("No registration fields were filled (form fields may be missing).");
            return anyFilled;
        } catch (Exception e) {
            TestListener.fail("Exception while filling registration: " + e.getMessage());
            return false;
        }
    }

    public boolean submitRegistration() {
        WebElement form = waitForRegistrationFormOrPassword();
        if (form == null) {
            TestListener.fail("Registration form not present for submit.");
            return false;
        }

        try {
            WebElement submit = null;
            if (form != null && form.getTagName().equalsIgnoreCase("form")) {
                submit = findVisibleWithin(form, insideCreateAcctBtn);
            }
            if (submit == null) {
                List<WebElement> buttons = driver.findElements(insideCreateAcctBtn);
                for (WebElement b : buttons) if (b.isDisplayed() && b.isEnabled()) { submit = b; break; }
            }
            if (submit == null) {
                List<WebElement> fallbackBtns = driver.findElements(By.cssSelector("button[type='submit'], input[type='submit']"));
                for (WebElement b : fallbackBtns) if (b.isDisplayed() && b.isEnabled()) { submit = b; break; }
            }

            if (submit == null) { TestListener.fail("No submit/create account button found."); return false; }

            try { submit.click(); } catch (Exception ex) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit); }
            TestListener.info("Clicked Create Account button.");

            boolean ok = wait.until(d -> {
                try {
                    String cur = d.getCurrentUrl();
                    if (cur != null && (!cur.contains("/register") && !cur.contains("/signup") && !cur.contains("/login"))) return true;
                } catch (Exception ignored) {}
                try { List<WebElement> succ = d.findElements(successAlert); for (WebElement s : succ) if (s.isDisplayed()) return true; } catch (Exception ignored) {}
                try { List<WebElement> acct = d.findElements(accountArea); for (WebElement a : acct) if (a.isDisplayed()) return true; } catch (Exception ignored) {}
                try { List<WebElement> out = d.findElements(logoutLink); for (WebElement o : out) if (o.isDisplayed()) return true; } catch (Exception ignored) {}
                return false;
            });

            if (ok) { TestListener.pass("Registration success detected."); return true; }
            else { TestListener.fail("Registration did not show success indicators."); return false; }
        } catch (TimeoutException te) {
            TestListener.fail("Timed out waiting for registration success: " + te.getMessage()); return false;
        } catch (Exception e) {
            TestListener.fail("Exception during registration submit: " + e.getMessage()); return false;
        }
    }

    // helper: find first visible element for locator (global)
    private WebElement findFirstVisible(By locator) {
        try {
            List<WebElement> els = driver.findElements(locator);
            if (els == null || els.isEmpty()) return null;
            for (WebElement e : els) if (e.isDisplayed()) return e;
            return els.get(0);
        } catch (Exception e) { return null; }
    }

    private WebElement findVisibleWithin(WebElement root, By locator) {
        try {
            List<WebElement> els = root.findElements(locator);
            if (els == null || els.isEmpty()) return null;
            for (WebElement e : els) if (e.isDisplayed()) return e;
            return els.get(0);
        } catch (Exception e) { return null; }
    }
}
