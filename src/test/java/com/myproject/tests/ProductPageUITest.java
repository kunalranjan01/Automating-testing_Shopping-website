package com.myproject.tests;

import com.myproject.base.BaseTest;
import com.myproject.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProductPageUITest extends BaseTest {

    private ProductPage page;

    @BeforeMethod
    public void beforeEach() {
        page = new ProductPage(getDriver());
        // navigate to product page explicitly
        getDriver().get("https://www.automationexercise.com/products");
    }

    @Test public void verifyProductsIcon() { Assert.assertTrue(page.isProductsLinkPresentAndClickable(), "Products icon should be present & clickable"); }
    @Test public void verifyHomeIcon() { Assert.assertTrue(page.isHomeLinkPresentAndClickable(), "Home icon should be present & clickable"); }
    @Test public void verifyCartIcon() { Assert.assertTrue(page.isCartLinkPresentAndClickable(), "Cart icon should be present & clickable"); }
    @Test public void verifySignupLoginIcon() { Assert.assertTrue(page.isSignupLoginPresentAndClickable(), "Signup/Login icon should be present & clickable"); }
    @Test public void verifyTestCasesIcon() { Assert.assertTrue(page.isTestCasesPresentAndClickable(), "Test Cases icon should be present & clickable"); }

    @Test public void verifyAppLogoPresence() { Assert.assertTrue(page.isSiteLogoPresent(), "Application logo should be visible on product page"); }
    @Test public void verifyOffersLogoPresence() { Assert.assertTrue(page.isOffersLogoPresent(), "Offers logo should be present"); }
    @Test public void verifySearchBarPresence() { Assert.assertTrue(page.isSearchBarPresent(), "Search bar should be present"); }
    @Test public void verifyCategoriesSection() { Assert.assertTrue(page.isCategoriesSectionPresent(), "Categories section should be visible"); }
    @Test public void verifyBrandsSection() { Assert.assertTrue(page.isBrandsSectionPresent(), "Brands section should be visible"); }
    @Test public void verifyAllProductsSection() { Assert.assertTrue(page.isAllProductsSectionPresent(), "All products section should be visible"); }

    @Test public void verifyWomenCategory() { Assert.assertTrue(page.isWomenCategoryPresentAndClickable(), "Women category should display & be clickable"); }
    @Test public void verifyMenCategory() { Assert.assertTrue(page.isMenCategoryPresentAndClickable(), "Men category should display & be clickable"); }
    @Test public void verifyKidsCategory() { Assert.assertTrue(page.isKidsCategoryPresentAndClickable(), "Kids category should display & be clickable"); }

    @Test public void verifyBrandAllenSolly() { Assert.assertTrue(page.isBrandAllenSollyPresentAndClickable(), "Allen Solly Junior brand should be present & clickable"); }
    @Test public void verifyBrandKookieKids() { Assert.assertTrue(page.isBrandKookieKidsPresentAndClickable(), "Kookie Kids brand should be present & clickable"); }
    @Test public void verifyBrandBiba() { Assert.assertTrue(page.isBrandBibaPresentAndClickable(), "Biba brand should be present & clickable"); }

    @Test public void verifyHoverOverlayShowsPriceAndAddToCart() { Assert.assertTrue(page.isHoverOverlayShowingPriceAndAddToCart(), "Hovering product should show overlay with price & Add to Cart"); }

    @Test public void verifyFooterCopyrightText() { Assert.assertTrue(page.isFooterCopyrightTextPresent(), "Footer copyright should show correct text"); }
    @Test public void verifyScrollUpButton() { Assert.assertTrue(page.clickScrollUpAndVerifyTop(), "Clicking scroll-up should return to top"); }

    @Test public void verifyProductImageFullScreen() { Assert.assertTrue(page.isProductImageFullScreenOnClick(), "Clicking product image should open full screen/modal"); }
    @Test public void verifySpecialOfferClickable() { Assert.assertTrue(page.isSpecialOfferClickable(), "Special offer section should be clickable"); }
}
