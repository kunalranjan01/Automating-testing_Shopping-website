package com.myproject.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.myproject.config.ExtentManager;
import com.myproject.utils.ScreenshotUtil;
import org.testng.IConfigurationListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;

public class TestListener implements ITestListener, IConfigurationListener {
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
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        String testName = methodName + " - " + className;
        ExtentTest test = extent.createTest(testName);
        // add description if provided
        if (result.getMethod().getDescription() != null) {
            try {
                test.assignCategory(result.getMethod().getDescription());
            } catch (Exception ignored) { }
        }
        testThread.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest t = testThread.get();
        if (t != null) t.log(Status.PASS, "Test passed");
        // clear thread-local to avoid subsequent config logs being attached to this test
        testThread.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest t = testThread.get();
        try {
            // Try to capture screenshot (if driver available)
            tryAttachScreenshotFromInstance(result, result.getMethod().getMethodName());

            // Log failure to extent
            if (t != null) {
                if (result.getThrowable() != null) t.log(Status.FAIL, result.getThrowable());
                else t.log(Status.FAIL, "Test failed");
            } else {
                System.err.println("[TestListener] ExtentTest missing when handling failure.");
                if (result.getThrowable() != null) result.getThrowable().printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (t != null) t.log(Status.FAIL, "Exception in onTestFailure: " + e.getMessage());
        } finally {
            // Always clear the thread-local after handling failure
            testThread.remove();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest t = testThread.get();
        if (t != null) {
            if (result.getThrowable() != null) t.log(Status.SKIP, "Test skipped: " + result.getThrowable());
            else t.log(Status.SKIP, "Test skipped");
        } else {
            System.out.println("[TestListener] Test skipped: " + result.getMethod().getMethodName());
        }
        // clear thread-local so next logs won't attach here
        testThread.remove();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }

    /* -------------------------
       IConfigurationListener methods
       ------------------------- */

    @Override
    public void onConfigurationFailure(ITestResult itr) {
        System.err.println("[TestListener] Configuration failure for: " + itr.getMethod().getMethodName());
        if (itr.getThrowable() != null) itr.getThrowable().printStackTrace();
        tryAttachScreenshotFromInstance(itr, "config_failure_" + itr.getMethod().getMethodName());
    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {
        // Optional: log config success to console (not attached to Extent because test may not be started yet)
        System.out.println("[TestListener] Configuration success: " + itr.getMethod().getMethodName());
    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {
        System.out.println("[TestListener] Configuration skipped: " + itr.getMethod().getMethodName());
        tryAttachScreenshotFromInstance(itr, "config_skipped_" + itr.getMethod().getMethodName());
    }

    /* -------------------------
       Private helpers
       ------------------------- */

    /**
     * Try to reflectively call getDriver() on the test instance and attach a screenshot to the current ExtentTest.
     */
    private void tryAttachScreenshotFromInstance(ITestResult result, String namePrefix) {
        try {
            Object testInstance = result.getInstance();
            if (testInstance == null) {
                System.err.println("[TestListener] No test instance available to take screenshot.");
                return;
            }

            Method getter = null;
            // First try on the concrete class
            try {
                getter = testInstance.getClass().getMethod("getDriver");
            } catch (NoSuchMethodException nsme) {
                // Try superclass (common when tests extend BaseTest)
                try {
                    Class<?> sc = testInstance.getClass().getSuperclass();
                    if (sc != null) getter = sc.getMethod("getDriver");
                } catch (Exception ignore) {
                    // leave getter null
                }
            }

            if (getter == null) {
                System.err.println("[TestListener] No getDriver() method found on test instance; can't take screenshot.");
                return;
            }

            Object drv = getter.invoke(testInstance);
            if (!(drv instanceof org.openqa.selenium.WebDriver)) {
                System.err.println("[TestListener] getDriver() did not return a WebDriver instance.");
                return;
            }

            org.openqa.selenium.WebDriver driver = (org.openqa.selenium.WebDriver) drv;
            String path = ScreenshotUtil.takeScreenshot(driver, namePrefix);

            ExtentTest t = testThread.get();
            if (t != null) {
                if (path != null) {
                    t.addScreenCaptureFromPath(path, "Screenshot: " + namePrefix);
                    System.out.println("[TestListener] Captured screenshot at: " + path);
                } else {
                    System.err.println("[TestListener] Screenshot util returned null for " + namePrefix);
                }
            } else {
                System.out.println("[TestListener] No active ExtentTest to attach screenshot to, path=" + path);
            }
        } catch (Exception e) {
            System.err.println("[TestListener] Exception while attempting to take screenshot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* -------------------------
       Public helpers for tests/pages
       ------------------------- */

    // generic logger that accepts Extent Status
    public static void logStep(Status status, String message) {
        ExtentTest t = testThread.get();
        if (t != null) {
            t.log(status, message);
        } else {
            System.out.println("[LOG-STEP][" + status + "] " + message);
        }
    }
    public static void step(String message) {
        logStep(Status.INFO, "[STEP] " + message);
    }

    // convenient shortcuts
    public static void info(String message) { logStep(Status.INFO, message); }
    public static void pass(String message) { logStep(Status.PASS, message); }
    public static void fail(String message) { logStep(Status.FAIL, message); }
}
