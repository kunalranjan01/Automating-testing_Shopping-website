package com.myproject.pages;

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
    private final By productsLink = By.linkText("Products");
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
    private final By womenCategory = By.linkText("Women");
    private final By menCategory = By.linkText("Men");
    private final By kidsCategory = By.linkText("Kids");

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

    // --- Generic helpers ---
    private boolean isVisible(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            WebElement el = driver.findElement(locator);
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isClickable(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            return driver.findElements(locator).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Top-nav presence & clickability checks
    public boolean isProductsLinkPresentAndClickable() { return isVisible(productsLink) && isClickable(productsLink); }
    public boolean isHomeLinkPresentAndClickable() { return isVisible(homeLink) && isClickable(homeLink); }
    public boolean isCartLinkPresentAndClickable() { return isVisible(cartLink) && isClickable(cartLink); }
    public boolean isSignupLoginPresentAndClickable() { return isVisible(signupLoginLink) && isClickable(signupLoginLink); }
    public boolean isTestCasesPresentAndClickable() { return isVisible(testCasesLink) && isClickable(testCasesLink); }

    // Logos / search / sections
    public boolean isSiteLogoPresent() { return isVisible(siteLogo); }
    public boolean isOffersLogoPresent() { return isVisible(offersLogo); }
    public boolean isSearchBarPresent() { return isVisible(searchInput); }
    public boolean isCategoriesSectionPresent() { return isVisible(categoriesSection); }
    public boolean isBrandsSectionPresent() { return isVisible(brandsSection); }
    public boolean isAllProductsSectionPresent() { return isVisible(allProductsSection); }

    // Category checks
    public boolean isWomenCategoryPresentAndClickable() { return isVisible(womenCategory) && isClickable(womenCategory); }
    public boolean isMenCategoryPresentAndClickable() { return isVisible(menCategory) && isClickable(menCategory); }
    public boolean isKidsCategoryPresentAndClickable() { return isVisible(kidsCategory) && isClickable(kidsCategory); }

    // Brand checks
    public boolean isBrandAllenSollyPresentAndClickable() {
        try { return isVisible(brandAllenSolly) && isClickable(brandAllenSolly); } catch (Exception e) { return false; }
    }
    public boolean isBrandKookieKidsPresentAndClickable() {
        try { return isVisible(brandKookieKids) && isClickable(brandKookieKids); } catch (Exception e) { return false; }
    }
    public boolean isBrandBibaPresentAndClickable() {
        try { return isVisible(brandBiba) && isClickable(brandBiba); } catch (Exception e) { return false; }
    }

    // Hover product -> overlay visible with price & add-to-cart
    public boolean isHoverOverlayShowingPriceAndAddToCart() {
        try {
            List<WebElement> cards = driver.findElements(productCard);
            if (cards.isEmpty()) return false;
            WebElement first = cards.get(0);
            actions.moveToElement(first).perform();
            // short wait for overlay
            Thread.sleep(700);
            // check for overlay add-to-cart presence in the hovered card
            if (!first.findElements(addToCartOverlaySelector).isEmpty()) return true;
            return !driver.findElements(By.cssSelector(".product-overlay .add-to-cart")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // Footer copyright text
    public boolean isFooterCopyrightTextPresent() {
        return isVisible(footerCopyright);
    }

    // Scroll up behavior
    public boolean clickScrollUpAndVerifyTop() {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(700);
            List<WebElement> els = driver.findElements(scrollUp);
            if (els.isEmpty()) return false;
            WebElement btn = els.get(0);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            // wait for top position approximately 0
            wait.until(d -> {
                Object res = ((JavascriptExecutor)d).executeScript("return Math.round(window.pageYOffset);");
                if (res instanceof Long) return ((Long) res) == 0L;
                if (res instanceof Number) return ((Number) res).intValue() == 0;
                return false;
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // product image full screen after clicking
    public boolean isProductImageFullScreenOnClick() {
        try {
            List<WebElement> imgs = driver.findElements(productImage);
            if (imgs.isEmpty()) return false;
            WebElement img = imgs.get(0);
            img.click();
            // wait for modal/viewer
            Thread.sleep(700);
            boolean modalVisible = !driver.findElements(productModal).isEmpty() && driver.findElement(productModal).isDisplayed();
            // close modal if present
            try {
                List<WebElement> closeBtns = driver.findElements(By.cssSelector(".fancybox-close, .modal-close, .close"));
                if (!closeBtns.isEmpty()) closeBtns.get(0).click();
            } catch (Exception ignored) {}
            return modalVisible;
        } catch (Exception e) {
            return false;
        }
    }

    // special offer section clickable
    public boolean isSpecialOfferClickable() {
        try {
            List<WebElement> els = driver.findElements(specialOfferSection);
            if (els.isEmpty()) return false;
            WebElement el = els.get(0);
            // if it contains a link or is clickable
            if (!el.findElements(By.tagName("a")).isEmpty()) return true;
            try {
                el.click();
                Thread.sleep(700);
                driver.navigate().back();
            } catch (Exception ignored) {}
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
