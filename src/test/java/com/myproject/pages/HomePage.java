package com.myproject.pages;

import com.myproject.listeners.TestListener;
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

    // --- locators ---
    private final By logo = By.cssSelector(".logo, .site-logo, .navbar-brand img");
    private final By productsLink = By.cssSelector("a[href='/products']");
    private final By contactUsLink = By.cssSelector("a[href='/contact_us']");
    private final By cartLink = By.linkText("Cart");
    private final By signupLoginLink = By.linkText("Signup / Login");
    private final By testCasesLink = By.linkText("Test Cases");
    private final By apiTestingLink = By.linkText("API Testing");
    private final By videoTutorialsLink = By.linkText("Video Tutorials");
    private final By categoriesSection = By.cssSelector(".left-sidebar, #categories, .category-products, .category-tab");
    private final By featureItemsSection = By.cssSelector(".features_items, .recommended_items, .feature-items");
    private final By brandsSection = By.cssSelector(".brands_products, .brands, .brand-list");
    private final By productItems = By.cssSelector(".features_items .product-image-wrapper, .product-overlay, .single-products, .product");
    private final By carouselSlides = By.cssSelector(".carousel-inner .item, .carousel .item, #slider .item, .owl-carousel .owl-item");
    private final By carouselNext = By.cssSelector(".carousel-control-next, .carousel .right.carousel-control, .owl-next, .slick-next");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // expose driver for tests/listener to use if needed
    public WebDriver getDriver() {
        return this.driver;
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

    private void logStep(String message) {
        com.myproject.listeners.TestListener.info(message);
    }


    // --- page verifications with logging ---
    public boolean isHomeIconPresent() {
        boolean result = isVisible(logo);
        logStep("Checked Home icon/logo presence → " + result);
        return result;
    }

    public boolean isProductsIconPresentAndClickable() {
        boolean result = isVisible(productsLink) && isClickable(productsLink);
        logStep("Checked Products icon presence & clickable → " + result);
        return result;
    }

    public boolean isCartIconPresentAndClickable() {
        boolean result = isVisible(cartLink) && isClickable(cartLink);
        logStep("Checked Cart icon presence & clickable → " + result);
        return result;
    }

    public boolean isSignupLoginPresentAndClickable() {
        boolean result = isVisible(signupLoginLink) && isClickable(signupLoginLink);
        logStep("Checked Signup/Login presence & clickable → " + result);
        return result;
    }

    public boolean isTestCasesPresentAndClickable() {
        boolean result = isVisible(testCasesLink) && isClickable(testCasesLink);
        logStep("Checked Test Cases presence & clickable → " + result);
        return result;
    }

    public boolean isApiTestingPresentAndClickable() {
        boolean result = isVisible(apiTestingLink) && isClickable(apiTestingLink);
        logStep("Checked API Testing presence & clickable → " + result);
        return result;
    }

    public boolean isVideoTutorialsPresentAndClickable() {
        boolean result = isVisible(videoTutorialsLink) && isClickable(videoTutorialsLink);
        logStep("Checked Video Tutorials presence & clickable → " + result);
        return result;
    }

    public boolean isContactUsPresentAndClickable() {
        boolean result = isVisible(contactUsLink) && isClickable(contactUsLink);
        logStep("Checked Contact Us presence & clickable → " + result);
        return result;
    }

    public boolean isCategoriesSectionPresent() {
        boolean result = isVisible(categoriesSection);
        logStep("Checked Categories section presence → " + result);
        return result;
    }

    public boolean isFeatureItemsPresent() {
        boolean result = isVisible(featureItemsSection);
        logStep("Checked Feature Items section presence → " + result);
        return result;
    }

    public boolean isBrandsSectionPresent() {
        boolean result = isVisible(brandsSection);
        logStep("Checked Brands section presence → " + result);
        return result;
    }

    public boolean areProductsAvailableOnHomepage() {
        try {
            List<WebElement> list = driver.findElements(productItems);
            boolean result = list.size() > 0;
            logStep("Checked products availability on homepage → count=" + list.size());
            return result;
        } catch (Exception e) {
            logStep("Error while checking products: " + e.getMessage());
            return false;
        }
    }

    public boolean isCarouselWorking() {
        try {
            List<WebElement> slides = driver.findElements(carouselSlides);
            boolean result;
            if (slides.size() > 1) {
                result = true;
            } else {
                result = driver.findElements(carouselNext).size() > 0;
            }
            logStep("Checked carousel working → " + result);
            return result;
        } catch (Exception e) {
            logStep("Error while checking carousel: " + e.getMessage());
            return false;
        }
    }

    public boolean isTestCaseButtonClickable() {
        boolean result = isTestCasesPresentAndClickable();
        logStep("Checked Test Case button clickable → " + result);
        return result;
    }

    // --- action methods (reusable, log steps inside) ---
    // Note: these will throw if action cannot be performed (so TestListener captures screenshot on failure)

    public void goToHome() {
        logStep("Action: goToHome() - attempt to click Home logo");
        if (!isClickable(logo)) {
            logStep("Home logo not clickable or not present");
            throw new IllegalStateException("Home logo not clickable or not present");
        }
        try {
            driver.findElement(logo).click();
            logStep("Clicked Home logo to navigate to homepage");
        } catch (Exception e) {
            logStep("Failed to click Home logo: " + e.getMessage());
            throw e;
        }
    }

    public void clickProducts() {
        logStep("Action: clickProducts() - attempt to click Products link");
        if (!isClickable(productsLink)) {
            logStep("Products link not clickable or not present");
            throw new IllegalStateException("Products link not clickable or not present");
        }
        try {
            driver.findElement(productsLink).click();
            logStep("Clicked Products link");
        } catch (Exception e) {
            logStep("Exception while clicking Products link: " + e.getMessage());
            throw e;
        }
    }

    public void clickCart() {
        logStep("Action: clickCart() - attempt to click Cart link");
        if (!isClickable(cartLink)) {
            logStep("Cart link not clickable or not present");
            throw new IllegalStateException("Cart link not clickable or not present");
        }
        try {
            driver.findElement(cartLink).click();
            logStep("Clicked Cart link");
        } catch (Exception e) {
            logStep("Exception while clicking Cart link: " + e.getMessage());
            throw e;
        }
    }

    public void clickSignupLogin() {
        logStep("Action: clickSignupLogin() - attempt to click Signup / Login link");
        if (!isClickable(signupLoginLink)) {
            logStep("Signup / Login link not clickable or not present");
            throw new IllegalStateException("Signup / Login link not clickable or not present");
        }
        try {
            driver.findElement(signupLoginLink).click();
            logStep("Clicked Signup / Login link");
        } catch (Exception e) {
            logStep("Exception while clicking Signup / Login link: " + e.getMessage());
            throw e;
        }
    }

    public void clickTestCases() {
        logStep("Action: clickTestCases() - attempt to click Test Cases link");
        if (!isClickable(testCasesLink)) {
            logStep("Test Cases link not clickable or not present");
            throw new IllegalStateException("Test Cases link not clickable or not present");
        }
        try {
            driver.findElement(testCasesLink).click();
            logStep("Clicked Test Cases link");
        } catch (Exception e) {
            logStep("Exception while clicking Test Cases link: " + e.getMessage());
            throw e;
        }
    }

    public void clickApiTesting() {
        logStep("Action: clickApiTesting() - attempt to click API Testing link");
        if (!isClickable(apiTestingLink)) {
            logStep("API Testing link not clickable or not present");
            throw new IllegalStateException("API Testing link not clickable or not present");
        }
        try {
            driver.findElement(apiTestingLink).click();
            logStep("Clicked API Testing link");
        } catch (Exception e) {
            logStep("Exception while clicking API Testing link: " + e.getMessage());
            throw e;
        }
    }

    public void clickVideoTutorials() {
        logStep("Action: clickVideoTutorials() - attempt to click Video Tutorials link");
        if (!isClickable(videoTutorialsLink)) {
            logStep("Video Tutorials link not clickable or not present");
            throw new IllegalStateException("Video Tutorials link not clickable or not present");
        }
        try {
            driver.findElement(videoTutorialsLink).click();
            logStep("Clicked Video Tutorials link");
        } catch (Exception e) {
            logStep("Exception while clicking Video Tutorials link: " + e.getMessage());
            throw e;
        }
    }

    public void clickContactUs() {
        logStep("Action: clickContactUs() - attempt to click Contact Us link");
        if (!isClickable(contactUsLink)) {
            logStep("Contact Us link not clickable or not present");
            throw new IllegalStateException("Contact Us link not clickable or not present");
        }
        try {
            driver.findElement(contactUsLink).click();
            logStep("Clicked Contact Us link");
        } catch (Exception e) {
            logStep("Exception while clicking Contact Us link: " + e.getMessage());
            throw e;
        }
    }

    // convenience: safeClick for any locator (throws on failure)
    public void safeClick(By locator, String actionName) {
        logStep("Action: " + actionName + " - attempt safeClick");
        if (!isClickable(locator)) {
            logStep(actionName + " - element not clickable/present");
            throw new IllegalStateException(actionName + " - element not clickable/present");
        }
        try {
            driver.findElement(locator).click();
            logStep(actionName + " - click successful");
        } catch (Exception e) {
            logStep(actionName + " - exception: " + e.getMessage());
            throw e;
        }
    }
}
