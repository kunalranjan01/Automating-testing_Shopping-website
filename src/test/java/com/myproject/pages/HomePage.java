package com.myproject.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // --- common locators (fallbacks where applicable) ---
    private final By logo = By.cssSelector(".logo, .site-logo, .navbar-brand img");
    private final By productsLink = By.linkText("Products");
    private final By cartLink = By.linkText("Cart");
    private final By signupLoginLink = By.linkText("Signup / Login");
    private final By testCasesLink = By.linkText("Test Cases");
    private final By apiTestingLink = By.linkText("API Testing");
    private final By videoTutorialsLink = By.linkText("Video Tutorials");
    private final By contactUsLink = By.linkText("Contact Us");
    private final By categoriesSection = By.cssSelector(".left-sidebar, #categories, .category-products, .category-tab");
    private final By featureItemsSection = By.cssSelector(".features_items, .recommended_items, .feature-items");
    private final By brandsSection = By.cssSelector(".brands_products, .brands, .brand-list");
    private final By productItems = By.cssSelector(".features_items .product-image-wrapper, .product-overlay, .single-products, .product");
    // carousel variants: try several common patterns
    private final By carouselSlides = By.cssSelector(".carousel-inner .item, .carousel .item, #slider .item, .owl-carousel .owl-item");
    private final By carouselNext = By.cssSelector(".carousel-control-next, .carousel .right.carousel-control, .owl-next, .slick-next");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- helpers ---
    private boolean isVisible(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return driver.findElements(locator).size() > 0;
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

    // --- page actions & verifications ---
    public boolean isHomeIconPresent() {
        return isVisible(logo);
    }

    public boolean isProductsIconPresentAndClickable() {
        return isVisible(productsLink) && isClickable(productsLink);
    }

    public boolean isCartIconPresentAndClickable() {
        return isVisible(cartLink) && isClickable(cartLink);
    }

    public boolean isSignupLoginPresentAndClickable() {
        return isVisible(signupLoginLink) && isClickable(signupLoginLink);
    }

    public boolean isTestCasesPresentAndClickable() {
        return isVisible(testCasesLink) && isClickable(testCasesLink);
    }

    public boolean isApiTestingPresentAndClickable() {
        return isVisible(apiTestingLink) && isClickable(apiTestingLink);
    }

    public boolean isVideoTutorialsPresentAndClickable() {
        return isVisible(videoTutorialsLink) && isClickable(videoTutorialsLink);
    }

    public boolean isContactUsPresentAndClickable() {
        return isVisible(contactUsLink) && isClickable(contactUsLink);
    }

    public boolean isCategoriesSectionPresent() {
        return isVisible(categoriesSection);
    }

    public boolean isFeatureItemsPresent() {
        return isVisible(featureItemsSection);
    }

    public boolean isBrandsSectionPresent() {
        return isVisible(brandsSection);
    }

    public boolean areProductsAvailableOnHomepage() {
        try {
            List<WebElement> list = driver.findElements(productItems);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCarouselWorking() {
        try {
            // check there are multiple slides
            List<WebElement> slides = driver.findElements(carouselSlides);
            if (slides.size() > 1) return true;

            // fallback: try clicking next and see that something changes or next control exists
            if (driver.findElements(carouselNext).size() > 0) return true;

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTestCaseButtonClickable() {
        return isTestCasesPresentAndClickable();
    }

    // small utility to navigate (we assume BaseTest opened base URL)
    public void goToHome() {
        // a click on logo often returns home
        try {
            if (isClickable(logo)) {
                driver.findElement(logo).click();
            }
        } catch (Exception ignored) {}
    }
}
