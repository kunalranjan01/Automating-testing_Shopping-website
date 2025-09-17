package com.myproject.tests;

import com.myproject.base.BaseTest;
import com.myproject.listeners.TestListener;
import com.myproject.pages.RegistrationPageFunctional;
import com.myproject.pages.SignupPageFunctional;
import com.myproject.utils.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Signup -> Registration test.
 * Writes only email + password into GENERATED_COPY Sheet2 for each row (attempted).
 */
public class SignupRegistrationTest extends BaseTest {

    private static final String TESTDATA_DIR = System.getProperty("user.dir") + "/src/test/resources/testdata/";
    private static final String INPUT_FILE = TESTDATA_DIR + "signup_data.xlsx";
    private static final String GENERATED_COPY = TESTDATA_DIR + "signup_data_generated.xlsx";
    private static final String INPUT_SHEET = "Sheet1";
    private static final String OUTPUT_SHEET = "Sheet2";

    @DataProvider(name = "signupData")
    public Object[][] signupData() {
        // Reads Sheet1, optionally writes generated emails into GENERATED_COPY and returns rows for TestNG
        return ExcelUtil.readSheetAndWriteGeneratedEmails(INPUT_FILE, INPUT_SHEET, GENERATED_COPY, true);
    }

    @Test(dataProvider = "signupData", description = "Signup -> Registration; append only email+password into Sheet2 (generated copy)")
    public void tc_signup_and_register(String... row) {
        // columns mapping (Sheet1):
        // 0:name, 1:email, 2:password, 3:firstName, 4:lastName, 5:address, 6:country, 7:state, 8:city, 9:zipcode, 10:mobile,
        // 11:day, 12:month, 13:year, 14:newsletter, 15:offers
        String name = row.length > 0 ? row[0] : "";
        String email = row.length > 1 ? row[1] : "";
        String password = row.length > 2 ? row[2] : "";
        String firstName = row.length > 3 ? row[3] : "";
        String lastName = row.length > 4 ? row[4] : "";
        String address = row.length > 5 ? row[5] : "";
        String country = row.length > 6 ? row[6] : "";
        String state = row.length > 7 ? row[7] : "";
        String city = row.length > 8 ? row[8] : "";
        String zipcode = row.length > 9 ? row[9] : "";
        String mobile = row.length > 10 ? row[10] : "";
        String day = row.length > 11 ? row[11] : "";
        String month = row.length > 12 ? row[12] : "";
        String year = row.length > 13 ? row[13] : "";
        String newsletterRaw = row.length > 14 ? row[14] : "";
        String offersRaw = row.length > 15 ? row[15] : "";

        boolean newsletterFlag = parseBoolean(newsletterRaw);
        boolean offersFlag = parseBoolean(offersRaw);

        TestListener.info("=== RUN ROW: name='" + safe(name) + "', email='" + safe(email) + "' ===");

        boolean overallSuccess = false;
        String message = "";

        try {
            // SIGNUP
            SignupPageFunctional signup = new SignupPageFunctional(getDriver());
            signup.open();

            boolean filled = signup.fillSignup(name, email);
            TestListener.info("Signup.fillSignup -> " + filled);
            if (!filled) {
                message = "Signup fill failed";
                TestListener.fail(message);
                appendResult(email, password);
                Assert.fail(message);
                return;
            }

            boolean clicked = signup.clickSignup();
            TestListener.info("Signup.clickSignup -> " + clicked);
            if (!clicked) {
                message = "Signup click failed";
                TestListener.fail(message);
                appendResult(email, password);
                Assert.fail(message);
                return;
            }

            boolean proceed = signup.isProceedToRegistration();
            TestListener.info("Signup.isProceedToRegistration -> " + proceed);
            if (!proceed) {
                message = "Signup did not proceed to registration (validation)";
                TestListener.fail(message);
                appendResult(email, password);
                Assert.fail(message);
                return;
            }

            // REGISTRATION
            RegistrationPageFunctional reg = new RegistrationPageFunctional(getDriver());
            boolean regFilled = reg.fillRegistration(password, firstName, lastName, address, country, state, city, zipcode, mobile, day, month, year, newsletterFlag, offersFlag);
            TestListener.info("Registration.fillRegistration -> " + regFilled);
            if (!regFilled) {
                message = "Registration fields not filled";
                TestListener.fail(message);
                appendResult(email, password);
                Assert.fail(message);
                return;
            }

            boolean regSubmitted = reg.submitRegistration();
            TestListener.info("Registration.submitRegistration -> " + regSubmitted);
            if (!regSubmitted) {
                message = "Registration submit did not detect success";
                TestListener.fail(message);
                appendResult(email, password);
                Assert.fail(message);
                return;
            }

            overallSuccess = true;
            message = "Signup+Registration completed successfully";
            TestListener.pass(message);

        } catch (Exception e) {
            overallSuccess = false;
            message = "Exception: " + e.getMessage();
            TestListener.fail(message);
            // ensure we append attempted credentials
            try { appendResult(email, password); } catch (Exception ex) { TestListener.fail("Failed to append credentials: " + ex.getMessage()); }
            throw new RuntimeException(message, e);
        } finally {
            // Always append email+password whether PASS or FAIL so Sheet2 contains creds for login phase.
            try { appendResult(email, password); } catch (Exception ex) { TestListener.fail("Failed to append credentials: " + ex.getMessage()); }
        }

        Assert.assertTrue(overallSuccess, "Signup+Registration flow must succeed for email=" + email + ". Message: " + message);
    }

    /**
     * Append only email & password into GENERATED_COPY sheet OUTPUT_SHEET.
     */
    private void appendResult(String email, String password) {
        String[] header = new String[]{"email", "password"};
        try {
            ExcelUtil.appendRowToSheet(GENERATED_COPY, OUTPUT_SHEET, header,
                    email == null ? "" : email,
                    password == null ? "" : password
            );
            TestListener.info("Appended result (email+password only) to " + GENERATED_COPY + " Sheet \"" + OUTPUT_SHEET + "\" for " + email);
        } catch (Exception ex) {
            // log and rethrow so caller is aware if you want tests to stop on append errors:
            TestListener.fail("Failed to append result to Sheet2: " + ex.getMessage());
            throw new RuntimeException("Failed to append to Sheet2", ex);
        }
    }

    private boolean parseBoolean(String s) {
        if (s == null) return false;
        String v = s.trim().toLowerCase();
        return v.equals("yes") || v.equals("true") || v.equals("1") || v.equals("y");
    }

    private String safe(String s) { return s == null ? "" : s; }
}
