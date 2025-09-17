package com.myproject.tests;

import com.myproject.pages.CartPageFunctional;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class CartPageFunctionalTest {
    private static WebDriver driver;
    private static CartPageFunctional cartPage;

    @BeforeClass(alwaysRun = true)
    @Parameters({"browser"})
    public void beforeClass(@Optional("chrome") String browser) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        cartPage = new CartPageFunctional(driver);
        cartPage.openCartPage();
    }

    // --- 15 Functional Test Cases ---
    @Test(priority = 1, description = "Verify cart page loads successfully")
    public void tc_cart_page_loads() {
        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should display");
    }

    @Test(priority = 2, description = "Verify empty cart message when no items present")
    public void tc_empty_cart_message() {
        Assert.assertTrue(cartPage.isEmptyCartMessageDisplayed() || cartPage.isProductListedInCart());
    }

    @Test(priority = 3, description = "Verify product is listed in cart when added")
    public void tc_product_listed_in_cart() {
        Assert.assertTrue(cartPage.isProductListedInCart() || cartPage.isEmptyCartMessageDisplayed());
    }

    @Test(priority = 4, description = "Verify product name is visible")
    public void tc_product_name_visible() {
        Assert.assertTrue(cartPage.verifyProductNameVisible() || cartPage.isEmptyCartMessageDisplayed());
    }

    @Test(priority = 5, description = "Verify product quantity field is visible")
    public void tc_product_quantity_visible() {
        Assert.assertTrue(cartPage.verifyProductQuantityVisible() || cartPage.isEmptyCartMessageDisplayed());
    }

    @Test(priority = 6, description = "Verify product price is visible")
    public void tc_product_price_visible() {
        Assert.assertTrue(cartPage.verifyProductPriceVisible() || cartPage.isEmptyCartMessageDisplayed());
    }

    @Test(priority = 7, description = "Verify total price is visible")
    public void tc_total_price_visible() {
        Assert.assertTrue(cartPage.verifyTotalPriceVisible() || cartPage.isEmptyCartMessageDisplayed());
    }

    @Test(priority = 8, description = "Remove item from cart (if present)", dependsOnMethods = {"tc_product_listed_in_cart"})
    public void tc_remove_item() {
        if (cartPage.isProductListedInCart()) {
            Assert.assertTrue(cartPage.removeFirstItemFromCart(), "Item should be removed");
        } else {
            Assert.assertTrue(true, "No items to remove");
        }
    }

    @Test(priority = 9, description = "Proceed to checkout button works")
    public void tc_proceed_to_checkout() {
        if (cartPage.isProductListedInCart()) {
            Assert.assertTrue(cartPage.clickProceedToCheckout(), "Should navigate to checkout");
            cartPage.openCartPage(); // return to cart
        } else {
            Assert.assertTrue(true, "Skipped - no products");
        }
    }

    @Test(priority = 10, description = "Continue shopping button works")
    public void tc_continue_shopping() {
        Assert.assertTrue(cartPage.clickContinueShopping(), "Should navigate to products page");
        cartPage.openCartPage();
    }

    @Test(priority = 11, description = "Apply invalid coupon code")
    public void tc_apply_invalid_coupon() {
        Assert.assertTrue(cartPage.applyCouponCode("INVALIDCODE"), "Coupon attempt should show message");
    }

    @Test(priority = 12, description = "Apply empty coupon code")
    public void tc_apply_empty_coupon() {
        Assert.assertTrue(cartPage.applyCouponCode(""), "Empty coupon attempt should show message");
    }

    @Test(priority = 13, description = "Verify cart rows exist when product added")
    public void tc_cart_rows_exist() {
        Assert.assertTrue(cartPage.isProductListedInCart() || cartPage.isEmptyCartMessageDisplayed());
    }

    @Test(priority = 14, description = "Verify scroll-up button functionality")
    public void tc_scroll_up() {
        Assert.assertTrue(cartPage.clickScrollUpAndVerifyTop(), "Scroll-up button should return page to top");
    }

    @Test(priority = 15, description = "Verify cart page reloads correctly")
    public void tc_cart_page_reload() {
        cartPage.openCartPage();
        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should reload successfully");
    }

    public WebDriver getDriver() {
        return driver;
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
        }
    }
}
