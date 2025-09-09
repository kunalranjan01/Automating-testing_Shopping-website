package com.myproject.tests;

import com.myproject.base.BaseTest;
import com.myproject.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageUITest extends BaseTest {

    @Test(description = "Verify homepage loads and Home icon is present")
    public void verifyHomePageLoadsAndHomeIcon() {
        HomePage page = new HomePage(getDriver());
        // Basic check: page title not empty (homepage loaded)
        String title = getDriver().getTitle();
        Assert.assertTrue(title != null && title.trim().length() > 0, "Homepage title should be present");
        // Home icon
        Assert.assertTrue(page.isHomeIconPresent(), "Home icon/logo should be present on homepage");
    }

    @Test(description = "Verify Products icon available and clickable")
    public void verifyProductsIcon() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isProductsIconPresentAndClickable(), "Products icon should be available and clickable");
    }

    @Test(description = "Verify Cart icon available and clickable")
    public void verifyCartIcon() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isCartIconPresentAndClickable(), "Cart icon should be available and clickable");
    }

    @Test(description = "Verify Signup/Login icon available and clickable")
    public void verifySignupLoginIcon() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isSignupLoginPresentAndClickable(), "Signup/Login should be available and clickable");
    }

    @Test(description = "Verify Test Cases icon available and clickable")
    public void verifyTestCasesIcon() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isTestCasesPresentAndClickable(), "Test Cases icon should be available and clickable");
    }

    @Test(description = "Verify API Testing icon available and clickable")
    public void verifyApiTestingIcon() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isApiTestingPresentAndClickable(), "API Testing icon should be available and clickable");
    }

    @Test(description = "Verify Video Tutorials icon available and clickable")
    public void verifyVideoTutorialsIcon() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isVideoTutorialsPresentAndClickable(), "Video Tutorials should be available and clickable");
    }

    @Test(description = "Verify Contact Us icon available and clickable")
    public void verifyContactUsIcon() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isContactUsPresentAndClickable(), "Contact Us should be available and clickable");
    }

    @Test(description = "Verify Categories section is present")
    public void verifyCategoriesSection() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isCategoriesSectionPresent(), "Categories section should be visible on homepage");
    }

    @Test(description = "Verify Feature Items section is present")
    public void verifyFeatureItemsSection() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isFeatureItemsPresent(), "Feature Items section should be visible on homepage");
    }

    @Test(description = "Verify Brands section is present")
    public void verifyBrandsSection() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isBrandsSectionPresent(), "Brands section should be visible on homepage");
    }

    @Test(description = "Verify products are available on homepage")
    public void verifyProductsOnHomepage() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.areProductsAvailableOnHomepage(), "Products should be listed on homepage");
    }

    @Test(description = "Verify homepage carousel is working")
    public void verifyCarousel() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isCarouselWorking(), "Carousel should have slides or next control");
    }

    @Test(description = "Verify Test Case button/icon is clickable")
    public void verifyTestCaseButtonClickable() {
        HomePage page = new HomePage(getDriver());
        Assert.assertTrue(page.isTestCaseButtonClickable(), "Test Case button should be clickable");
    }
}
