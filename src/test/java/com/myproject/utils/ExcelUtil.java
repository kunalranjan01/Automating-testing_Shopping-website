package com.myproject.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ExcelUtils - reading/writing helpers for TestNG data-driven tests.
 *
 * - readSheetAndWriteGeneratedEmails(...) : reads sheet and optionally writes a copy with generated emails.
 * - appendRowToSheet(...) : append a row to an existing sheet (creates sheet/header if missing).
 * - readSheetAsDataProvider(...) : convenience to return Object[][] for TestNG DataProvider.
 *
 * Requires Apache POI on classpath (poi + poi-ooxml).
 */
public final class ExcelUtil {

    private ExcelUtil() { /* utility */ }

    /**
     * Read an xlsx sheet and return Object[][] suitable for TestNG DataProvider.
     * If writeBackGeneratedEmails==true, generated/modified emails will be written
     * into a copy at outputPath.
     *
     * See earlier description for email generation behaviour.
     *
     * @param inputPath existing xlsx
     * @param sheetName sheet to read (null => first sheet)
     * @param outputPath path to write modified copy (if null -> skip write)
     * @param writeBackGeneratedEmails whether to write modified workbook
     * @return Object[][] rows (each row is String[])
     */
    public static Object[][] readSheetAndWriteGeneratedEmails(String inputPath, String sheetName, String outputPath, boolean writeBackGeneratedEmails) {
        List<String[]> rows = new ArrayList<>();
        try (InputStream is = new FileInputStream(inputPath); Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = (sheetName == null || sheetName.isEmpty()) ? wb.getSheetAt(0) : wb.getSheet(sheetName);
            if (sheet == null) throw new RuntimeException("Sheet not found: " + sheetName);

            Iterator<Row> it = sheet.iterator();
            if (!it.hasNext()) return new Object[0][];
            Row header = it.next();
            int headerCols = header.getLastCellNum() > 0 ? header.getLastCellNum() : 1;

            // identify email column index from header
            int emailCol = -1;
            for (int c = 0; c < headerCols; c++) {
                Cell hc = header.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (hc != null) {
                    String hv = hc.toString().trim().toLowerCase();
                    if (hv.contains("email")) { emailCol = c; break; }
                }
            }

            while (it.hasNext()) {
                Row r = it.next();
                int last = Math.max(headerCols, r.getLastCellNum() > 0 ? r.getLastCellNum() : headerCols);
                String[] data = new String[last];
                for (int c = 0; c < last; c++) {
                    Cell cell = r.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell == null) data[c] = "";
                    else {
                        cell.setCellType(CellType.STRING);
                        data[c] = cell.getStringCellValue().trim();
                    }
                }

                // maybe generate/modify email
                if (emailCol >= 0) {
                    String rawEmail = data.length > emailCol ? (data[emailCol] == null ? "" : data[emailCol]) : "";
                    String newEmail = generateUniqueEmailIfNeeded(rawEmail);
                    if (!newEmail.equals(rawEmail)) {
                        data[emailCol] = newEmail;
                        Cell emailCell = r.getCell(emailCol, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        emailCell.setCellValue(newEmail);
                    }
                }

                rows.add(data);
            }

            // write back if requested and outputPath provided
            if (writeBackGeneratedEmails && outputPath != null && !outputPath.trim().isEmpty()) {
                try (OutputStream os = new FileOutputStream(outputPath)) {
                    wb.write(os);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read/write Excel: " + e.getMessage(), e);
        }

        Object[][] out = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) out[i] = rows.get(i);
        return out;
    }

    /**
     * Convenience wrapper to call readSheetAndWriteGeneratedEmails with no write-back
     */
    public static Object[][] readSheetAsDataProvider(String inputPath, String sheetName) {
        return readSheetAndWriteGeneratedEmails(inputPath, sheetName, null, false);
    }
    public static Object[][] readSheetSimple(String inputPath, String sheetName) {
        List<String[]> rows = new ArrayList<>();
        try (InputStream is = new FileInputStream(inputPath); Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = (sheetName == null || sheetName.isEmpty()) ? wb.getSheetAt(0) : wb.getSheet(sheetName);
            if (sheet == null) throw new RuntimeException("Sheet not found: " + sheetName);

            Iterator<Row> it = sheet.iterator();
            if (!it.hasNext()) return new Object[0][];
            it.next(); // skip header row

            while (it.hasNext()) {
                Row r = it.next();
                int last = r.getLastCellNum();
                String[] data = new String[last];
                for (int c = 0; c < last; c++) {
                    Cell cell = r.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell == null) {
                        data[c] = "";
                    } else {
                        cell.setCellType(CellType.STRING);
                        data[c] = cell.getStringCellValue().trim();
                    }
                }
                rows.add(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel: " + e.getMessage(), e);
        }

        // convert to Object[][]
        Object[][] out = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            out[i] = rows.get(i);
        }
        return out;
    }

    /**
     * Append a row of string values to a sheet in an xlsx file. If sheet does not exist it will be created.
     * If sheet is newly created and header != null, the header row will be created as first row.
     *
     * This method reads the workbook into memory, appends, and writes it back. Caller should avoid concurrent
     * appends from multiple processes.
     *
     * @param path existing xlsx file path (will be created if missing)
     * @param sheetName sheet to append to
     * @param header optional header row (String[]) to add if creating sheet
     * @param values values to append (String...)
     */
    public static synchronized void appendRowToSheet(String path, String sheetName, String[] header, String... values) {
        final int MAX_RETRIES = 5;
        final long RETRY_WAIT_MS = 500L;
        int attempt = 0;
        Exception lastEx = null;

        while (attempt < MAX_RETRIES) {
            attempt++;
            Workbook wb = null;
            File tmp = null;
            try {
                File file = new File(path);
                if (!file.exists()) {
                    wb = new XSSFWorkbook();
                } else {
                    try (InputStream is = new FileInputStream(file)) {
                        wb = new XSSFWorkbook(is);
                    }
                }

                Sheet sheet = wb.getSheet(sheetName);
                if (sheet == null) {
                    sheet = wb.createSheet(sheetName);
                    if (header != null && header.length > 0) {
                        Row h = sheet.createRow(0);
                        for (int c = 0; c < header.length; c++) {
                            Cell cell = h.createCell(c);
                            cell.setCellValue(header[c] == null ? "" : header[c]);
                        }
                    }
                }

                int lastRow = sheet.getLastRowNum();
                int appendIndex = lastRow + 1;
                if (sheet.getPhysicalNumberOfRows() == 0) appendIndex = 0;

                Row newRow = sheet.createRow(appendIndex);
                for (int i = 0; i < values.length; i++) {
                    Cell cell = newRow.createCell(i);
                    cell.setCellValue(values[i] == null ? "" : values[i]);
                }

                // write to temporary file first
                tmp = File.createTempFile("excel_append_", ".tmp");
                try (OutputStream os = new FileOutputStream(tmp)) {
                    wb.write(os);
                } finally {
                    try { wb.close(); } catch (Exception ignored) {}
                }

                // replace original (atomic move if possible)
                File dest = new File(path);
                if (!dest.exists()) {
                    File parent = dest.getParentFile();
                    if (parent != null && !parent.exists()) parent.mkdirs();
                }
                try {
                    java.nio.file.Files.move(tmp.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
                } catch (Exception moveEx) {
                    java.nio.file.Files.move(tmp.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                // success
                return;
            } catch (Exception e) {
                lastEx = e;
                try { if (tmp != null && tmp.exists()) tmp.delete(); } catch (Exception ignored) {}
                try { if (wb != null) wb.close(); } catch (Exception ignored) {}
                try { Thread.sleep(RETRY_WAIT_MS); } catch (InterruptedException ignored) {}
            }
        }

        throw new RuntimeException("Failed to append to Excel file after " + MAX_RETRIES + " attempts: " + (lastEx == null ? "" : lastEx.getMessage()), lastEx);
    }

    /**
     * Internal helper — generate unique email if raw requires it.
     */
    private static String generateUniqueEmailIfNeeded(String raw) {
        long ts = System.currentTimeMillis();
        if (raw == null) raw = "";
        raw = raw.trim();
        try {
            if (!raw.isEmpty()) {
                if (raw.contains("{unique}")) {
                    int at = raw.indexOf('@');
                    if (at > 0) {
                        String local = raw.substring(0, at).replace("{unique}", "+" + ts);
                        String domain = raw.substring(at + 1);
                        return local + "@" + domain;
                    } else {
                        return raw.replace("{unique}", String.valueOf(ts)) + "@example.com";
                    }
                }
                int at = raw.indexOf('@');
                if (at > 0) {
                    String local = raw.substring(0, at);
                    String domain = raw.substring(at + 1);
                    return local + "+" + ts + "@" + domain;
                }
                return raw + "+" + ts + "@example.com";
            } else {
                return "auto" + ts + "@example.com";
            }
        } catch (Exception e) {
            return "auto" + ts + "@example.com";
        }
    }
}
