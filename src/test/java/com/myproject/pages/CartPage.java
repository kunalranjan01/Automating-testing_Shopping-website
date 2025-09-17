package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final String baseUrl = "https://www.automationexercise.com";
    private final String cartUrl = baseUrl + "/view_cart";
    private final String productsUrl = baseUrl + "/products";
    private final String homeUrl = baseUrl + "/";

    // Nav locators (simple; if text differs adjust)
    private final By navHome = By.linkText("Home");
    private final By navProducts = By.cssSelector("a[href='/products'], a[href='/products/']");
    private final By navCart = By.linkText("Cart");
    private final By navSignup = By.linkText("Signup / Login");
    private final By navTestCases = By.linkText("Test Cases");
    private final By navApi = By.linkText("API Testing");
    private final By navVideos = By.linkText("Video Tutorials");
    private final By navContact1 = By.linkText("Contact us");
    private final By navContact2 = By.linkText("Contact Us");

    // cart / product locators
    private final By cartEmptyMsg = By.xpath("//*[contains(text(),'Cart is empty') or contains(text(),'Your cart is empty')]");
    private final By cartRows = By.cssSelector(".cart_info tbody tr, .cart_item, .cart-table tbody tr");
    private final By addToCartBtn = By.cssSelector(".add-to-cart, a.add-to-cart, .btn.add-to-cart, .btn.btn-default.add-to-cart");
    private final By removeBtn = By.cssSelector(".cart_quantity_delete, .remove_cart_item, .cart-remove, a.remove");
    private final By proceedCheckout =By.cssSelector("input[value*='Proceed To Checkout'], button[value*='Proceed To Checkout'], button:contains('Proceed To Checkout')");
    private final By couponInput = By.cssSelector("input[name='coupon'], input#coupon, input[name='code']");
    private final By couponApply = By.cssSelector("button#apply_coupon, input[value='Apply'], .apply-coupon");
    private final By cartCountBadge = By.cssSelector(".cart-count, .badge.cart, .cart_quantity");
    private final By logo = By.cssSelector(".logo, .navbar-brand img");
    private final By footerCopyright = By.xpath("//*[contains(text(),'All rights reserved') or contains(text(),'Copyright')]");
    private final By continueBtn = By.xpath("//a[contains(.,'Continue Shopping') or contains(.,'Continue to cart') or contains(.,'Continue')]");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- Logging helper ---
    private void logStep(String message) {
        TestListener.info(message);
    }

    // ---------- Navigation and waits ----------
    public void openCartPage() {
        logStep("Action: openCartPage() - navigating to " + cartUrl);
        try {
            driver.get(cartUrl);
            waitForReady();
            logStep("Navigation to cart page complete: " + driver.getCurrentUrl());
        } catch (Exception e) {
            logStep("Exception navigating to cart page: " + e.getMessage());
            throw new RuntimeException("Failed to open cart page", e);
        }
    }

    public void openProductsPage() {
        logStep("Action: openProductsPage() - navigating to " + productsUrl);
        try {
            driver.get(productsUrl);
            waitForReady();
            logStep("Navigation to products page complete: " + driver.getCurrentUrl());
        } catch (Exception e) {
            logStep("Exception navigating to products page: " + e.getMessage());
            throw new RuntimeException("Failed to open products page", e);
        }
    }

    public void openHomePage() {
        logStep("Action: openHomePage() - navigating to " + homeUrl);
        try {
            driver.get(homeUrl);
            waitForReady();
            logStep("Navigation to home page complete: " + driver.getCurrentUrl());
        } catch (Exception e) {
            logStep("Exception navigating to home page: " + e.getMessage());
            throw new RuntimeException("Failed to open home page", e);
        }
    }

    private void waitForReady() {
        try {
            wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
            logStep("Document readyState=complete");
        } catch (Exception e) {
            logStep("waitForReady() finished with exception (ignored): " + e.getMessage());
        }
    }

    private void safeClick(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            wait.until(ExpectedConditions.elementToBeClickable(el));
            el.click();
            logStep("Performed safeClick on element: " + describeElement(el));
        } catch (Exception e) {
            logStep("Normal click failed, attempting JS click: " + e.getMessage());
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                logStep("Performed JS click on element: " + describeElement(el));
            } catch (Exception ex) {
                logStep("JS click also failed: " + ex.getMessage());
                throw new RuntimeException("safeClick failed", ex);
            }
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private void closeModalIfPresent() {
        try {
            List<WebElement> close = driver.findElements(By.cssSelector(".close-modal, .modal .close, .dismiss, .close, .btn.close"));
            if (!close.isEmpty()) {
                safeClick(close.get(0));
                sleep(300);
                logStep("Closed a modal (if present)");
            }
        } catch (Exception e) {
            logStep("Exception while trying to close modal: " + e.getMessage());
        }
    }

    // ---------- Checks usable on empty cart ----------
    public boolean isCartAccessibleViaUrl() {
        logStep("Check: isCartAccessibleViaUrl()");
        try {
            openCartPage();
            boolean ok = driver.getCurrentUrl().contains("/view_cart");
            logStep("Result: cart URL contains /view_cart -> " + ok);
            return ok;
        } catch (Exception e) {
            logStep("Exception in isCartAccessibleViaUrl: " + e.getMessage());
            return false;
        }
    }

    public boolean isEmptyCartMessageDisplayed() {
        logStep("Check: isEmptyCartMessageDisplayed()");
        try {
            openCartPage();
            boolean present = !driver.findElements(cartEmptyMsg).isEmpty();
            logStep("Result: empty cart message present -> " + present);
            return present;
        } catch (Exception e) {
            logStep("Exception in isEmptyCartMessageDisplayed: " + e.getMessage());
            return false;
        }
    }

    public boolean isCartLayoutPresent() {
        logStep("Check: isCartLayoutPresent()");
        try {
            openCartPage();
            boolean present = !driver.findElements(By.cssSelector(".cart_info, .cart-table, .cart-items")).isEmpty();
            logStep("Result: cart layout present -> " + present);
            return present;
        } catch (Exception e) {
            logStep("Exception in isCartLayoutPresent: " + e.getMessage());
            return false;
        }
    }

    public boolean isLogoPresent() {
        logStep("Check: isLogoPresent()");
        boolean present = !driver.findElements(logo).isEmpty();
        logStep("Result: logo present -> " + present);
        return present;
    }

    public boolean isFooterCopyrightPresent() {
        logStep("Check: isFooterCopyrightPresent()");
        try {
            openCartPage();
            boolean present = !driver.findElements(footerCopyright).isEmpty();
            logStep("Result: footer copyright present -> " + present);
            return present;
        } catch (Exception e) {
            logStep("Exception in isFooterCopyrightPresent: " + e.getMessage());
            return false;
        }
    }

    public boolean basicTabNavigationWorks() {
        logStep("Check: basicTabNavigationWorks()");
        try {
            openCartPage();
            WebElement body = driver.findElement(By.tagName("body"));
            body.sendKeys(Keys.TAB);
            Object active = ((JavascriptExecutor) driver).executeScript("return document.activeElement && document.activeElement.tagName;");
            boolean ok = active != null;
            logStep("Result: active element after TAB -> " + active);
            return ok;
        } catch (Exception e) {
            logStep("Exception in basicTabNavigationWorks: " + e.getMessage());
            return false;
        }
    }

    public boolean isCouponFeaturePresent() {
        logStep("Check: isCouponFeaturePresent()");
        try {
            openCartPage();
            boolean present = !driver.findElements(couponInput).isEmpty() && !driver.findElements(couponApply).isEmpty();
            logStep("Result: coupon feature present -> " + present);
            return present;
        } catch (Exception e) {
            logStep("Exception in isCouponFeaturePresent: " + e.getMessage());
            return false;
        }
    }

    // ---------- Nav-button click & verify (operate from Cart page) ----------
    private WebElement findNav(By locator) {
        try {
            List<WebElement> els = driver.findElements(locator);
            if (els.isEmpty()) return null;
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) return e;
            return els.get(0);
        } catch (Exception e) {
            logStep("Exception in findNav for " + locator + ": " + e.getMessage());
            return null;
        }
    }

    // click nav, verify target using URL fragment or known check, then navigate back to cart
    public boolean clickHomeAndVerifyThenBack() {
        logStep("Action: clickHomeAndVerifyThenBack()");
        try {
            openCartPage();
            WebElement el = findNav(navHome); if (el == null) { logStep("Home nav not found"); return false; }
            safeClick(el);
            boolean ok = driver.getCurrentUrl().equalsIgnoreCase(homeUrl) || driver.getCurrentUrl().contains(baseHostFragment(homeUrl));
            logStep("Result: clickHomeAndVerifyThenBack -> " + ok + " currentUrl=" + driver.getCurrentUrl());
            openCartPage(); return ok;
        } catch (Exception e) {
            logStep("Exception in clickHomeAndVerifyThenBack: " + e.getMessage());
            return false;
        }
    }

    public boolean clickProductsAndVerifyThenBack() {
        logStep("Action: clickProductsAndVerifyThenBack()");
        try {
            openCartPage();
            WebElement el = findNav(navProducts); if (el == null) { logStep("Products nav not found"); return false; }
            safeClick(el);
            boolean ok = driver.getCurrentUrl().contains("/products");
            logStep("Result: clickProductsAndVerifyThenBack -> " + ok + " currentUrl=" + driver.getCurrentUrl());
            openCartPage(); return ok;
        } catch (Exception e) {
            logStep("Exception in clickProductsAndVerifyThenBack: " + e.getMessage());
            return false;
        }
    }

    public boolean clickCartAndVerifyThenBack() {
        logStep("Action: clickCartAndVerifyThenBack()");
        try {
            openCartPage();
            WebElement el = findNav(navCart); if (el == null) { logStep("Cart nav not found"); return false; }
            safeClick(el);
            boolean ok = driver.getCurrentUrl().contains("/view_cart");
            logStep("Result: clickCartAndVerifyThenBack -> " + ok + " currentUrl=" + driver.getCurrentUrl());
            openCartPage(); return ok;
        } catch (Exception e) {
            logStep("Exception in clickCartAndVerifyThenBack: " + e.getMessage());
            return false;
        }
    }

    public boolean clickSignupLoginAndVerifyThenBack() {
        logStep("Action: clickSignupLoginAndVerifyThenBack()");
        try {
            openCartPage();
            WebElement el = findNav(navSignup); if (el == null) { logStep("Signup nav not found"); return false; }
            safeClick(el);
            boolean ok = driver.getCurrentUrl().contains("/login");
            logStep("Result: clickSignupLoginAndVerifyThenBack -> " + ok + " currentUrl=" + driver.getCurrentUrl());
            openCartPage(); return ok;
        } catch (Exception e) {
            logStep("Exception in clickSignupLoginAndVerifyThenBack: " + e.getMessage());
            return false;
        }
    }

    public boolean clickTestCasesAndVerifyThenBack() {
        logStep("Action: clickTestCasesAndVerifyThenBack()");
        try {
            openCartPage();
            WebElement el = findNav(navTestCases); if (el == null) { logStep("TestCases nav not found"); return false; }
            safeClick(el);
            boolean ok = driver.getCurrentUrl().contains("/test_cases");
            logStep("Result: clickTestCasesAndVerifyThenBack -> " + ok + " currentUrl=" + driver.getCurrentUrl());
            openCartPage(); return ok;
        } catch (Exception e) {
            logStep("Exception in clickTestCasesAndVerifyThenBack: " + e.getMessage());
            return false;
        }
    }

    public boolean clickApiTestingAndVerifyThenBack() {
        logStep("Action: clickApiTestingAndVerifyThenBack()");
        try {
            openCartPage();
            WebElement el = findNav(navApi); if (el == null) { logStep("API nav not found"); return false; }
            safeClick(el);
            boolean ok = driver.getCurrentUrl().toLowerCase().contains("api");
            logStep("Result: clickApiTestingAndVerifyThenBack -> " + ok + " currentUrl=" + driver.getCurrentUrl());
            openCartPage(); return ok;
        } catch (Exception e) {
            logStep("Exception in clickApiTestingAndVerifyThenBack: " + e.getMessage());
            return false;
        }
    }

    public boolean isOutOfStockBehaviorVisible() {
        logStep("Check: isOutOfStockBehaviorVisible()");
        try {
            openProductsPage();
            // Look for any text containing "Out of stock"
            List<WebElement> outs = driver.findElements(
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'out of stock')]")
            );
            boolean res = !outs.isEmpty();
            logStep("Result: isOutOfStockBehaviorVisible -> " + res);
            return res;
        } catch (Exception e) {
            logStep("Exception in isOutOfStockBehaviorVisible: " + e.getMessage());
            return false;
        }
    }

    public boolean clickVideoTutorialsAndVerifyThenBack() {
        logStep("Action: clickVideoTutorialsAndVerifyThenBack()");
        try {
            openCartPage();
            WebElement el = findNav(navVideos); if (el == null) { logStep("Video Tutorials nav not found"); return false; }
            safeClick(el);
            boolean ok = driver.getPageSource().toLowerCase().contains("video") || driver.getCurrentUrl().toLowerCase().contains("tutorial");
            logStep("Result: clickVideoTutorialsAndVerifyThenBack -> " + ok + " currentUrl=" + driver.getCurrentUrl());
            openCartPage(); return ok;
        } catch (Exception e) {
            logStep("Exception in clickVideoTutorialsAndVerifyThenBack: " + e.getMessage());
            return false;
        }
    }

    public boolean clickContactUsAndVerifyThenBack() {
        logStep("Action: clickContactUsAndVerifyThenBack()");
        try {
            openCartPage();
            WebElement el = findNav(navContact1); if (el == null) el = findNav(navContact2);
            if (el == null) { logStep("Contact nav not found"); return false; }
            safeClick(el);
            boolean ok = driver.getCurrentUrl().contains("/contact_us");
            logStep("Result: clickContactUsAndVerifyThenBack -> " + ok + " currentUrl=" + driver.getCurrentUrl());
            openCartPage(); return ok;
        } catch (Exception e) {
            logStep("Exception in clickContactUsAndVerifyThenBack: " + e.getMessage());
            return false;
        }
    }

    // ---------- Product-related helpers ----------
    public boolean addFirstProductFromHomeOrProducts() {
        logStep("Action: addFirstProductFromHomeOrProducts()");
        try {
            try { openHomePage(); } catch (Exception ignored) {}
            List<WebElement> addBtns = driver.findElements(addToCartBtn);
            if (addBtns.isEmpty()) {
                openProductsPage();
                addBtns = driver.findElements(addToCartBtn);
            }
            for (WebElement b : addBtns) {
                if (b.isDisplayed() && b.isEnabled()) {
                    try { safeClick(b); } catch (Exception ignored) {}
                    sleep(600);
                    closeModalIfPresent();
                    logStep("Added a product using add-to-cart button");
                    return true;
                }
            }
            logStep("No add-to-cart buttons found to add products");
            return false;
        } catch (Exception e) {
            logStep("Exception in addFirstProductFromHomeOrProducts: " + e.getMessage());
            return false;
        }
    }

    public boolean addMultipleProductsFromProducts(int n) {
        logStep("Action: addMultipleProductsFromProducts(" + n + ")");
        try {
            openProductsPage();
            int added = 0;
            for (WebElement b : driver.findElements(addToCartBtn)) {
                if (added >= n) break;
                if (b.isDisplayed()) {
                    safeClick(b);
                    added++;
                    sleep(500);
                    closeModalIfPresent();
                }
            }
            logStep("Result: added " + added + " products (requested " + n + ")");
            return added >= n;
        } catch (Exception e) {
            logStep("Exception in addMultipleProductsFromProducts: " + e.getMessage());
            return false;
        }
    }

    public int getCartItemsCount() {
        logStep("Action: getCartItemsCount()");
        try {
            openCartPage();
            int count = driver.findElements(cartRows).size();
            logStep("Result: cart items count -> " + count);
            return count;
        } catch (Exception e) {
            logStep("Exception in getCartItemsCount: " + e.getMessage());
            return 0;
        }
    }

    public boolean removeFirstCartItem() {
        logStep("Action: removeFirstCartItem()");
        try {
            openCartPage();
            List<WebElement> rem = driver.findElements(removeBtn);
            if (rem.isEmpty()) { logStep("No remove button found"); return false; }
            safeClick(rem.get(0));
            sleep(500);
            logStep("Removed first cart item");
            return true;
        } catch (Exception e) {
            logStep("Exception in removeFirstCartItem: " + e.getMessage());
            return false;
        }
    }

    public boolean clickProceedToCheckout() {
        logStep("Action: clickProceedToCheckout()");
        try {
            openCartPage();
            List<WebElement> els = driver.findElements(proceedCheckout);
            if (els.isEmpty()) { logStep("No proceed to checkout element found"); return false; }
            safeClick(els.get(0));
            waitForReady();
            boolean ok = driver.getCurrentUrl().toLowerCase().contains("checkout") || driver.getCurrentUrl().toLowerCase().contains("payment");
            logStep("Result: clickProceedToCheckout -> " + ok + " currentUrl=" + driver.getCurrentUrl());
            return ok;
        } catch (Exception e) {
            logStep("Exception in clickProceedToCheckout: " + e.getMessage());
            return false;
        }
    }

    public boolean clickContinueToProducts() {
        logStep("Action: clickContinueToProducts()");
        try {
            openCartPage();
            List<WebElement> els = driver.findElements(continueBtn);
            if (els.isEmpty()) { logStep("No continue button found"); return false; }
            safeClick(els.get(0));
            waitForReady();
            boolean ok = driver.getCurrentUrl().contains("/products") || !driver.getCurrentUrl().contains("/view_cart");
            logStep("Result: clickContinueToProducts -> " + ok + " currentUrl=" + driver.getCurrentUrl());
            return ok;
        } catch (Exception e) {
            logStep("Exception in clickContinueToProducts: " + e.getMessage());
            return false;
        }
    }

    public boolean applyCoupon(String code) {
        logStep("Action: applyCoupon(" + code + ")");
        try {
            openCartPage();
            if (driver.findElements(couponInput).isEmpty() || driver.findElements(couponApply).isEmpty()) {
                logStep("Coupon UI not present");
                return false;
            }
            WebElement in = driver.findElement(couponInput);
            in.clear();
            in.sendKeys(code);
            safeClick(driver.findElement(couponApply));
            sleep(700);
            boolean alerts = !driver.findElements(By.cssSelector(".alert, .success, .error")).isEmpty();
            logStep("Result: applyCoupon -> alertPresent=" + alerts);
            return alerts;
        } catch (Exception e) {
            logStep("Exception in applyCoupon: " + e.getMessage());
            return false;
        }
    }

    public boolean isCartCountBadgeUpdating() {
        logStep("Action: isCartCountBadgeUpdating()");
        try {
            String before = getCartCount();
            addFirstProductFromHomeOrProducts();
            String after = getCartCount();
            boolean changed = !after.equals(before);
            logStep("Result: isCartCountBadgeUpdating -> before='" + before + "' after='" + after + "' -> changed=" + changed);
            return changed;
        } catch (Exception e) {
            logStep("Exception in isCartCountBadgeUpdating: " + e.getMessage());
            return false;
        }
    }

    public String getCartCount() {
        logStep("Action: getCartCount()");
        try {
            List<WebElement> b = driver.findElements(cartCountBadge);
            String val = b.isEmpty() ? "" : b.get(0).getText().trim();
            logStep("Result: cart count -> '" + val + "'");
            return val;
        } catch (Exception e) {
            logStep("Exception in getCartCount: " + e.getMessage());
            return "";
        }
    }

    // helpers used by tests
    public boolean clearCart() {
        logStep("Action: clearCart()");
        try {
            openCartPage();
            List<WebElement> rem = driver.findElements(removeBtn);
            for (WebElement r : rem) {
                safeClick(r);
                sleep(400);
            }
            boolean empty = driver.findElements(cartRows).isEmpty();
            logStep("Result: clearCart -> empty=" + empty);
            return empty;
        } catch (Exception e) {
            logStep("Exception in clearCart: " + e.getMessage());
            return false;
        }
    }

    public void printNavLinks() {
        logStep("Action: printNavLinks()");
        By[] items = new By[]{navHome, navProducts, navCart, navSignup, navTestCases, navApi, navVideos, navContact1, navContact2};
        System.out.println("=== NAV LIST ===");
        for (By loc : items) {
            List<WebElement> els = driver.findElements(loc);
            if (els.isEmpty()) System.out.println(loc + " -> NOT FOUND");
            else for (WebElement el : els) {
                String txt = ""; try { txt = el.getText().trim(); } catch (Exception ignored) {}
                String href = ""; try { href = el.getAttribute("href"); } catch (Exception ignored) {}
                System.out.println(loc + " -> text='" + txt + "' href='" + href + "' displayed=" + el.isDisplayed());
            }
        }
        System.out.println("=== NAV LIST END ===");
    }

    // small util to get host fragment for home compare
    private String baseHostFragment(String url) {
        try {
            java.net.URI u = new java.net.URI(url);
            return u.getHost();
        } catch (Exception e) { return url; }
    }

    private String describeElement(WebElement el) {
        try {
            String t = el.getText();
            String h = el.getAttribute("href");
            return "text='" + (t==null?"":t.trim()) + "' href='" + (h==null?"":h) + "'";
        } catch (Exception e) {
            return el.toString();
        }
    }
}
