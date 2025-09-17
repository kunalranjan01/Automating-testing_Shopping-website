package com.myproject.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelDataProvider {

    private static final String FILE_PATH = "src/test/resources/testdata/signup_data.xlsx";

    @DataProvider(name = "signupData")
    public static Object[][] getSignupData(Method method) throws IOException {
        return readExcelData(FILE_PATH, "Sheet1");
    }

    private static Object[][] readExcelData(String filePath, String sheetName) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        int rows = sheet.getPhysicalNumberOfRows();
        int cols = sheet.getRow(0).getPhysicalNumberOfCells();

        Object[][] data = new Object[rows - 1][cols];

        for (int i = 1; i < rows; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < cols; j++) {
                Cell cell = row.getCell(j);
                String value = (cell == null) ? "" : cell.toString();

                // Replace {unique} with timestamp for email uniqueness
                if (value.contains("{unique}")) {
                    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    value = value.replace("{unique}", timestamp);
                }
                data[i - 1][j] = value;
            }
        }
        workbook.close();
        fis.close();
        return data;
    }
}
