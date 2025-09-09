package com.myproject.tests;

import com.myproject.base.BaseTest;
import com.myproject.pages.HomePageFunctional;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageFunctionalTest extends BaseTest {

    @Test(description = "Verify left and right arrows on homepage carousel work")
    public void verifyCarouselArrows() {
        HomePageFunctional page = new HomePageFunctional(getDriver());
        Assert.assertTrue(page.clickCarouselNext(), "Carousel Next arrow should work");
        Assert.assertTrue(page.clickCarouselPrev(), "Carousel Previous arrow should work");
    }

    @Test(description = "Verify vertical scrolling works on homepage")
    public void verifyVerticalScrolling() {
        HomePageFunctional page = new HomePageFunctional(getDriver());
        Assert.assertTrue(page.scrollToBottom(), "Should be able to scroll down");
        Assert.assertTrue(page.scrollToTop(), "Should be able to scroll up");
    }

    @Test(description = "Verify subscription accepts valid email")
    public void verifySubscriptionValidEmail() {
        HomePageFunctional page = new HomePageFunctional(getDriver());
        page.enterSubscriptionEmail("validemail@test.com");
        Assert.assertTrue(page.isSubscriptionSuccess(), "Subscription success message should appear for valid email");
    }

    @Test(description = "Verify subscription rejects invalid email")
    public void verifySubscriptionInvalidEmail() {
        HomePageFunctional page = new HomePageFunctional(getDriver());
        page.enterSubscriptionEmail("invalid-email");
        Assert.assertTrue(page.isSubscriptionError(), "Subscription error message should appear for invalid email");
    }

    @Test(description = "Verify Automation Exercise button works")
    public void verifyAutomationExerciseButton() {
        HomePageFunctional page = new HomePageFunctional(getDriver());
        Assert.assertTrue(page.clickAutomationExerciseButton(), "Automation Exercise button/logo should be clickable");
    }

    @Test(description = "Verify Add to Cart button works on homepage")
    public void verifyAddToCartButton() {
        HomePageFunctional page = new HomePageFunctional(getDriver());
        Assert.assertTrue(page.clickFirstAddToCart(), "Add to Cart button should be clickable on homepage");
    }
}
