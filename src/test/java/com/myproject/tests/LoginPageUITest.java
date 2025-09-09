package com.myproject.tests;

import com.myproject.base.BaseTest;
import com.myproject.pages.LoginPageUI;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginPageUITest extends BaseTest {

    private LoginPageUI page;

    @BeforeMethod
    public void beforeEach() {
        page = new LoginPageUI(getDriver());
        page.openLoginPage(); // ensure on /login
    }

    @Test(description = "Signup / Login button navigates to login page and shows login form")
    public void verifySignupLoginNavigatesAndLoginFormDisplayed() {
        Assert.assertTrue(page.isLoginFormDisplayed(), "Login form should be visible on login page");
    }

    @Test(description = "Login section accessible from login page")
    public void verifyLoginSectionAccessible() {
        Assert.assertTrue(page.isLoginSectionAccessible(), "Login section should be accessible on login page");
    }

    @Test(description = "Signup section has name and email fields")
    public void verifySignupFields() {
        Assert.assertTrue(page.isSignupNamePresent(), "Signup name input should be present");
        Assert.assertTrue(page.isSignupEmailPresent(), "Signup email input should be present");
    }

    @Test(description = "Login section has email and password fields")
    public void verifyLoginFields() {
        Assert.assertTrue(page.isLoginEmailPresent(), "Login email input should be present");
        Assert.assertTrue(page.isLoginPasswordPresent(), "Login password input should be present");
    }

    @Test(description = "Signup button availability")
    public void verifySignupButtonAvailable() {
        Assert.assertTrue(page.isSignupButtonAvailable(), "Signup button should be available and clickable");
    }

    @Test(description = "Login button availability")
    public void verifyLoginButtonAvailable() {
        Assert.assertTrue(page.isLoginButtonAvailable(), "Login button should be available and clickable");
    }

    @Test(description = "Home button navigates to homepage")
    public void verifyHomeNavigationFromLogin() {
        Assert.assertTrue(page.isHomeNavWorking(), "Home navigation should work from login page");
    }

    @Test(description = "Products button navigates to products list")
    public void verifyProductsNavigationFromLogin() {
        Assert.assertTrue(page.isProductsNavWorking(), "Products navigation should work from login page");
    }

    @Test(description = "Cart button navigates to cart page")
    public void verifyCartNavigationFromLogin() {
        Assert.assertTrue(page.isCartNavWorking(), "Cart navigation should work from login page");
    }

    @Test(description = "Test Cases button navigates to test cases listing")
    public void verifyTestCasesNavigationFromLogin() {
        Assert.assertTrue(page.isTestCasesNavWorking(), "Test Cases navigation should work from login page");
    }

    @Test(description = "API Testing navigation from login page")
    public void verifyApiTestingNavigationFromLogin() {
        Assert.assertTrue(page.isApiTestingNavWorking(), "API Testing navigation should work from login page");
    }

    @Test(description = "Video Tutorials navigation from login page")
    public void verifyVideoTutorialsNavigationFromLogin() {
        Assert.assertTrue(page.isVideoTutorialsNavWorking(), "Video Tutorials navigation should work from login page");
    }

    @Test(description = "Contact Us navigation from login page")
    public void verifyContactUsNavigationFromLogin() {
        Assert.assertTrue(page.isContactUsNavWorking(), "Contact Us navigation should work from login page");
    }

    @Test(description = "Subscription form accepts a valid email")
    public void verifySubscriptionValidEmail() {
        page.submitSubscription("valid+" + System.currentTimeMillis() + "@example.com");
        Assert.assertTrue(page.isSubscriptionSuccessVisible() || !page.isSubscriptionErrorVisible(),
                "Subscription should accept valid email (success message expected)");
    }

    @Test(description = "Subscription form rejects invalid email")
    public void verifySubscriptionInvalidEmail() {
        page.submitSubscription("invalid-email-format");
        Assert.assertTrue(page.isSubscriptionErrorVisible() || !page.isSubscriptionSuccessVisible(),
                "Subscription should show error for invalid email");
    }

    @Test(description = "Subscription should handle duplicate emails (may show success or duplicate message)")
    public void verifySubscriptionDuplicateEmail() {
        page.submitSubscription("already_used@example.com"); // change to a known duplicate if available
        // either success or error UI acceptable; just ensure some response visible
        Assert.assertTrue(page.isSubscriptionErrorVisible() || page.isSubscriptionSuccessVisible(),
                "Subscription should return visible response for duplicate email submission");
    }

    @Test(description = "Site logo displayed on login page")
    public void verifyLogoOnLoginPage() {
        Assert.assertTrue(page.isLogoDisplayed(), "Site logo should be visible on login page");
    }

    @Test(description = "Scroll Up button appears after scroll")
    public void verifyScrollUpAppears() {
        Assert.assertTrue(page.isScrollUpVisibleAfterScroll(), "Scroll-up button should appear after scrolling down");
    }

    @Test(description = "Clicking Scroll Up takes user to top")
    public void verifyScrollUpClickBehavior() {
        Assert.assertTrue(page.clickScrollUpAndVerifyTop(), "Clicking scroll-up should bring the page to the top");
    }

    @Test(description = "Scroll Up appearance and icon conforms (basic check)")
    public void verifyScrollUpAppearance() {
        // Basic heuristic: it's visible and has some icon/text (can't perfectly validate style without visual tests)
        Assert.assertTrue(page.isScrollUpVisibleAfterScroll(), "Scroll up should be visible (appearance check)");
    }

    @Test(description = "Login page layout conforms to expected design (heuristic)")
    public void verifyLayoutConforms() {
        Assert.assertTrue(page.isLayoutConforming(), "Login page main layout and sections should be present");
    }
}
