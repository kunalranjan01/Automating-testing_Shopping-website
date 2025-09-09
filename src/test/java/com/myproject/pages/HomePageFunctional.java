package com.myproject.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePageFunctional {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Carousel locators
    private final By carouselNext = By.cssSelector(".carousel-control-next, .carousel .right.carousel-control");
    private final By carouselPrev = By.cssSelector(".carousel-control-prev, .carousel .left.carousel-control");

    // Subscription field
    private final By subscriptionInput = By.id("susbscribe_email");
    private final By subscriptionButton = By.id("subscribe"); // adjust if site has different button
    private final By subscriptionSuccess = By.cssSelector("#success-subscribe, .alert-success, .alert.alert-success");
    private final By subscriptionError = By.cssSelector("#error-subscribe, .alert-danger, .alert.alert-danger");

    // Automation Exercise logo / button
    private final By automationExerciseBtn = By.cssSelector(".logo img, .navbar-brand img");

    // Add to Cart button (first product on homepage)
    private final By firstAddToCartBtn = By.cssSelector(".features_items .product-overlay .add-to-cart, .add-to-cart, .btn.btn-default.add-to-cart");

    public HomePageFunctional(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- Carousel actions ---
    public boolean clickCarouselNext() {
        try {
            WebElement next = wait.until(ExpectedConditions.elementToBeClickable(carouselNext));
            next.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickCarouselPrev() {
        try {
            WebElement prev = wait.until(ExpectedConditions.elementToBeClickable(carouselPrev));
            prev.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- Scrolling ---
    public boolean scrollToBottom() {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean scrollToTop() {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- Subscription field ---
    public void enterSubscriptionEmail(String email) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(subscriptionInput));
        input.clear();
        input.sendKeys(email);
        driver.findElement(subscriptionButton).click();
    }

    public boolean isSubscriptionSuccess() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(subscriptionSuccess));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSubscriptionError() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(subscriptionError));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- Automation Exercise logo/button ---
    public boolean clickAutomationExerciseButton() {
        try {
            WebElement logo = wait.until(ExpectedConditions.elementToBeClickable(automationExerciseBtn));
            logo.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- Add to Cart ---
    public boolean clickFirstAddToCart() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(firstAddToCartBtn));
            btn.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
