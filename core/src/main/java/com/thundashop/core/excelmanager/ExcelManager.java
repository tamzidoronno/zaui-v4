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
        createExcelSheet("sheet", array);
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

    private void createExcelSheet(String sheetName, List<List<String>> array) {

        XSSFFont sfont = workbook.createFont();
        sfont.setFontHeightInPoints((short) 12);
        sfont.setBold(false);
        
        XSSFCellStyle standardFontStyle = workbook.createCellStyle();
        standardFontStyle.setFont(sfont);

        XSSFSheet sheet = workbook.createSheet(sheetName);
        CellStyle numericStyle = workbook.createCellStyle();
        numericStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#.##"));
        numericStyle.setFont(sfont);
        
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        XSSFCellStyle boldFontStyle = workbook.createCellStyle();
        boldFontStyle.setFont(font);

        
        for(Integer i = 0; i < array.size(); i++) {
            List<String> fields = array.get(i);
            int j = 0;
            Row row = sheet.createRow(i);
            
            for(String field : fields) {
                Cell cell = row.createCell(j);
                cell.setCellValue(field);
                if(i == 0) {
                    cell.setCellStyle(boldFontStyle);
                } else {
                    if(isNumeric(field)) {
                        cell.setCellType(CellType.STRING);
                        cell.setCellValue(new Double(field));
                        cell.setCellStyle(numericStyle);
                    } else if(isDouble(field)) {
                        cell.setCellType(CellType.STRING);
                        cell.setCellValue(new Double(field));
                        cell.setCellStyle(numericStyle);
                    } else {
                        cell.setCellStyle(standardFontStyle);
                    }
                }
                j++;
            }
        }   
    }

    @Override
    public void startExcelSheet() {
        workbook = new XSSFWorkbook();
    }

    @Override
    public void prepareExcelSheet(String name, List<List<String>> array) {
        createExcelSheet(name, array);
    }

    @Override
    public String getPreparedExcelSheet() {
        return getBase64Encoded();
    }
    
}
