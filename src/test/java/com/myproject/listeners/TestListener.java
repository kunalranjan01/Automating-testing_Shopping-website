package com.myproject.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.myproject.config.ExtentManager;
import com.myproject.utils.ScreenshotUtil;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;

public class TestListener implements ITestListener {
    private static final ExtentReports extent = ExtentManager.getInstance();
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) { }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = extent.createTest(testName);
        testThread.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        testThread.get().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            Object testInstance = result.getInstance();
            Method m = testInstance.getClass().getMethod("getDriver");
            org.openqa.selenium.WebDriver driver = (org.openqa.selenium.WebDriver) m.invoke(testInstance);
            String path = ScreenshotUtil.takeScreenshot(driver, result.getMethod().getMethodName());
            testThread.get().fail(result.getThrowable());
            if (path != null) {
                testThread.get().addScreenCaptureFromPath(path);
            }
        } catch (NoSuchMethodException nsme) {
            System.err.println("No getDriver() method found on test instance. Can't take screenshot.");
            testThread.get().fail(result.getThrowable());
        } catch (Exception e) {
            e.printStackTrace();
            testThread.get().fail("Failed to attach screenshot: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        testThread.get().skip("Test skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }
}
