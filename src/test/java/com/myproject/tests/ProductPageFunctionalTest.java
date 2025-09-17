package com.myproject.tests;

import com.myproject.base.BaseTest;
import com.myproject.pages.ProductPageFunctional;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProductPageFunctionalTest extends BaseTest {
    private ProductPageFunctional page;

    @BeforeMethod
    public void setupPage() {
        page = new ProductPageFunctional(getDriver());
        getDriver().get("https://www.automationexercise.com/products");
    }

    @Test(priority = 1)
    public void verifyNavigationToProductsPage() {
        page.navigateToProductsPage();
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/products"));
    }

    @Test(priority = 2)
    public void verifySearchFunctionality() {
        page.searchProduct("Blue Top");
        Assert.assertTrue(page.isSearchResultVisible());
    }

    @Test(priority = 3)
    public void verifyAddToCartAndContinueShopping() {
        Assert.assertTrue(page.addFirstProductToCart());
        Assert.assertTrue(page.clickContinueShopping(), "Continue shopping should work");
    }

    @Test(priority = 4)
    public void verifyAddToCartAndViewCart() {
        Assert.assertTrue(page.addFirstProductToCart());
        Assert.assertTrue(page.clickViewCart(), "View cart should redirect");
    }

    @Test(priority = 5)
    public void verifyWomenCategoryFilter() {
        Assert.assertTrue(page.clickWomenCategory());
    }

    @Test(priority = 6)
    public void verifyMenCategoryFilter() {
        Assert.assertTrue(page.clickMenCategory());
    }

    @Test(priority = 7)
    public void verifyKidsCategoryFilter() {
        Assert.assertTrue(page.clickKidsCategory());
    }

    @Test(priority = 8)
    public void verifyPoloBrandFilter() {
        Assert.assertTrue(page.clickPoloBrand());
    }

    @Test(priority = 9)
    public void verifyHMBrandFilter() {
        Assert.assertTrue(page.clickHMBrand());
    }

    @Test(priority = 10)
    public void verifyMadameBrandFilter() {
        Assert.assertTrue(page.clickMadameBrand());
    }

    @Test(priority = 11)
    public void verifySubscriptionWithValidEmail() {
        Assert.assertTrue(page.subscribeWithEmail("testuser@example.com"));
    }

    @Test(priority = 12)
    public void verifyFooterPresenceAfterScroll() {
        Assert.assertTrue(page.scrollToBottomAndCheckFooter());
    }

    @Test(priority = 13)
    public void verifyScrollUpButtonWorks() {
        Assert.assertTrue(page.scrollUpToTop());
    }

    @Test(priority = 14)
    public void verifyMultipleProductSearch() {
        page.searchProduct("Dress");
        Assert.assertTrue(page.isSearchResultVisible());
    }

    @Test(priority = 15)
    public void verifyProductGridVisibleOnPageLoad() {
        Assert.assertTrue(page.isSearchResultVisible(), "Products should be visible on page load");
    }
}
