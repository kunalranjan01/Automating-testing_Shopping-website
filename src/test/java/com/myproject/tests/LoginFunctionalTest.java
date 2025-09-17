package com.myproject.tests;

import com.myproject.base.BaseTest;
import com.myproject.listeners.TestListener;
import com.myproject.pages.LoginPageFunctional;
import com.myproject.utils.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class LoginFunctionalTest extends BaseTest {

    private static final String TESTDATA_DIR = System.getProperty("user.dir") + "/src/test/resources/testdata/";
    private static final String INPUT_FILE = TESTDATA_DIR + "signup_data.xlsx";
    private static final String SHEET2 = "Sheet2";

    @DataProvider(name = "loginRows")
    public Object[][] loginRows() {
        // use simple reader that does not mutate emails
        Object[][] raw = ExcelUtil.readSheetSimple(INPUT_FILE, SHEET2);
        List<Object[]> rows = new ArrayList<>();
        if (raw == null || raw.length == 0) {
            TestListener.info("Sheet2 empty or not found: " + INPUT_FILE);
            return new Object[0][];
        }

        // detect header row: if first row contains 'email' or 'password' treat as header
        boolean header = false;
        Object[] first = raw[0];
        if (first != null) {
            for (Object o : first) {
                if (o == null) continue;
                String s = String.valueOf(o).trim().toLowerCase();
                if (s.contains("email") || s.contains("password")) { header = true; break; }
            }
        }

        int start = header ? 1 : 0;
        for (int r = start; r < raw.length; r++) {
            Object[] row = raw[r];
            if (row == null) continue;
            String email = row.length > 0 && row[0] != null ? String.valueOf(row[0]).trim() : "";
            String password = row.length > 1 && row[1] != null ? String.valueOf(row[1]).trim() : "";
            TestListener.info("[Sheet2 row " + r + "] email=" + email + " pwdPresent=" + (!password.isEmpty()));
            if (!email.isEmpty() && !password.isEmpty()) {
                rows.add(new Object[]{email, password});
            } else {
                TestListener.info("Skipping row " + r + " due to missing email/password");
            }
        }

        if (rows.isEmpty()) {
            TestListener.info("No usable credentials found in Sheet2 (" + INPUT_FILE + ")");
            return new Object[0][];
        }

        Object[][] out = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) out[i] = rows.get(i);
        return out;
    }

    @Test(dataProvider = "loginRows", description = "Login using credentials in Sheet2 (col1=email, col2=password)")
    public void tc_login_from_sheet(String email, String password) {
        TestListener.info("Starting login test for: " + email);
        LoginPageFunctional page = new LoginPageFunctional(getDriver());
        Assert.assertTrue(page.openLoginPage(), "Login page should open");
        boolean ok = page.login(email, password);
        Assert.assertTrue(ok, "Login should succeed for " + email);
    }
}
