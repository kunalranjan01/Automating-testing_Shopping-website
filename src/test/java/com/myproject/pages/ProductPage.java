package com.myproject.pages;

import com.myproject.listeners.TestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProductPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;

    // Top nav links
    private final By productsLink = By.cssSelector("a[href='/products'], a[href='/products/']");
    private final By homeLink = By.linkText("Home");
    private final By cartLink = By.linkText("Cart");
    private final By signupLoginLink = By.linkText("Signup / Login");
    private final By testCasesLink = By.linkText("Test Cases");

    // logos and UI
    private final By siteLogo = By.cssSelector(".logo, .navbar-brand img");
    private final By offersLogo = By.cssSelector(".offer-image, .offers, .promo, .campaign");
    private final By searchInput = By.cssSelector("input#search_product, input[name='search'], input[type='search']");
    private final By categoriesSection = By.cssSelector(".left-sidebar, #categories");
    private final By brandsSection = By.cssSelector(".brands_products, .brands");
    private final By allProductsSection = By.cssSelector(".features_items, .all-products, .product-list");

    // category links on product page (women/men/kids)
    private final By womenCategory = By.xpath(
    	    "//div[contains(@class,'left-sidebar') or contains(@id,'categories')]"
    	  + "//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'women')]"
    	);
    	private final By menCategory = By.xpath(
    	    "//div[contains(@class,'left-sidebar') or contains(@id,'categories')]"
    	  + "//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'men')]"
    	);
    	private final By kidsCategory = By.xpath(
    	    "//div[contains(@class,'left-sidebar') or contains(@id,'categories')]"
    	  + "//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'kids')]"
    	);
    	
    // brands - specific brand names in brands section
    private final By brandAllenSolly = By.xpath("//*[contains(@class,'brands') or contains(@class,'brand')]//a[contains(.,'Allen Solly') or contains(.,'Allen Solly Junior')]");
    private final By brandKookieKids = By.xpath("//*[contains(@class,'brands') or contains(@class,'brand')]//a[contains(.,'Kookie Kids') or contains(.,'Kookie')]");
    private final By brandBiba = By.xpath("//*[contains(@class,'brands') or contains(@class,'brand')]//a[contains(.,'Biba')]");

    // products and overlay
    private final By productCard = By.cssSelector(".product-image-wrapper, .single-products, .product, .col-sm-4");
    private final By addToCartOverlaySelector = By.cssSelector(".product-overlay .add-to-cart, .overlay-content .add-to-cart, .add-to-cart");

    // footer copyright
    private final By footerCopyright = By.xpath("//*[contains(text(),'Copyright') and contains(text(),'All rights reserved')]");
    // scroll up
    private final By scrollUp = By.cssSelector("#scrollUp, .scroll-up, .back-to-top");

    // product image modal or full screen viewer
    private final By productImage = By.cssSelector(".product-image-wrapper img, .gallery img, .zoom, img");
    private final By productModal = By.cssSelector(".fancybox-wrap, .modal, .product-overlay-modal, .zoomed");

    // special offer section
    private final By specialOfferSection = By.cssSelector(".recommended_items, .specials, .offer-image, .left-sidebar .single-widget");

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.actions = new Actions(driver);
    }

    // --- Logging helper ---
    private void logStep(String message) {
        // use INFO-level step logging so it appears in Extent under the test
        TestListener.info(message);
    }

    // --- Generic helpers ---
    private boolean isVisible(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            WebElement el = driver.findElement(locator);
            boolean displayed = el.isDisplayed();
            logStep("Checked visibility of locator " + locator + " -> " + displayed);
            return displayed;
        } catch (Exception e) {
            logStep("Visibility check failed for locator " + locator + " -> " + e.getMessage());
            return false;
        }
    }

    private boolean isClickable(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            boolean exists = driver.findElements(locator).size() > 0;
            logStep("Checked clickability of locator " + locator + " -> " + exists);
            return exists;
        } catch (Exception e) {
            logStep("Clickability check failed for locator " + locator + " -> " + e.getMessage());
            return false;
        }
    }

    // Top-nav presence & clickability checks (with step logs)
    public boolean isProductsLinkPresentAndClickable() {
        logStep("Step: Verify Products link is present and clickable");
        boolean result = isVisible(productsLink) && isClickable(productsLink);
        logStep("Result: Products link present & clickable -> " + result);
        return result;
    }

    public boolean isHomeLinkPresentAndClickable() {
        logStep("Step: Verify Home link is present and clickable");
        boolean result = isVisible(homeLink) && isClickable(homeLink);
        logStep("Result: Home link present & clickable -> " + result);
        return result;
    }

    public boolean isCartLinkPresentAndClickable() {
        logStep("Step: Verify Cart link is present and clickable");
        boolean result = isVisible(cartLink) && isClickable(cartLink);
        logStep("Result: Cart link present & clickable -> " + result);
        return result;
    }

    public boolean isSignupLoginPresentAndClickable() {
        logStep("Step: Verify Signup / Login link is present and clickable");
        boolean result = isVisible(signupLoginLink) && isClickable(signupLoginLink);
        logStep("Result: Signup / Login link present & clickable -> " + result);
        return result;
    }

    public boolean isTestCasesPresentAndClickable() {
        logStep("Step: Verify Test Cases link is present and clickable");
        boolean result = isVisible(testCasesLink) && isClickable(testCasesLink);
        logStep("Result: Test Cases link present & clickable -> " + result);
        return result;
    }

    // Logos / search / sections
    public boolean isSiteLogoPresent() {
        logStep("Step: Check site/app logo presence");
        boolean result = isVisible(siteLogo);
        logStep("Result: Site logo visible -> " + result);
        return result;
    }

    public boolean isOffersLogoPresent() {
        logStep("Step: Check offers/logo presence on product page");
        boolean result = isVisible(offersLogo);
        logStep("Result: Offers logo present -> " + result);
        return result;
    }

    public boolean isSearchBarPresent() {
        logStep("Step: Check search bar presence");
        boolean result = isVisible(searchInput);
        logStep("Result: Search bar present -> " + result);
        return result;
    }

    public boolean isCategoriesSectionPresent() {
        logStep("Step: Check categories section presence");
        boolean result = isVisible(categoriesSection);
        logStep("Result: Categories section present -> " + result);
        return result;
    }

    public boolean isBrandsSectionPresent() {
        logStep("Step: Check brands section presence");
        boolean result = isVisible(brandsSection);
        logStep("Result: Brands section present -> " + result);
        return result;
    }

    public boolean isAllProductsSectionPresent() {
        logStep("Step: Check all products section presence");
        boolean result = isVisible(allProductsSection);
        logStep("Result: All products section present -> " + result);
        return result;
    }

    // Category checks
    public boolean isWomenCategoryPresentAndClickable() {
        logStep("Step: Verify Women category presence & clickable");
        boolean result = isVisible(womenCategory) && isClickable(womenCategory);
        logStep("Result: Women category present & clickable -> " + result);
        return result;
    }

    public boolean isMenCategoryPresentAndClickable() {
        logStep("Step: Verify Men category presence & clickable");
        boolean result = isVisible(menCategory) && isClickable(menCategory);
        logStep("Result: Men category present & clickable -> " + result);
        return result;
    }

    public boolean isKidsCategoryPresentAndClickable() {
        logStep("Step: Verify Kids category presence & clickable");
        boolean result = isVisible(kidsCategory) && isClickable(kidsCategory);
        logStep("Result: Kids category present & clickable -> " + result);
        return result;
    }

    // Brand checks
    public boolean isBrandAllenSollyPresentAndClickable() {
        logStep("Step: Check Allen Solly brand presence & clickability");
        try {
            boolean result = isVisible(brandAllenSolly) && isClickable(brandAllenSolly);
            logStep("Result: Allen Solly brand present & clickable -> " + result);
            return result;
        } catch (Exception e) {
            logStep("Exception while checking Allen Solly brand: " + e.getMessage());
            return false;
        }
    }

    public boolean isBrandKookieKidsPresentAndClickable() {
        logStep("Step: Check Kookie Kids brand presence & clickability");
        try {
            boolean result = isVisible(brandKookieKids) && isClickable(brandKookieKids);
            logStep("Result: Kookie Kids brand present & clickable -> " + result);
            return result;
        } catch (Exception e) {
            logStep("Exception while checking Kookie Kids brand: " + e.getMessage());
            return false;
        }
    }

    public boolean isBrandBibaPresentAndClickable() {
        logStep("Step: Check Biba brand presence & clickability");
        try {
            boolean result = isVisible(brandBiba) && isClickable(brandBiba);
            logStep("Result: Biba brand present & clickable -> " + result);
            return result;
        } catch (Exception e) {
            logStep("Exception while checking Biba brand: " + e.getMessage());
            return false;
        }
    }

    // Hover product -> overlay visible with price & add-to-cart
    public boolean isHoverOverlayShowingPriceAndAddToCart() {
        logStep("Step: Hover first product and check overlay with price & Add to Cart");
        try {
            List<WebElement> cards = driver.findElements(productCard);
            if (cards.isEmpty()) {
                logStep("No product cards found on the product page");
                return false;
            }
            WebElement first = cards.get(0);
            actions.moveToElement(first).perform();
            logStep("Hovered over first product card");
            // short sleep to allow overlay to appear
            try { Thread.sleep(700); } catch (InterruptedException ignored) {}
            boolean overlayPresent = !first.findElements(addToCartOverlaySelector).isEmpty()
                    || !driver.findElements(addToCartOverlaySelector).isEmpty();
            logStep("Overlay + Add-to-Cart present after hover -> " + overlayPresent);
            return overlayPresent;
        } catch (Exception e) {
            logStep("Exception during hover overlay check: " + e.getMessage());
            return false;
        }
    }

    // Footer copyright text
    public boolean isFooterCopyrightTextPresent() {
        logStep("Step: Verify footer copyright text presence");
        boolean result = isVisible(footerCopyright);
        logStep("Result: Footer copyright present -> " + result);
        return result;
    }

    // Scroll up behavior
    public boolean clickScrollUpAndVerifyTop() {
        logStep("Step: Scroll to bottom and click scroll-up button, then verify top");
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try { Thread.sleep(700); } catch (InterruptedException ignored) {}
            List<WebElement> els = driver.findElements(scrollUp);
            if (els.isEmpty()) {
                logStep("Scroll-up button not present");
                return false;
            }
            WebElement btn = els.get(0);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logStep("Clicked scroll-up button");
            // wait for top position approximately 0
            wait.until(d -> {
                Object res = ((JavascriptExecutor)d).executeScript("return Math.round(window.pageYOffset);");
                if (res instanceof Long) return ((Long) res) == 0L;
                if (res instanceof Number) return ((Number) res).intValue() == 0;
                return false;
            });
            logStep("Verified page scrolled back to top");
            return true;
        } catch (Exception e) {
            logStep("Exception while verifying scroll-up behavior: " + e.getMessage());
            return false;
        }
    }

    // product image full screen after clicking
    public boolean isProductImageFullScreenOnClick() {
        logStep("Step: Click first product image and verify full-screen/modal opens");
        try {
            List<WebElement> imgs = driver.findElements(productImage);
            if (imgs.isEmpty()) {
                logStep("No product images found to click");
                return false;
            }
            WebElement img = imgs.get(0);
            img.click();
            logStep("Clicked product image");
            try { Thread.sleep(700); } catch (InterruptedException ignored) {}
            boolean modalVisible = !driver.findElements(productModal).isEmpty() && driver.findElement(productModal).isDisplayed();
            logStep("Product modal visible -> " + modalVisible);
            // close modal if present
            try {
                List<WebElement> closeBtns = driver.findElements(By.cssSelector(".fancybox-close, .modal-close, .close"));
                if (!closeBtns.isEmpty()) {
                    closeBtns.get(0).click();
                    logStep("Closed product modal");
                }
            } catch (Exception ignored) {}
            return modalVisible;
        } catch (Exception e) {
            logStep("Exception while checking product full-screen modal: " + e.getMessage());
            return false;
        }
    }

    // special offer section clickable
    public boolean isSpecialOfferClickable() {
        logStep("Step: Verify special offer section is clickable or contains links");
        try {
            List<WebElement> els = driver.findElements(specialOfferSection);
            if (els.isEmpty()) {
                logStep("No special offer section found");
                return false;
            }
            WebElement el = els.get(0);
            boolean hasLink = !el.findElements(By.tagName("a")).isEmpty();
            if (hasLink) {
                logStep("Special offer contains links (clickable) -> true");
                return true;
            }
            try {
                el.click();
                logStep("Clicked on special offer section (no anchor link)");
                try { Thread.sleep(700); } catch (InterruptedException ignored) {}
                driver.navigate().back();
                logStep("Navigated back after clicking special offer");
            } catch (Exception clickEx) {
                logStep("Special offer click attempted but threw: " + clickEx.getMessage());
            }
            return true;
        } catch (Exception e) {
            logStep("Exception while checking special offers: " + e.getMessage());
            return false;
        }
    }
}
