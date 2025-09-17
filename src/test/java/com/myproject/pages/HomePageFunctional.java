package com.myproject.pages;

import com.myproject.listeners.TestListener;
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
    private final By automationExerciseBtn = By.cssSelector(".logo img, .navbar-brand img, .logo, .navbar-brand");

    // Add to Cart button (first product on homepage)
    private final By firstAddToCartBtn = By.cssSelector(".features_items .product-overlay .add-to-cart, .add-to-cart, .btn.btn-default.add-to-cart");

    public HomePageFunctional(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- Carousel actions ---
    public boolean clickCarouselNext() {
        TestListener.info("Step: clickCarouselNext() - attempt to click next carousel control");
        try {
            WebElement next = wait.until(ExpectedConditions.elementToBeClickable(carouselNext));
            next.click();
            TestListener.info("Step result: clickCarouselNext() -> clicked next");
            return true;
        } catch (Exception e) {
            TestListener.fail("Step failed: clickCarouselNext() -> " + e.getMessage());
            return false;
        }
    }

    public boolean clickCarouselPrev() {
        TestListener.info("Step: clickCarouselPrev() - attempt to click previous carousel control");
        try {
            WebElement prev = wait.until(ExpectedConditions.elementToBeClickable(carouselPrev));
            prev.click();
            TestListener.info("Step result: clickCarouselPrev() -> clicked prev");
            return true;
        } catch (Exception e) {
            TestListener.fail("Step failed: clickCarouselPrev() -> " + e.getMessage());
            return false;
        }
    }

    // --- Scrolling ---
    public boolean scrollToBottom() {
        TestListener.info("Step: scrollToBottom() - scroll page to bottom");
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            TestListener.info("Step result: scrollToBottom() -> scrolled to bottom");
            return true;
        } catch (Exception e) {
            TestListener.fail("Step failed: scrollToBottom() -> " + e.getMessage());
            return false;
        }
    }

    public boolean scrollToTop() {
        TestListener.info("Step: scrollToTop() - scroll page to top");
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            TestListener.info("Step result: scrollToTop() -> scrolled to top");
            return true;
        } catch (Exception e) {
            TestListener.fail("Step failed: scrollToTop() -> " + e.getMessage());
            return false;
        }
    }

    // --- Subscription field ---
    public void enterSubscriptionEmail(String email) {
        TestListener.info("Step: enterSubscriptionEmail() - enter '" + email + "' into subscription input and submit");
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(subscriptionInput));
            input.clear();
            input.sendKeys(email);
            TestListener.info("Entered subscription email: " + email);
            if (wait.until(ExpectedConditions.elementToBeClickable(subscriptionButton)) != null) {
                driver.findElement(subscriptionButton).click();
                TestListener.info("Clicked subscription button");
            } else {
                input.sendKeys(Keys.ENTER);
                TestListener.info("Submitted subscription via Enter key");
            }
        } catch (Exception e) {
            TestListener.fail("Step failed: enterSubscriptionEmail() -> " + e.getMessage());
            // don't rethrow — test should assert on isSubscriptionSuccess/isSubscriptionError
        }
    }

    public boolean isSubscriptionSuccess() {
        TestListener.info("Step: isSubscriptionSuccess() - checking for subscription success message");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(subscriptionSuccess));
            TestListener.info("Step result: isSubscriptionSuccess() -> success message visible");
            return true;
        } catch (Exception e) {
            TestListener.info("Step result: isSubscriptionSuccess() -> success message NOT visible (" + e.getMessage() + ")");
            return false;
        }
    }

    public boolean isSubscriptionError() {
        TestListener.info("Step: isSubscriptionError() - checking for subscription error message");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(subscriptionError));
            TestListener.info("Step result: isSubscriptionError() -> error message visible");
            return true;
        } catch (Exception e) {
            TestListener.info("Step result: isSubscriptionError() -> error message NOT visible (" + e.getMessage() + ")");
            return false;
        }
    }

    // --- Automation Exercise logo/button ---
    public boolean clickAutomationExerciseButton() {
        TestListener.info("Step: clickAutomationExerciseButton() - attempt to click the Automation Exercise logo/button");
        try {
            WebElement logo = wait.until(ExpectedConditions.elementToBeClickable(automationExerciseBtn));
            logo.click();
            TestListener.info("Step result: clickAutomationExerciseButton() -> clicked logo/button");
            return true;
        } catch (Exception e) {
            TestListener.fail("Step failed: clickAutomationExerciseButton() -> " + e.getMessage());
            return false;
        }
    }

    // --- Add to Cart ---
    public boolean clickFirstAddToCart() {
        TestListener.info("Step: clickFirstAddToCart() - attempt to click Add to Cart for first product");
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(firstAddToCartBtn));
            btn.click();
            TestListener.info("Step result: clickFirstAddToCart() -> clicked add to cart");
            return true;
        } catch (Exception e) {
            TestListener.fail("Step failed: clickFirstAddToCart() -> " + e.getMessage());
            return false;
        }
    }
}
