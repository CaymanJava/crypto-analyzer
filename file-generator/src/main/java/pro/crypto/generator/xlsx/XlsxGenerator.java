package pro.crypto.generator.xlsx;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import pro.crypto.model.tick.Tick;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

@Component
@Slf4j
public class XlsxGenerator {

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static String[] TICK_SHEET_COLUMNS = {"Time", "Volume", "Open", "High", "Low", "Close"};
    private final static String SHEET_NAME = "Tick Data";

    public byte[] generateTickFile(Tick[] data) throws IOException {
        log.trace("Generating Xlsx file with tick data {tickDataSize: {}}", data.length);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(SHEET_NAME);
        CellStyle headerCellStyle = configureHeaderCellStyle(workbook);
        Row headerRow = sheet.createRow(0);
        createHeader(headerCellStyle, headerRow);
        fillInData(data, sheet);
        autoSizeColumns(sheet);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        log.trace("Generated Xlsx file with tick data {tickDataSize: {}}", data.length);
        return byteArrayOutputStream.toByteArray();
    }

    private CellStyle configureHeaderCellStyle(Workbook workbook) {
        log.trace("Configuring header cell style");
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(configureFont(workbook));
        return headerCellStyle;
    }

    private Font configureFont(Workbook workbook) {
        log.trace("Configuring header font");
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());
        return headerFont;
    }

    private void createHeader(CellStyle headerCellStyle, Row headerRow) {
        log.trace("Creating header");
        IntStream.range(0, TICK_SHEET_COLUMNS.length)
                .forEach(idx -> createHeaderCell(headerRow, headerCellStyle, idx));
    }

    private void createHeaderCell(Row headerRow, CellStyle headerCellStyle, int columnIndex) {
        Cell cell = headerRow.createCell(columnIndex);
        cell.setCellValue(TICK_SHEET_COLUMNS[columnIndex]);
        cell.setCellStyle(headerCellStyle);
    }

    private void fillInData(Tick[] data, Sheet sheet) {
        log.trace("Filling in data");
        IntStream.rangeClosed(1, data.length)
                .forEach(idx -> fillInRow(sheet, data[idx - 1], idx));
    }

    private void fillInRow(Sheet sheet, Tick tick, int rowNumber) {
        Row row = sheet.createRow(rowNumber);
        row.createCell(0)
                .setCellValue(tick.getTickTime().format(FORMATTER));
        row.createCell(1)
                .setCellValue(tick.getBaseVolume().doubleValue());
        row.createCell(2)
                .setCellValue(tick.getOpen().doubleValue());
        row.createCell(3)
                .setCellValue(tick.getHigh().doubleValue());
        row.createCell(4)
                .setCellValue(tick.getLow().doubleValue());
        row.createCell(5)
                .setCellValue(tick.getClose().doubleValue());
    }

    private void autoSizeColumns(Sheet sheet) {
        IntStream.range(0, TICK_SHEET_COLUMNS.length)
                .forEach(sheet::autoSizeColumn);
    }

}
