package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProductPageFunctional {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;

    // --- Locators ---
    private final By productsNav = By.cssSelector("a[href='/products']");
    private final By searchBox = By.id("search_product");
    private final By searchBtn = By.id("submit_search");
    private final By productCard = By.cssSelector(".features_items .product-image-wrapper");

    // Add-to-cart overlay + modal
    private final By addToCartOverlay = By.cssSelector(".product-overlay .add-to-cart");
    private final By cartModal = By.id("cartModal");
    private final By continueShoppingBtn = By.cssSelector("#cartModal button[data-dismiss='modal'], #cartModal .btn.btn-success.close-modal");
    private final By viewCartBtn = By.cssSelector("#cartModal a[href='/view_cart']");

    // Categories
    private final By womenCategory = By.cssSelector(".left-sidebar .category-products a[href='#Women']");
    private final By menCategory   = By.cssSelector(".left-sidebar .category-products a[href='#Men']");
    private final By kidsCategory  = By.cssSelector(".left-sidebar .category-products a[href='#Kids']");


    // Brands
    private final By poloBrand   = By.cssSelector(".brands_products ul li a[href*='Polo']");
    private final By hmBrand     = By.cssSelector(".brands_products ul li a[href*='H&M']");
    private final By madameBrand = By.cssSelector(".brands_products ul li a[href*='Madame']");


    // Subscription
    private final By subscriptionInput = By.id("susbscribe_email");
    private final By subscriptionBtn = By.id("subscribe");
    private final By subscriptionSuccess = By.cssSelector(".alert-success");

    // Footer + scroll
    private final By footer = By.cssSelector("footer");
    private final By scrollUp = By.id("scrollUp");

    public ProductPageFunctional(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.actions = new Actions(driver);
    }

    // Logging helper
    private void logStep(String message) {
        TestListener.info(message);
    }

    // --- Actions ---
    public void navigateToProductsPage() {
        logStep("Navigating to products page");
        driver.findElement(productsNav).click();
        wait.until(ExpectedConditions.urlContains("/products"));
    }

    public void searchProduct(String query) {
        logStep("Searching for product: " + query);
        WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
        box.clear();
        box.sendKeys(query);
        driver.findElement(searchBtn).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(productCard));
    }

    public boolean isSearchResultVisible() {
        boolean result = !driver.findElements(productCard).isEmpty();
        logStep("Search results visible -> " + result);
        return result;
    }

    public boolean addFirstProductToCart() {
        try {
            List<WebElement> cards = driver.findElements(productCard);
            if (cards.isEmpty()) {
                logStep("No product cards found");
                return false;
            }
            WebElement first = cards.get(0);
            actions.moveToElement(first).perform();
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addToCartOverlay));
            btn.click();
            logStep("Clicked Add to Cart");
            wait.until(ExpectedConditions.visibilityOfElementLocated(cartModal));
            return true;
        } catch (Exception e) {
            logStep("Add to cart failed: " + e.getMessage());
            return false;
        }
    }

    public boolean clickContinueShopping() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(continueShoppingBtn));
            btn.click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(cartModal));
            logStep("Clicked Continue Shopping");
            return true;
        } catch (Exception e) {
            logStep("Continue Shopping failed: " + e.getMessage());
            return false;
        }
    }

    public boolean clickViewCart() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(viewCartBtn));
            btn.click();
            wait.until(ExpectedConditions.urlContains("/view_cart"));
            logStep("Clicked View Cart");
            return true;
        } catch (Exception e) {
            logStep("View Cart failed: " + e.getMessage());
            return false;
        }
    }

    // --- Category filters ---
    public boolean clickWomenCategory() {
        return clickCategory(womenCategory, "Women");
    }

    public boolean clickMenCategory() {
        return clickCategory(menCategory, "Men");
    }

    public boolean clickKidsCategory() {
        return clickCategory(kidsCategory, "Kids");
    }

    private boolean clickCategory(By locator, String name) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
            logStep("Clicked " + name + " category");
            wait.until(ExpectedConditions.visibilityOfElementLocated(productCard));
            return true;
        } catch (Exception e) {
            logStep(name + " category click failed: " + e.getMessage());
            return false;
        }
    }

    // --- Brand filters ---
    public boolean clickPoloBrand() { return clickBrand(poloBrand, "Polo"); }
    public boolean clickHMBrand() { return clickBrand(hmBrand, "H&M"); }
    public boolean clickMadameBrand() { return clickBrand(madameBrand, "Madame"); }

    private boolean clickBrand(By locator, String name) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
            logStep("Clicked " + name + " brand");
            wait.until(ExpectedConditions.visibilityOfElementLocated(productCard));
            return true;
        } catch (Exception e) {
            logStep(name + " brand click failed: " + e.getMessage());
            return false;
        }
    }

    // --- Subscription ---
    public boolean subscribeWithEmail(String email) {
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(subscriptionInput));
            input.clear();
            input.sendKeys(email);
            driver.findElement(subscriptionBtn).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(subscriptionSuccess));
            logStep("Subscription success with " + email);
            return true;
        } catch (Exception e) {
            logStep("Subscription failed: " + e.getMessage());
            return false;
        }
    }

    // --- Scroll + footer ---
    public boolean scrollToBottomAndCheckFooter() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        boolean result = driver.findElement(footer).isDisplayed();
        logStep("Footer visible after scroll -> " + result);
        return result;
    }

    public boolean scrollUpToTop() {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(scrollUp));
            btn.click();
            wait.until(d -> ((Long)((JavascriptExecutor) d).executeScript("return window.pageYOffset;")) == 0L);
            logStep("Scroll-up successful");
            return true;
        } catch (Exception e) {
            logStep("Scroll-up failed: " + e.getMessage());
            return false;
        }
    }
}
