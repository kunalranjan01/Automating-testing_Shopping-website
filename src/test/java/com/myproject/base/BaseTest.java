package com.myproject.base;

import com.myproject.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
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
        String configuredBrowser = ConfigReader.get("browser");
        String useBrowser = (browser == null || browser.trim().isEmpty()) ? configuredBrowser : browser;

        WebDriver driver;
        if ("firefox".equalsIgnoreCase(useBrowser)) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        } else {
            // default to chrome
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            // useful option for some chrome versions, keep if you see warnings
            options.addArguments("--remote-allow-origins=*");
            driver = new ChromeDriver(options);
        }

        tlDriver.set(driver);

        // timeouts from config
        int implicit = ConfigReader.getInt("implicit.wait", 10);
        int pageLoad = ConfigReader.getInt("page.load.timeout", 30);

        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoad));
        getDriver().manage().window().maximize();

        // open base URL
        String baseUrl = ConfigReader.get("base.url");
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            getDriver().get(baseUrl);
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
