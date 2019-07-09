package Petplan;

import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

@Data
public class MyExcel {
    private String path;
    private FileInputStream file;
    private Workbook wb;
    private Sheet ws;

    public MyExcel(String path, int sheet) {
        this.path = path;
        try {
            file = new FileInputStream(new File(path));
            wb = new XSSFWorkbook(file);
            ws = wb.getSheetAt(sheet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MyExcel(String path) {
        this.path = path;
        try {
            file = new FileInputStream(new File(path));
            wb = new XSSFWorkbook(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openWorkSheet(String sheet) {
        ws = wb.getSheet(sheet);
    }

    public int finedInColumn(int columnNum, String value, int startPos, int endPos) {
        Iterator<Row> it = ws.iterator();
        int i = 0;
        while (i < startPos && it.hasNext()) {
            it.next();
            i++;
        }
        while (i <= endPos && it.hasNext()) {
            Row row = it.next();
            if (readCellValue(row, columnNum).trim().toLowerCase()
                    .equals(value.trim().toLowerCase()))
                return i;
            i++;
        }
        return -1;
    }

    public int finedInColumn(int columnNum1, String value1, int columnNum2, String value2,
                             int startPos, int endPos) {
        Iterator<Row> it = ws.iterator();
        int i = 0;
        while (i < startPos && it.hasNext()) {
            it.next();
            i++;
        }
        while (i <= endPos && it.hasNext()) {
            Row row = it.next();
            if (readCellValue(row, columnNum1).trim().toLowerCase()
                    .equals(value1.trim().toLowerCase()) &&
                    readCellValue(row, columnNum2).trim().toLowerCase()
                    .equals(value2.trim().toLowerCase()))
                return i;
            i++;
        }
        return -1;
    }

    public String readCell(int i, int j) {
        Row row = ws.getRow(i);

        return readCellValue(row, j);
    }

    private String readCellValue(Row row, int columnNum) {
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        Cell cell = row.getCell(columnNum);

        if (cell != null) {
            cell.getCellType();
            switch (cell.getCellType()) {
                case FORMULA:
                case NUMERIC:
                    return cell.getNumericCellValue() +"";
                case STRING:
                    return cell.getStringCellValue();
            }
        }
        return "";
    }
}
