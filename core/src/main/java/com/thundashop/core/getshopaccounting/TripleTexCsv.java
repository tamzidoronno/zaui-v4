/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class TripleTexCsv extends AccountingSystemBase {
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private TripleTextExcelMap excelHeaders = new TripleTextExcelMap();
    private int rowNumber = 0;
    
    @Override
    public String getSystemName() {
        return "TripleTex - csv";
    }

    @Override
    public List<SavedOrderFile> createFiles(List<Order> orders, Date start, Date end) {
        Map<String, List<Order>> allOrders = groupOrders(orders);
        ArrayList<SavedOrderFile> retFiles = new ArrayList();
        
        boolean allOk = validateProductIds(orders);
        
        if (!allOk) {
            return new ArrayList();
        }
        
        rowNumber = 0;

        for (String subType : allOrders.keySet()) {
            Workbook wb = new HSSFWorkbook();
            Sheet sheet = wb.createSheet();
            createHeaders(sheet);
            
            SavedOrderFile file = new SavedOrderFile();

            file.orders = new ArrayList();
            file.subtype = subType;

            List<Order> iOrders = allOrders.get(subType);
            for (Order order : iOrders) {
                file.orders.add(order.id);
                addOrder(sheet, order);
            }

            file.result = getCsv(wb);
            retFiles.add(file);
        }
        
        return retFiles;
    }

    @Override
    public SystemType getSystemType() {
        return SystemType.TRIPLETEX_CSV;
    }

    private void createHeaders(Sheet sheet) {
        Row row = sheet.createRow(getNextRowNumberAndIncrement());
        
        int i = 0;
        for(String key : excelHeaders) {
            Cell cell = row.createCell(i);
            cell.setCellValue(key);
            i++;
        }
    }

    private void writeFileToTemp(Workbook wb, String filename) {
        try {    
            FileOutputStream fileOut = new FileOutputStream(filename);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addOrder(Sheet sheet, Order order) {
        
        List<CartItem> cartItems = order.cart.getItems();
        
        int i = 0;
        for (CartItem item : cartItems) {
            boolean firstInRow = i == 0;
            addCartItem(sheet, item, order, firstInRow);
            i++;
        }
    }

    private int getNextRowNumberAndIncrement() {
        int toReturn = rowNumber;
        rowNumber++;
        return toReturn;
    }

    private int getCellNumber(String text) {
        return excelHeaders.indexOf(text);
    }

    private void addCartItem(Sheet sheet, CartItem item, Order order, boolean firstRowInOrder) {
        Product product = productManager.getProduct(item.getProduct().id);
        User user = userManager.getUserById(order.userId);
        
        Row row = sheet.createRow(getNextRowNumberAndIncrement());
        row.createCell(getCellNumber("ORDER NO")).setCellValue(order.incrementOrderId);
        row.createCell(getCellNumber("COMMENTS")).setCellValue(order.invoiceNote);
        
        if (firstRowInOrder) {
            row.createCell(getCellNumber("ORDER DATE")).setCellValue(generateOrderDate(order));
            row.createCell(getCellNumber("CUSTOMER NO")).setCellValue(getAccountingAccountId(order.userId));
            row.createCell(getCellNumber("DELIVERY DATE")).setCellValue(getAccountingPostingDate(order));
            
            if(user.address != null) {
                if(user.address.address != null) 
                    row.createCell(getCellNumber("POSTAL ADDR - LINE 1")).setCellValue(user.address.address);
  
                if(user.address.address != null) 
                    row.createCell(getCellNumber("POSTAL ADDR - POSTAL NO")).setCellValue(user.address.postCode);
                
                if(user.address.address != null)               
                row.createCell(getCellNumber("POSTAL ADDR - CITY")).setCellValue(user.address.city);
                row.createCell(getCellNumber("POSTAL ADDR - COUNTRY")).setCellValue(user.address.countrycode);
            }
        
            // Customer details
            row.createCell(getCellNumber("CUSTOMER NAME")).setCellValue(nullAndCsvCheck(user.fullName));
            row.createCell(getCellNumber("CUSTOMER EMAIL")).setCellValue(nullAndCsvCheck(user.emailAddress));
            row.createCell(getCellNumber("CUSTOMER PHONE")).setCellValue(nullAndCsvCheck(user.cellPhone));
        }
        
        row.createCell(getCellNumber("ORDER LINE - DESCRIPTION")).setCellValue(createLineText(item));
        // Its important that this unit price is not rounded, example an SMS costs 0.39 x 2000 smses. Rounding this will be very wrong.
        row.createCell(getCellNumber("ORDER LINE - UNIT PRICE")).setCellValue(item.getProduct().priceExTaxes);
        row.createCell(getCellNumber("ORDER LINE - COUNT")).setCellValue(item.getCount());
        row.createCell(getCellNumber("ORDER LINE - VAT CODE")).setCellValue(product.sku);
        
        // Product Name
        row.createCell(getCellNumber("ORDER LINE - PROD NO")).setCellValue(product.incrementalProductId);
        row.createCell(getCellNumber("ORDER LINE - PROD NAME")).setCellValue(nullAndCsvCheck(product.name));
    }

    private String generateOrderDate(Order order) {
        return formatter.format(order.transferToAccountingDate);
    }

    private String getDeliveryDate(Order order) {
        return formatter.format(order.transferToAccountingDate);
    }

    private boolean validateProductIds(List<Order> orders) {
        boolean allOk = true;
        for (Order order : orders) {
            for(CartItem item : order.cart.getItems()) {
                Product product = productManager.getProduct(item.getProduct().id);
                if (product.incrementalProductId == null || product.incrementalProductId < 1) {
                    addToLog("The product: " + product.name + " does not have an product id that is used for matching product in tripletex");
                    allOk = false;
                }
            }
        }
        
        return allOk;
    }

    private double round(double priceExTaxes) {
        return Math.round(priceExTaxes);
    }

    private String getBase64Encoded(Workbook wb) {
        String tmpName = "/tmp/"+UUID.randomUUID().toString()+".xls";
        writeFileToTemp(wb, tmpName);
        
        File file = new File(tmpName);
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

    @Override
    public void handleDirectTransfer(String orderId) {
        throw new ErrorException(1049);
    }

    private List<String> getCsv(Workbook wb) {
        HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(0);

        List<String> result = new ArrayList();
        int deliveryDateField = getCellNumber("DELIVERY DATE");
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);

            StringBuilder strBuff = new StringBuilder();
            for (short j = 0; j <= row.getLastCellNum(); j++) {
                HSSFCell cell = row.getCell(j);
                String cellField = "";
                if(cell != null) {
                    if(cell.getCellType()==XSSFCell.CELL_TYPE_NUMERIC) {
                        if(j == deliveryDateField) {
                            cellField = formatter.format(cell.getDateCellValue())+ "";
                        } else {
                            cellField = cell.getNumericCellValue() + "";
                        }
                    } else {
                        cellField = cell.getStringCellValue()+ "";
                    }
                }
                cellField = cellField.replaceAll(",", "");
                cellField += ",";
                strBuff.append(cellField);
            }
            result.add(strBuff.toString() + "\n");

        }
        return result;
    }
    
    @Override
    boolean isUsingProductTaxCodes() {
        return true;
    }

    @Override
    boolean isPrimitive() {
        return false;
    }
}
