package com.myproject.base;

import com.myproject.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public class BaseTest {
    // ThreadLocal WebDriver for thread-safe parallel execution
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    /**
     * Setup executed before each test method.
     * Browser can be passed from testng.xml via parameter "browser" or read from config.properties.
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional String browser) {
        // debug info to help diagnose suite vs IDE differences
        System.out.println("[BaseTest] user.dir = " + System.getProperty("user.dir"));
        System.out.println("[BaseTest] Loading config: base.url=" + ConfigReader.get("base.url") + ", browser param=" + browser);

        String configuredBrowser = ConfigReader.get("browser");
        String useBrowser = (browser == null || browser.trim().isEmpty()) ? configuredBrowser : browser;

        WebDriver driver = null;
        try {
            if ("firefox".equalsIgnoreCase(useBrowser)) {
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
            } else {
                // default to chrome
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();

                // Hardening flags to reduce renderer/timeouts in CI or constrained environment
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-extensions");
                options.addArguments("--disable-gpu");
                // Use a safe page load strategy - KEEP AS NORMAL if you prefer full page load behavior,
                // but NONE gives control to explicit waits (useful for flaky pages)
                options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

                driver = new ChromeDriver(options);
            }

            // set driver early so TestListener can access it on config failure
            tlDriver.set(driver);

            // timeouts from config
            int implicit = ConfigReader.getInt("implicit.wait", 10);
            int pageLoad = ConfigReader.getInt("page.load.timeout", 60); // increase default to 60s

            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));
            getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoad));
            getDriver().manage().window().maximize();

            // open base URL with retry + explicit wait for a stable element (logo)
            String baseUrl = ConfigReader.get("base.url");
            if (baseUrl != null && !baseUrl.trim().isEmpty()) {
                safeGet(baseUrl); // will try once, then retry on TimeoutException
                // wait for the homepage to be "ready" - look for a reliable element (logo)
                WebDriverWait initialWait = new WebDriverWait(getDriver(), Duration.ofSeconds(Math.max(implicit, 15)));
                initialWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".logo, .site-logo, .navbar-brand img")));
            }
        } catch (Exception e) {
            // print useful debug and rethrow so TestNG marks config as failed (not swallowed)
            System.err.println("=== BaseTest.setUp FAILED: " + e.getMessage());
            e.printStackTrace();
            // ensure driver is quit on failure if it was created
            try {
                if (driver != null) {
                    driver.quit();
                }
            } catch (Exception ignored) { }
            tlDriver.remove();
            throw new RuntimeException("Failed to set up WebDriver", e);
        }
    }

    /**
     * Safe navigation with a single retry for TimeoutException to reduce flakiness.
     */
    private void safeGet(String url) {
        int attempts = 0;
        while (attempts < 2) {
            try {
                getDriver().get(url);
                return;
            } catch (org.openqa.selenium.TimeoutException te) {
                attempts++;
                System.err.println("[BaseTest] get() timed out (attempt " + attempts + "): " + te.getMessage());
                if (attempts >= 2) {
                    throw te;
                }
                try {
                    // brief pause then retry
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Return the current thread's WebDriver instance.
     */
    public WebDriver getDriver() {
        return tlDriver.get();
    }

    /**
     * Quit and cleanup the driver after each test method.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = getDriver();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {}
            tlDriver.remove();
        }
    }
}
