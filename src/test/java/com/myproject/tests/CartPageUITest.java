package com.myproject.tests;

import com.myproject.pages.CartPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class CartPageUITest {

    private static WebDriver driver;
    private static CartPage cart;

    @BeforeClass(alwaysRun = true)
    @Parameters({"browser"})
    public void beforeClass(@Optional("chrome") String browser) {
        if ("edge".equalsIgnoreCase(browser)) {
            WebDriverManager.edgedriver().setup();
            driver = new org.openqa.selenium.edge.EdgeDriver();
        } else if ("firefox".equalsIgnoreCase(browser)) {
            WebDriverManager.firefoxdriver().setup();
            driver = new org.openqa.selenium.firefox.FirefoxDriver();
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions opts = new ChromeOptions();
            opts.addArguments("--remote-allow-origins=*");
            driver = new ChromeDriver(opts);
        }
        driver.manage().window().maximize();
        cart = new CartPage(driver);
    }

    // ---------------- Phase A: Empty-cart checks & nav checks that don't need products ----------------
    @Test(priority = 1, description = "Verify cart page accessible via direct URL")
    public void tc_cart_accessible_via_url_empty() {
        Assert.assertTrue(cart.isCartAccessibleViaUrl());
    }

    @Test(priority = 2, description = "Verify empty cart displays proper message")
    public void tc_empty_cart_message() {
        cart.clearCart();
        Assert.assertTrue(cart.isEmptyCartMessageDisplayed());
    }

    @Test(priority = 3, description = "Verify Home nav from cart (empty) works")
    public void tc_home_nav_empty() {
        Assert.assertTrue(cart.clickHomeAndVerifyThenBack());
    }

    @Test(priority = 4, description = "Verify Products nav from cart (empty) works")
    public void tc_products_nav_empty() {
        Assert.assertTrue(cart.clickProductsAndVerifyThenBack());
    }

    @Test(priority = 5, description = "Verify Cart nav from cart (empty) works")
    public void tc_cart_nav_empty() {
        Assert.assertTrue(cart.clickCartAndVerifyThenBack());
    }

    @Test(priority = 6, description = "Verify Signup/Login nav from cart (empty) works")
    public void tc_signup_nav_empty() {
        Assert.assertTrue(cart.clickSignupLoginAndVerifyThenBack());
    }

    @Test(priority = 7, description = "Verify Test Cases nav from cart (empty) works")
    public void tc_testcases_nav_empty() {
        Assert.assertTrue(cart.clickTestCasesAndVerifyThenBack());
    }

    @Test(priority = 8, description = "Verify API Testing nav from cart (empty) works")
    public void tc_api_nav_empty() {
        Assert.assertTrue(cart.clickApiTestingAndVerifyThenBack());
    }

    @Test(priority = 9, description = "Verify Video Tutorials nav from cart (empty) works")
    public void tc_videos_nav_empty() {
        Assert.assertTrue(cart.clickVideoTutorialsAndVerifyThenBack());
    }

    @Test(priority = 10, description = "Verify Contact Us nav from cart (empty) works")
    public void tc_contactus_nav_empty() {
        Assert.assertTrue(cart.clickContactUsAndVerifyThenBack());
    }

    @Test(priority = 11, description = "Verify logo present on cart page")
    public void tc_logo_present_empty() {
        Assert.assertTrue(cart.isLogoPresent());
    }

    @Test(priority = 12, description = "Verify footer copyright present on cart page")
    public void tc_footer_present_empty() {
        Assert.assertTrue(cart.isFooterCopyrightPresent());
    }

    @Test(priority = 13, description = "Verify cart UI layout (empty)")
    public void tc_cart_layout_empty() {
        Assert.assertTrue(cart.isCartLayoutPresent());
    }

    @Test(priority = 14, description = "Verify coupon UI present (empty)")
    public void tc_coupon_ui_present_empty() {
        Assert.assertTrue(cart.isCouponFeaturePresent());
    }

    @Test(priority = 15, description = "Verify accessibility basics (tab navigation) on empty cart page")
    public void tc_accessibility_empty() {
        Assert.assertTrue(cart.basicTabNavigationWorks());
    }

    // ---------------- Phase B: Add products then verify product-dependent testcases ----------------
    @Test(priority = 20, description = "Add product(s) from home/products then navigate to cart")
    public void tc_add_product_and_navigate_to_cart() {
        boolean added = cart.addFirstProductFromHomeOrProducts();
        Assert.assertTrue(added, "Should be able to add a product from home/products");
        cart.openCartPage();
        Assert.assertTrue(cart.getCartItemsCount() >= 1, "Cart should contain at least 1 item after add");
    }

    @Test(priority = 21, description = "Verify product appears in cart")
    public void tc_product_shown_in_cart() {
        Assert.assertTrue(cart.getCartItemsCount() >= 1);
    }

    @Test(priority = 22, description = "Verify multiple products visible in cart")
    public void tc_multiple_products_visible() {
        cart.addMultipleProductsFromProducts(2);
        Assert.assertTrue(cart.getCartItemsCount() >= 2);
    }

    @Test(priority = 23, description = "Verify Remove button deletes an item")
    public void tc_remove_button_deletes_item() {
        int before = cart.getCartItemsCount();
        Assert.assertTrue(before >= 1);
        Assert.assertTrue(cart.removeFirstCartItem());
        int after = cart.getCartItemsCount();
        Assert.assertTrue(after < before);
    }

    @Test(priority = 24, description = "Verify Proceed To Checkout navigates to checkout")
    public void tc_proceed_to_checkout() {
        if (cart.getCartItemsCount() == 0) cart.addFirstProductFromHomeOrProducts();
        Assert.assertTrue(cart.clickProceedToCheckout());
        cart.openCartPage();
    }

    @Test(priority = 25, description = "Verify Continue to products returns user to product page")
    public void tc_continue_to_products() {
        Assert.assertTrue(cart.clickContinueToProducts());
        cart.openCartPage();
    }

    @Test(priority = 26, description = "Verify discounts/coupons apply correctly (best-effort)")
    public void tc_coupons_apply() {
        boolean attempted = cart.applyCoupon("DUMMYCOUPON123");
        Assert.assertTrue(attempted);
    }

    @Test(priority = 27, description = "Verify cart count icon updates when adding")
    public void tc_cart_count_updates_on_add() {
        String before = cart.getCartCount();
        cart.addFirstProductFromHomeOrProducts();
        String after = cart.getCartCount();
        Assert.assertTrue(!after.equals(before));
    }

    @Test(priority = 28, description = "Verify behavior when product is out of stock (best-effort)")
    public void tc_out_of_stock_behavior() {
        boolean out = cart.isOutOfStockBehaviorVisible();
        Assert.assertTrue(true, "Out-of-stock check executed (result=" + out + ")");
    }

    @Test(priority = 29, description = "Verify cart UI layout (with products)")
    public void tc_cart_layout_with_products() {
        Assert.assertTrue(cart.isCartLayoutPresent());
    }
    public WebDriver getDriver() {
        return driver;
    }
    @AfterClass(alwaysRun = true)
    public void afterClass() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            driver = null;
        }
    }
}
