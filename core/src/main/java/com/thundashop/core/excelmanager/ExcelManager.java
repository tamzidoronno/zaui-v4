/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.excelmanager;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.pdf.InvoiceManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */

@Component
@GetShopSession
public class ExcelManager extends ManagerBase implements IExcelManager {

    private XSSFWorkbook workbook;
    
    @Override
    public String getBase64Excel(List<List<String>> array) {
        workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet");
        for(Integer i = 0; i < array.size(); i++) {
            List<String> fields = array.get(i);
            int j = 0;
            Row row = sheet.createRow(i);
            for(String field : fields) {
                Cell cell = row.createCell(j);
                cell.setCellValue(field);
                if(isNumeric(field)) {
                    cell.setCellType(CellType.NUMERIC);
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#.#"));
                    cell.setCellValue(new Double(field));
                } else if(isDouble(field)) {
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#.#"));
                    cell.setCellValue(new Double(field));
                    cell.setCellStyle(cellStyle);
                }
                if(i == 0) {
                    setFontSize(cell, 12, true);
                } else {
                    setFontSize(cell, 12, false);
                }
                j++;
            }
        }
        return getBase64Encoded();
    }
    
    private void writeFile() {
        try {
            FileOutputStream out = new FileOutputStream(new File("/tmp/tmp_excelfile_"+storeId+".xlsx"));
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public String getBase64Encoded() {
        writeFile();
        
        File file = new File("/tmp/tmp_excelfile_"+storeId+".xlsx");
        
        byte[] bytes;
        try {
            bytes = InvoiceManager.loadFile(file);
            byte[] encoded = Base64.encodeBase64(bytes);
            String encodedString = new String(encoded);
            file.delete();
            return encodedString;
        } catch (IOException ex) {
            throw new ErrorException(1033);
        }
        
    }
    
    private void setFontSize(Cell cell, int fontSize, boolean bold) {
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(bold);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        cell.setCellStyle(style);
    }

    private boolean isNumeric(String field) {
        try {
            if(!field.contains(".")) {
                Integer res = new Integer(field);
                return res.toString().equals(field);
            }
        } catch(Exception e) {
        }
        return false;
    }

    private boolean isDouble(String field) {
        try {
            Double res = new Double(field);
            return true;
        } catch(Exception e) {
        }
        return false;
    }
    
    
}
