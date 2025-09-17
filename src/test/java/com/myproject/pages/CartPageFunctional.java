package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPageFunctional {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // URLs
    private final String cartUrl = "https://www.automationexercise.com/view_cart";

    // Locators
    private final By cartTable = By.cssSelector(".cart_info, .cart-table, .table-responsive");
    private final By emptyCartMsg = By.xpath("//*[contains(text(),'empty') or contains(text(),'Cart is empty')]");
    private final By removeBtn = By.cssSelector(".cart_quantity_delete, .remove_cart_item, a.cart_quantity_delete");
    private final By proceedToCheckoutBtn = By.cssSelector(".btn.btn-default.check_out, a[href*='checkout']");
    private final By continueShoppingBtn = By.cssSelector(".btn.btn-default, a[href='/products']");
    private final By productName = By.cssSelector(".cart_description h4 a, .cart_description a");
    private final By productQuantity = By.cssSelector(".cart_quantity_input, .quantity input");
    private final By productPrice = By.cssSelector(".cart_price, .product-price");
    private final By totalPrice = By.cssSelector("#cart_info_table td.cart_total, .total_price");
    private final By cartRows = By.cssSelector("#cart_info_table tbody tr, .cart_item");
    private final By couponInput = By.cssSelector("input[name='coupon_code'], #coupon, input[name='coupon']");
    private final By applyCouponBtn = By.cssSelector("button[name='apply_coupon'], #apply_coupon, button.apply-coupon");
    private final By successOrErrorMsg = By.cssSelector(".alert-success, .alert-danger, .alert");

    // Scroll-up
    private final By scrollUp = By.cssSelector("#scrollUp, .scroll-up, .back-to-top");

    public CartPageFunctional(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- Helper ---
    private void logStep(String msg) { TestListener.info(msg); }

    private WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void openCartPage() {
        logStep("Navigating to Cart page: " + cartUrl);
        driver.get(cartUrl);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    // --- Functional checks ---
    public boolean isCartPageDisplayed() {
        logStep("Checking cart table visibility");
        return !driver.findElements(cartTable).isEmpty();
    }

    public boolean isEmptyCartMessageDisplayed() {
        logStep("Checking empty cart message");
        return !driver.findElements(emptyCartMsg).isEmpty();
    }

    public boolean isProductListedInCart() {
        logStep("Checking if any product rows exist in cart");
        return !driver.findElements(cartRows).isEmpty();
    }

    public boolean verifyProductNameVisible() {
        logStep("Checking product name visibility");
        return !driver.findElements(productName).isEmpty();
    }

    public boolean verifyProductQuantityVisible() {
        logStep("Checking product quantity input visibility");
        return !driver.findElements(productQuantity).isEmpty();
    }

    public boolean verifyProductPriceVisible() {
        logStep("Checking product price visibility");
        return !driver.findElements(productPrice).isEmpty();
    }

    public boolean verifyTotalPriceVisible() {
        logStep("Checking total price visibility");
        return !driver.findElements(totalPrice).isEmpty();
    }

    public boolean removeFirstItemFromCart() {
        logStep("Attempting to remove first item from cart");
        List<WebElement> buttons = driver.findElements(removeBtn);
        if (buttons.isEmpty()) return false;
        buttons.get(0).click();
        return true;
    }

    public boolean clickProceedToCheckout() {
        logStep("Clicking Proceed To Checkout button");
        List<WebElement> btns = driver.findElements(proceedToCheckoutBtn);
        if (btns.isEmpty()) return false;
        btns.get(0).click();
        return driver.getCurrentUrl().contains("checkout");
    }

    public boolean clickContinueShopping() {
        logStep("Clicking Continue Shopping button");
        List<WebElement> btns = driver.findElements(continueShoppingBtn);
        if (btns.isEmpty()) return false;
        btns.get(0).click();
        return driver.getCurrentUrl().contains("/products");
    }

    public boolean applyCouponCode(String code) {
        logStep("Applying coupon code: " + code);
        try {
            WebElement input = waitVisible(couponInput);
            input.clear();
            input.sendKeys(code);
            driver.findElement(applyCouponBtn).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(successOrErrorMsg));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickScrollUpAndVerifyTop() {
        logStep("Scrolling down to trigger scroll-up button");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        List<WebElement> btns = driver.findElements(scrollUp);
        if (btns.isEmpty()) return false;
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btns.get(0));
        return wait.until(d -> ((JavascriptExecutor)d).executeScript("return window.pageYOffset").toString().equals("0"));
    }
}
