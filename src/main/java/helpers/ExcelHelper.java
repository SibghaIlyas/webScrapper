package helpers;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelHelper {
    public ExcelHelper() throws IOException {
        //nothing here
    }
    FileInputStream fis = new FileInputStream("/Users/sibghailyas/git/webScrapper/src/main/resources/Copy of Ahsan Hamza - Product Sheet.xlsx");
    XSSFWorkbook workbook = new XSSFWorkbook(fis);

    public String getValueFromCell(String sheetName, int rowNo, int cellNo){
        XSSFSheet sheet = workbook.getSheet(sheetName);
        XSSFRow row = sheet.getRow(rowNo);
        XSSFCell cell = row.getCell(cellNo);
        String value = cell.getStringCellValue();
        return value;
    }

    FileOutputStream fos = new FileOutputStream("/Users/sibghailyas/git/webScrapper/src/main/resources/Copy of Ahsan Hamza - Product Sheet.xlsx");

    public void setValueOfCell(String sheetName, int rowNo, int cellNo, String value) throws IOException {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        XSSFRow row = sheet.getRow(rowNo);
        XSSFCell cell = row.getCell(cellNo);
        cell.setCellValue(value);
    }

    public void saveFile() throws IOException {
        workbook.write(fos);
        fos.close();
    }

    public int getRowsCount(String sheetName) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        int temp = 1;
        while(true) {
            XSSFRow row = sheet.getRow(temp);
            XSSFCell cell = row.getCell(1);
            String value = cell.getStringCellValue();
            if(value.isEmpty()) {
                return cell.getRowIndex();
            }
            else temp++;
        }
    }


}
