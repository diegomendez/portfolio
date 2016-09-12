package com.wideo.metrics.bo.collector.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.wideo.metrics.model.collector.writer.csv.CSVField;

public class ExcelWriter {
    private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private Map<String, Integer> keyToColumn = new HashMap<String, Integer>();
    private int currentRow = 0;
    private OutputStream out;

    public void init(List<CSVField> fields, String sheetName,
            OutputStream out) {
        this.out = out;
        wb = new HSSFWorkbook();
        sheet = wb.createSheet(sheetName);

        // Print the column heading of all fields
        int col = 0;
        for (CSVField field : fields) {
            HSSFCell cell = getCell(sheet, currentRow, col);
            setText(cell, field.getName());
            keyToColumn.put(field.getName(), col);
            col++;
        }
        currentRow++;
    }

    public void writeRecord(Map<String, String> userRecord) {

        for (Entry<String, String> entry : userRecord.entrySet()) {
            HSSFCell cell = getCell(sheet, currentRow,
                    keyToColumn.get(entry.getKey()));
            setText(cell, entry.getValue());

        }
        currentRow++;

    }

    public int writeAndclose() {
        int length = 0;
        try {
            // Buffer the output in a byte output stream in memory just to
//get the length of the data written
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            length = baos.size();
            out.write(baos.toByteArray());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            try {
                wb.close();
            }
            catch (IOException e) {
                // Ignore silently
            }
        }
        return length;
    }

    /**
     * Convenient method to set a String as text content in a cell.
     * 
     * @param cell
     *            the cell in which the text must be put
     * @param text
     *            the text to put in the cell
     */
    public void setText(HSSFCell cell, String value) {
        try {
            // TODO: Esto se puede hacer mas lindo, quizas tener una list
//del formato de cada columna o algo asi
            double number = Double.parseDouble(value);
            cell.setCellValue(number);
        }
        catch (Exception e) {
            cell.setCellValue(value);
        }

    }

    /**
     * Convenient method to obtain the cell in the given sheet, row and
     * column.
     * <p>
     * Creates the row and the cell if they still doesn't already exist.
     * Thus, the column can be passed as an int, the method making the
     * needed downcasts.
     * 
     * @param sheet
     *            a sheet object. The first sheet is usually obtained by
     *            workbook.getSheetAt(0)
     * @param row
     *            thr row number
     * @param col
     *            the column number
     * @return the HSSFCell
     */
    public HSSFCell getCell(HSSFSheet sheet, int row, int col) {
        HSSFRow sheetRow = sheet.getRow(row);
        if (sheetRow == null) {
            sheetRow = sheet.createRow(row);
        }
        HSSFCell cell = sheetRow.getCell(col);
        if (cell == null) {
            cell = sheetRow.createCell(col);
        }
        return cell;
    }

}