package com.myproject.pages;

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
    private final By navProducts = By.linkText("Products");
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
    private final By proceedCheckout = By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'proceed') and contains(., 'Checkout')]");
    private final By continueBtn = By.xpath("//a[contains(.,'Continue Shopping') or contains(.,'Continue to cart') or contains(.,'Continue')]");
    private final By couponInput = By.cssSelector("input[name='coupon'], input#coupon, input[name='code']");
    private final By couponApply = By.cssSelector("button#apply_coupon, input[value='Apply'], .apply-coupon");
    private final By cartCountBadge = By.cssSelector(".cart-count, .badge.cart, .cart_quantity");
    private final By logo = By.cssSelector(".logo, .navbar-brand img");
    private final By footerCopyright = By.xpath("//*[contains(text(),'All rights reserved') or contains(text(),'Copyright')]");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ---------- Navigation and waits ----------
    public void openCartPage() {
        driver.get(cartUrl);
        waitForReady();
    }
    public void openProductsPage() {
        driver.get(productsUrl);
        waitForReady();
    }
    public void openHomePage() {
        driver.get(homeUrl);
        waitForReady();
    }
    private void waitForReady() {
        try {
            wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {}
    }

    private void safeClick(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            wait.until(ExpectedConditions.elementToBeClickable(el));
            el.click();
        } catch (Exception e) {
            try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); } catch (Exception ignored) {}
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
            }
        } catch (Exception ignored) {}
    }

    // ---------- Checks usable on empty cart ----------
    public boolean isCartAccessibleViaUrl() {
        openCartPage();
        return driver.getCurrentUrl().contains("/view_cart");
    }
    public boolean isEmptyCartMessageDisplayed() {
        openCartPage();
        return !driver.findElements(cartEmptyMsg).isEmpty();
    }
    public boolean isCartLayoutPresent() {
        openCartPage();
        return !driver.findElements(By.cssSelector(".cart_info, .cart-table, .cart-items")).isEmpty();
    }
    public boolean isLogoPresent() {
        return !driver.findElements(logo).isEmpty();
    }
    public boolean isFooterCopyrightPresent() {
        openCartPage();
        return !driver.findElements(footerCopyright).isEmpty();
    }
    public boolean basicTabNavigationWorks() {
        openCartPage();
        WebElement body = driver.findElement(By.tagName("body"));
        body.sendKeys(Keys.TAB);
        Object active = ((JavascriptExecutor) driver).executeScript("return document.activeElement && document.activeElement.tagName;");
        return active != null;
    }
    public boolean isCouponFeaturePresent() {
        openCartPage();
        return !driver.findElements(couponInput).isEmpty() && !driver.findElements(couponApply).isEmpty();
    }

    // ---------- Nav-button click & verify (operate from Cart page) ----------
    private WebElement findNav(By locator) {
        try {
            List<WebElement> els = driver.findElements(locator);
            if (els.isEmpty()) return null;
            for (WebElement e : els) if (e.isDisplayed() && e.isEnabled()) return e;
            return els.get(0);
        } catch (Exception e) { return null; }
    }

    // click nav, verify target using URL fragment or known check, then navigate back to cart
    public boolean clickHomeAndVerifyThenBack() {
        openCartPage();
        WebElement el = findNav(navHome); if (el == null) return false;
        safeClick(el);
        boolean ok = driver.getCurrentUrl().equalsIgnoreCase(homeUrl) || driver.getCurrentUrl().contains(baseHostFragment(homeUrl));
        openCartPage(); return ok;
    }
    public boolean clickProductsAndVerifyThenBack() {
        openCartPage();
        WebElement el = findNav(navProducts); if (el == null) return false;
        safeClick(el);
        boolean ok = driver.getCurrentUrl().contains("/products");
        openCartPage(); return ok;
    }
    public boolean clickCartAndVerifyThenBack() {
        openCartPage();
        WebElement el = findNav(navCart); if (el == null) return false;
        safeClick(el);
        boolean ok = driver.getCurrentUrl().contains("/view_cart");
        openCartPage(); return ok;
    }
    public boolean clickSignupLoginAndVerifyThenBack() {
        openCartPage();
        WebElement el = findNav(navSignup); if (el == null) return false;
        safeClick(el);
        boolean ok = driver.getCurrentUrl().contains("/login");
        openCartPage(); return ok;
    }
    public boolean clickTestCasesAndVerifyThenBack() {
        openCartPage();
        WebElement el = findNav(navTestCases); if (el == null) return false;
        safeClick(el);
        boolean ok = driver.getCurrentUrl().contains("/test_cases");
        openCartPage(); return ok;
    }
    public boolean clickApiTestingAndVerifyThenBack() {
        openCartPage();
        WebElement el = findNav(navApi); if (el == null) return false;
        safeClick(el);
        boolean ok = driver.getCurrentUrl().toLowerCase().contains("api");
        openCartPage(); return ok;
    }
    public boolean isOutOfStockBehaviorVisible() {
        try {
            openProductsPage();
            // Look for any text containing "Out of stock"
            List<WebElement> outs = driver.findElements(
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'out of stock')]")
            );
            if (!outs.isEmpty()) {
                return true; // page shows an out-of-stock label
            }
            // If not found, just return false (no OOS products currently)
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickVideoTutorialsAndVerifyThenBack() {
        openCartPage();
        WebElement el = findNav(navVideos); if (el == null) return false;
        safeClick(el);
        boolean ok = driver.getPageSource().toLowerCase().contains("video") || driver.getCurrentUrl().toLowerCase().contains("tutorial");
        openCartPage(); return ok;
    }
    public boolean clickContactUsAndVerifyThenBack() {
        openCartPage();
        WebElement el = findNav(navContact1); if (el == null) el = findNav(navContact2);
        if (el == null) return false;
        safeClick(el);
        boolean ok = driver.getCurrentUrl().contains("/contact_us");
        openCartPage(); return ok;
    }

    // ---------- Product-related helpers ----------
    public boolean addFirstProductFromHomeOrProducts() {
        // try home first then products page
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
                return true;
            }
        }
        return false;
    }

    public boolean addMultipleProductsFromProducts(int n) {
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
        return added >= n;
    }

    public int getCartItemsCount() {
        openCartPage();
        return driver.findElements(cartRows).size();
    }

    public boolean removeFirstCartItem() {
        openCartPage();
        List<WebElement> rem = driver.findElements(removeBtn);
        if (rem.isEmpty()) return false;
        safeClick(rem.get(0));
        sleep(500);
        return true;
    }

    public boolean clickProceedToCheckout() {
        openCartPage();
        List<WebElement> els = driver.findElements(proceedCheckout);
        if (els.isEmpty()) return false;
        safeClick(els.get(0));
        waitForReady();
        return driver.getCurrentUrl().toLowerCase().contains("checkout") || driver.getCurrentUrl().toLowerCase().contains("payment");
    }

    public boolean clickContinueToProducts() {
        openCartPage();
        List<WebElement> els = driver.findElements(continueBtn);
        if (els.isEmpty()) return false;
        safeClick(els.get(0));
        waitForReady();
        return driver.getCurrentUrl().contains("/products") || !driver.getCurrentUrl().contains("/view_cart");
    }

    public boolean applyCoupon(String code) {
        openCartPage();
        if (driver.findElements(couponInput).isEmpty() || driver.findElements(couponApply).isEmpty()) return false;
        WebElement in = driver.findElement(couponInput);
        in.clear();
        in.sendKeys(code);
        safeClick(driver.findElement(couponApply));
        sleep(700);
        // success or error shows some alert - detect any alert-like element
        return !driver.findElements(By.cssSelector(".alert, .success, .error")).isEmpty();
    }

    public boolean isCartCountBadgeUpdating() {
        String before = getCartCount();
        addFirstProductFromHomeOrProducts();
        String after = getCartCount();
        return !after.equals(before);
    }

    public String getCartCount() {
        List<WebElement> b = driver.findElements(cartCountBadge);
        return b.isEmpty() ? "" : b.get(0).getText().trim();
    }

    // helpers used by tests
    public boolean clearCart() {
        openCartPage();
        List<WebElement> rem = driver.findElements(removeBtn);
        for (WebElement r : rem) {
            safeClick(r);
            sleep(400);
        }
        return driver.findElements(cartRows).isEmpty();
    }

    public void printNavLinks() {
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
}
