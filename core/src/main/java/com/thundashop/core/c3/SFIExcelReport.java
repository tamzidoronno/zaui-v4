/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.pdf.InvoiceManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ktonder
 */
public class SFIExcelReport {
    private XSSFSheet sheet;
    private int rownum = 0;
    private XSSFWorkbook workbook;
    
    private SFIExcelReportData data;
    
    private double overTotal = 0;
    private double overTotalNfr = 0;
    private double overTotalInkind = 0;
    private final List<SFIExcelReportData> datas;
    private final HashMap<String, WorkPackage> wps;
    private final Date end;
    
    private SFIExcelReportData wp11 ;
    private final boolean segreate;
    
    public SFIExcelReport(List<SFIExcelReportData> datas, HashMap<String, WorkPackage> wps, boolean segreate, Date end, SFIExcelReportData wp11) {
        workbook = new XSSFWorkbook();
        this.wp11 = wp11;
        this.datas = datas;
        
        if (isWp11Included()) {
            this.wp11 = getWp11Data();
        }
        
        this.wps = wps;
        this.end = end;
        this.segreate = segreate;
        
        for (SFIExcelReportData data : datas) {
            doSfiReport(data);
        }
        
        if (segreate && shouldAddWp11()) {
            doSfiReport(wp11);
        }
    }

    private void doSfiReport(SFIExcelReportData data1) {
        this.data = data1;
        clearCounters();
        createSheet();
        addHeader();
        addNameOfPartner();
        addNavnSfi();
        addSFIProjectNumber();
        addWps();
        addPeriode();
        addPost1Header();
        addPost1Content(data1.wpId);
        addPost3Header();
        addPost3Content(data1.wpId);
        addPost4Header();
        addPost4Content(data1.wpId);
        addTotalRow();
        addDateLine();
        addAnsvarlig();
        addCommentField();
        setColumnSizes();
    }

    private void clearCounters() {
        rownum = 0;
        overTotal = 0;
        overTotalNfr = 0;
        overTotalInkind = 0;
    }
    
    public static void main(String[] args) {
        
    }

    private void createSheet() {
        String sheetName = data.delprosjekter.replaceAll(":", "");
        sheet = workbook.createSheet(sheetName);
        
    }

    private void addHeader() {
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        
        Row row = sheet.createRow(rownum++);
        Cell cell = row.createCell(0);
        cell.setCellValue("SFI - Rapportering av prosjektkostnader");
        setFontSize(cell, 15, true);
    }

    private void setFontSize(Cell cell, int fontSize, boolean bold) {
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(bold);
        
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        
        
        cell.setCellStyle(style);
    }
    
    private void setBorderLeftAndRight(Cell cell) {
        CellStyle style = cell.getCellStyle();
        
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    }
    
    private void setBorderLeft(Cell cell) {
        CellStyle style = cell.getCellStyle();
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    }
    
    private void setBorderTopAndBottom(Cell cell) {
        CellStyle style = cell.getCellStyle();
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    }
    
    private void setBorder(Cell cell) {
        CellStyle style = cell.getCellStyle();
        
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    }

    public String getBase64Encoded() {
        writeFile();
        
        File file = new File("/tmp/tmp_excel_sfi_report.xlsx");
        
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
    
    public void writeFile() {
        try {
            FileOutputStream out = new FileOutputStream(new File("/tmp/tmp_excel_sfi_report.xlsx"));
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setColumnSizes() {
        sheet.setColumnWidth(0, 10000);
        sheet.setColumnWidth(1, 2000);
        sheet.setColumnWidth(2, 2000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 3000);
    }

    private void addNameOfPartner() {
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Navn på partner");
        
        XSSFCell cell2 = row.createCell(1);
        cell2.setCellValue(data.nameOfPartner);
        
        setFontSize(cell1, 12, true);
        setFontSize(cell2, 12, true);
        
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 5));
        addLeftBorderToRegion(1, 1, 1, 5);
    }

    private void addNavnSfi() {
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Navn på SFI:");
        
        XSSFCell cell2 = row.createCell(1);
        cell2.setCellValue("Center for Connected Care");
        
        setFontSize(cell1, 12, true);
        setFontSize(cell2, 12, true);
        
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 5));
        addLeftBorderToRegion(2, 2, 1, 5);
    }

    private void addSFIProjectNumber() {
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Prosjektnr SFI:");
        
        XSSFCell cell2 = row.createCell(1);
        cell2.setCellValue("32242");
        
        setFontSize(cell1, 12, true);
        setFontSize(cell2, 12, true);
        
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 5));
        addLeftBorderToRegion(3, 3, 1, 5);
    }

    private void addWps() {
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Delprosjekt:");
        
        XSSFCell cell2 = row.createCell(1);
        cell2.setCellValue(data.delprosjekter);
        
        setFontSize(cell1, 12, true);
        setFontSize(cell2, 12, true);
        
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 5));
        addLeftBorderToRegion(4, 4, 1, 5);
    }
    
    private void addPeriode() {
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Periode:");
        
        XSSFCell cell2 = row.createCell(1);
        cell2.setCellValue(data.periode);
        
        setFontSize(cell1, 12, true);
        setFontSize(cell2, 12, true);
        
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 1, 5));
        addLeftBorderToRegion(5, 5, 1, 5);
    }

    private void addPost1Header() {
        rownum++;
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Personal og indirekte kostnader (1.1)");
        
        setFontSize(cell1, 10, true);
        
        addLeftBorderToRegion(3, 3, 1, 5);
        
        sheet.addMergedRegion(new CellRangeAddress((rownum-1), (rownum-1), 0, 5));
        
        addLeftBorderToRegion((rownum-1), (rownum-1), 0, 5);
        XSSFRow rowh = sheet.createRow(rownum++);
        XSSFCell cell_1 = rowh.createCell(0);
        XSSFCell cell_2 = rowh.createCell(1);
        XSSFCell cell_3 = rowh.createCell(2);
        XSSFCell cell_4 = rowh.createCell(3);
        XSSFCell cell_5 = rowh.createCell(4);
        XSSFCell cell_6 = rowh.createCell(5);
        
        cell_1.setCellValue("Navn");
        cell_2.setCellValue("Timer");
        cell_3.setCellValue("Timesats");
        cell_4.setCellValue("NFR-finansiering");
        cell_5.setCellValue("In-kind");
        cell_6.setCellValue("Totalt");
        
        setFontSize(cell_1, 10, true);
        setFontSize(cell_2, 10, true);
        setFontSize(cell_3, 10, true);
        setFontSize(cell_4, 10, true);
        setFontSize(cell_5, 10, true);
        setFontSize(cell_6, 10, true);
        
        setBorder(cell_1);
        setBorder(cell_2);
        setBorder(cell_3);
        setBorder(cell_4);
        setBorder(cell_5);
        setBorder(cell_6);
    }

    private double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
    
    private void addPost1Content(String wpId) {
        double sumtimer = 0;
        double sumtotal = 0;
        double sumnfr = 0;
        double suminkind = 0;
        
        for (SFIExcelReportDataPost11 post11 : data.post11) {
            XSSFRow rowh = sheet.createRow(rownum++);
            XSSFCell cell_1 = rowh.createCell(0);
            XSSFCell cell_2 = rowh.createCell(1);
            XSSFCell cell_3 = rowh.createCell(2);
            XSSFCell cell_4 = rowh.createCell(3);
            XSSFCell cell_5 = rowh.createCell(4);
            XSSFCell cell_6 = rowh.createCell(5);

            cell_1.setCellValue(post11.navn);
            if (post11.timer > 0) {
                cell_2.setCellValue(round(post11.timer, 1));
            } else {
                cell_2.setCellValue(" ");
            }
            
            if (post11.timer > 0) {
                cell_3.setCellValue(post11.timesats);
            } else {
                cell_3.setCellValue(" ");
            }
            
            if (post11.nfr > 0) {
                cell_4.setCellValue(post11.nfr);
            } else {
                cell_4.setCellValue(" ");
            }
            
            if (post11.inkind > 0) {
                cell_5.setCellValue(post11.inkind);
            } else {
                cell_5.setCellValue(" ");
            }
            
            cell_6.setCellValue(post11.totalt);

            setFontSize(cell_1, 10, false);
            setFontSize(cell_2, 10, false);
            setFontSize(cell_3, 10, false);
            setFontSize(cell_4, 10, false);
            setFontSize(cell_5, 10, false);
            setFontSize(cell_6, 10, false);
            
            setBorderLeftAndRight(cell_2);
            setBorderLeftAndRight(cell_3);
            setBorderLeftAndRight(cell_4);
            setBorderLeftAndRight(cell_5);
            setBorderLeftAndRight(cell_6);
            
            sumtimer += round(post11.timer,1);
            sumtotal += post11.totalt;
            sumnfr += post11.nfr;
            suminkind += post11.inkind;
        }
        
        if (segreate && shouldTransferToOtherWp11(wpId)) {
            double post11RemovalTotalNfr = 0;
            double post11RemovalTotalInkind = suminkind * 0.01;
            double post11RemovalTotal = post11RemovalTotalInkind;

            
            sumtotal -= (int)post11RemovalTotal;
            sumnfr -= (int)post11RemovalTotalNfr;
            suminkind -= (int)post11RemovalTotalInkind;
            
            addRemovalLine((int)post11RemovalTotalNfr, (int)post11RemovalTotalInkind, (int)post11RemovalTotal, wp11.post11, wpId, 0);
        }
        
        // Empty row
        XSSFRow erowh = sheet.createRow(rownum++);
        XSSFCell ecell_1 = erowh.createCell(0); ecell_1.setCellValue(" "); setFontSize(ecell_1, 10, false); setBorderLeftAndRight(ecell_1);
        XSSFCell ecell_2 = erowh.createCell(1); ecell_2.setCellValue(" "); setFontSize(ecell_2, 10, false); setBorderLeftAndRight(ecell_2);
        XSSFCell ecell_3 = erowh.createCell(2); ecell_3.setCellValue(" "); setFontSize(ecell_3, 10, false); setBorderLeftAndRight(ecell_3);
        XSSFCell ecell_4 = erowh.createCell(3); ecell_4.setCellValue(" "); setFontSize(ecell_4, 10, false); setBorderLeftAndRight(ecell_4);
        XSSFCell ecell_5 = erowh.createCell(4); ecell_5.setCellValue(" "); setFontSize(ecell_5, 10, false); setBorderLeftAndRight(ecell_5);
        XSSFCell ecell_6 = erowh.createCell(5); ecell_6.setCellValue(" "); setFontSize(ecell_6, 10, false); setBorderLeftAndRight(ecell_6);
        
        
        
        XSSFRow rowh = sheet.createRow(rownum++);
        XSSFCell cell_1 = rowh.createCell(0);
        XSSFCell cell_2 = rowh.createCell(1);
        XSSFCell cell_3 = rowh.createCell(2);
        XSSFCell cell_4 = rowh.createCell(3);
        XSSFCell cell_5 = rowh.createCell(4);
        XSSFCell cell_6 = rowh.createCell(5);
        
        cell_1.setCellValue("Sum");
        cell_2.setCellValue(sumtimer);
        cell_4.setCellValue(sumnfr);
        cell_5.setCellValue(suminkind);
        cell_6.setCellValue(sumtotal);
        
        setFontSize(cell_1, 10, true);
        setFontSize(cell_2, 10, false);
        setFontSize(cell_3, 10, false);
        setFontSize(cell_4, 10, false);
        setFontSize(cell_5, 10, false);
        setFontSize(cell_6, 10, false);
            
        setBorder(cell_1);
        setBorder(cell_2);
        setBorder(cell_3);
        setBorder(cell_4);
        setBorder(cell_5);
        setBorder(cell_6);
        
        overTotal += sumtotal;
        overTotalInkind += suminkind;
        overTotalNfr += sumnfr;
        
    }

    private void addPost3Header() {
        rownum++;
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Vitenskapelig utstyr (1.3)");
        
        setFontSize(cell1, 10, true);
        
        sheet.addMergedRegion(new CellRangeAddress((rownum-1), (rownum-1), 0, 5));
       
        addLeftBorderToRegion((rownum-1), (rownum-1), 0, 5);
    }

    private void addLeftBorderToRegion(int a, int b, int c, int d) {
        RegionUtil.setBorderTop(CellStyle.BORDER_THIN, new CellRangeAddress(a,b,c,d), sheet);
        RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, new CellRangeAddress(a,b,c,d), sheet);
        RegionUtil.setBorderRight(CellStyle.BORDER_THIN, new CellRangeAddress(a,b,c,d), sheet);
        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, new CellRangeAddress(a,b,c,d), sheet);

//        XSSFCell cell7 = row.createCell(cellNumber);
//        cell7.setCellValue(" ");
//        setFontSize(cell7, 10, true);
//        setBorderLeft(cell7);
    }

    private void addPost3Content(String wpId) {
        double sumtotal = 0;
        double sumnfr = 0;
        double suminkind = 0;
        
        for (SFIExcelReportDataPost13 post13 : data.post13) {
            XSSFRow rowh = sheet.createRow(rownum++);
            XSSFCell cell_1 = rowh.createCell(0);
            XSSFCell cell_2 = rowh.createCell(1);
            XSSFCell cell_3 = rowh.createCell(2);
            XSSFCell cell_4 = rowh.createCell(3);
            XSSFCell cell_5 = rowh.createCell(4);
            XSSFCell cell_6 = rowh.createCell(5);

            cell_1.setCellValue(post13.navn);
            cell_2.setCellValue(" ");
            cell_3.setCellValue(" ");
            cell_4.setCellValue(post13.nfr);
            cell_5.setCellValue(post13.inkind);
            cell_6.setCellValue(post13.totalt);

            setFontSize(cell_1, 10, false);
            setFontSize(cell_2, 10, false);
            setFontSize(cell_3, 10, false);
            setFontSize(cell_4, 10, false);
            setFontSize(cell_5, 10, false);
            setFontSize(cell_6, 10, false);
            
            setBorderLeft(cell_2);
            setBorderLeftAndRight(cell_4);
            setBorderLeftAndRight(cell_5);
            setBorderLeftAndRight(cell_6);
            
            sumtotal += post13.totalt;
            sumnfr += post13.nfr;
            suminkind += post13.inkind;
        }
        
        if (segreate && shouldTransferToOtherWp11(wpId)) {
            double post11RemovalTotalNfr = 0;
            double post11RemovalTotalInkind = suminkind * 0.01;
            double post11RemovalTotal = post11RemovalTotalInkind;
                    
            sumtotal -= (int)post11RemovalTotal;
            sumnfr -= (int)post11RemovalTotalNfr;
            suminkind -= (int)post11RemovalTotalInkind;
            
            addRemovalLine((int)post11RemovalTotalNfr, (int)post11RemovalTotalInkind, (int)post11RemovalTotal, wp11.post13 , wpId, 1);
        }
        
        // Empty row
        XSSFRow erowh = sheet.createRow(rownum++);
        XSSFCell ecell_1 = erowh.createCell(0); ecell_1.setCellValue(" "); setFontSize(ecell_1, 10, false); setBorderLeftAndRight(ecell_1);
        XSSFCell ecell_2 = erowh.createCell(1); ecell_2.setCellValue(" "); setFontSize(ecell_2, 10, false); setBorderLeft(ecell_2);
        XSSFCell ecell_3 = erowh.createCell(2); ecell_3.setCellValue(" "); setFontSize(ecell_3, 10, false);
        XSSFCell ecell_4 = erowh.createCell(3); ecell_4.setCellValue(" "); setFontSize(ecell_4, 10, false); setBorderLeftAndRight(ecell_4);
        XSSFCell ecell_5 = erowh.createCell(4); ecell_5.setCellValue(" "); setFontSize(ecell_5, 10, false); setBorderLeftAndRight(ecell_5);
        XSSFCell ecell_6 = erowh.createCell(5); ecell_6.setCellValue(" "); setFontSize(ecell_6, 10, false); setBorderLeftAndRight(ecell_6);
        
        
        XSSFRow rowh = sheet.createRow(rownum++);
        XSSFCell cell_1 = rowh.createCell(0);
        XSSFCell cell_2 = rowh.createCell(1);
        XSSFCell cell_3 = rowh.createCell(2);
        XSSFCell cell_4 = rowh.createCell(3);
        XSSFCell cell_5 = rowh.createCell(4);
        XSSFCell cell_6 = rowh.createCell(5);
        
        cell_1.setCellValue("Sum");
        cell_2.setCellValue(" ");
        cell_4.setCellValue(sumtotal);
        cell_5.setCellValue(sumnfr);
        cell_6.setCellValue(suminkind);
        
        setFontSize(cell_1, 10, true);
        setFontSize(cell_2, 10, false);
        setFontSize(cell_3, 10, false);
        setFontSize(cell_4, 10, false);
        setFontSize(cell_5, 10, false);
        setFontSize(cell_6, 10, false);
            
        setBorder(cell_1);
        setBorderTopAndBottom(cell_2);        
        setBorderTopAndBottom(cell_3);        
        setBorder(cell_4);
        setBorder(cell_5);
        setBorder(cell_6);
        
        overTotal += sumtotal;
        overTotalInkind += suminkind;
        overTotalNfr += sumnfr;
    }

    private void addPost4Header() {
        rownum++;
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Andre driftskostnader* (1.4)");
        
        setFontSize(cell1, 10, true);
        
        sheet.addMergedRegion(new CellRangeAddress((rownum-1), (rownum-1), 0, 5));
        
        addLeftBorderToRegion((rownum-1), (rownum-1), 0, 5);
    }

    private void addPost4Content(String wpId) {
        double sumtotal = 0;
        double sumnfr = 0;
        double suminkind = 0;
        
        for (SFIExcelReportDataPost14 post14 : data.post14) {
            XSSFRow rowh = sheet.createRow(rownum++);
            XSSFCell cell_1 = rowh.createCell(0);
            XSSFCell cell_2 = rowh.createCell(1);
            XSSFCell cell_3 = rowh.createCell(2);
            XSSFCell cell_4 = rowh.createCell(3);
            XSSFCell cell_5 = rowh.createCell(4);
            XSSFCell cell_6 = rowh.createCell(5);

            cell_1.setCellValue(post14.navn);
            cell_2.setCellValue(" ");
            cell_3.setCellValue(" ");
            cell_4.setCellValue(post14.nfr);
            cell_5.setCellValue(post14.inkind);
            cell_6.setCellValue(post14.totalt);

            setFontSize(cell_1, 10, false);
            setFontSize(cell_2, 10, false);
            setFontSize(cell_3, 10, false);
            setFontSize(cell_4, 10, false);
            setFontSize(cell_5, 10, false);
            setFontSize(cell_6, 10, false);
            
            setBorderLeft(cell_2);
            setBorderLeftAndRight(cell_4);
            setBorderLeftAndRight(cell_5);
            setBorderLeftAndRight(cell_6);
            
            sumtotal += post14.totalt;
            sumnfr += post14.nfr;
            suminkind += post14.inkind;
        }
        
        if (segreate && shouldTransferToOtherWp11(wpId)) {
            
            double post11RemovalTotalNfr = 0;
            double post11RemovalTotalInkind = suminkind * 0.01;
            double post11RemovalTotal = post11RemovalTotalInkind;
                    
            sumtotal -= (int)post11RemovalTotal;
            sumnfr -= (int)post11RemovalTotalNfr;
            suminkind -= (int)post11RemovalTotalInkind;
            
            addRemovalLine((int)post11RemovalTotalNfr, (int)post11RemovalTotalInkind, (int)post11RemovalTotal, wp11.post14, wpId, 2);
        }
        
        // Empty row
        XSSFRow erowh = sheet.createRow(rownum++);
        XSSFCell ecell_1 = erowh.createCell(0); ecell_1.setCellValue(" "); setFontSize(ecell_1, 10, false); setBorderLeftAndRight(ecell_1);
        XSSFCell ecell_2 = erowh.createCell(1); ecell_2.setCellValue(" "); setFontSize(ecell_2, 10, false); setBorderLeft(ecell_2);
        XSSFCell ecell_3 = erowh.createCell(2); ecell_3.setCellValue(" "); setFontSize(ecell_3, 10, false);
        XSSFCell ecell_4 = erowh.createCell(3); ecell_4.setCellValue(" "); setFontSize(ecell_4, 10, false); setBorderLeftAndRight(ecell_4);
        XSSFCell ecell_5 = erowh.createCell(4); ecell_5.setCellValue(" "); setFontSize(ecell_5, 10, false); setBorderLeftAndRight(ecell_5);
        XSSFCell ecell_6 = erowh.createCell(5); ecell_6.setCellValue(" "); setFontSize(ecell_6, 10, false); setBorderLeftAndRight(ecell_6);
        
        
        XSSFRow rowh = sheet.createRow(rownum++);
        XSSFCell cell_1 = rowh.createCell(0);
        XSSFCell cell_2 = rowh.createCell(1);
        XSSFCell cell_3 = rowh.createCell(2);
        XSSFCell cell_4 = rowh.createCell(3);
        XSSFCell cell_5 = rowh.createCell(4);
        XSSFCell cell_6 = rowh.createCell(5);
        
        cell_1.setCellValue("Sum");
        cell_2.setCellValue(" ");
        cell_4.setCellValue(sumnfr);
        cell_5.setCellValue(suminkind);
        cell_6.setCellValue(sumtotal);
        
        setFontSize(cell_1, 10, true);
        setFontSize(cell_2, 10, false);
        setFontSize(cell_3, 10, false);
        setFontSize(cell_4, 10, false);
        setFontSize(cell_5, 10, false);
        setFontSize(cell_6, 10, false);
            
        setBorder(cell_1);
        setBorderTopAndBottom(cell_2);        
        setBorderTopAndBottom(cell_3);        
        setBorder(cell_4);
        setBorder(cell_5);
        setBorder(cell_6);
        
        overTotal += sumtotal;
        overTotalInkind += suminkind;
        overTotalNfr += sumnfr;
    }

    private void addAnsvarlig() {
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Ansvarlig for rapport:");
        
        XSSFCell cell2 = row.createCell(1);
        cell2.setCellValue(data.ansvarlig);
        
        setFontSize(cell1, 10, true);
        setFontSize(cell2, 10, true);
        
        sheet.addMergedRegion(new CellRangeAddress((rownum-1), (rownum-1), 1, 5));
        addLeftBorderToRegion((rownum-1), (rownum-1), 1, 5);
        
        
        row = sheet.createRow(rownum++);
        cell1 = row.createCell(0);
        row.setHeight((short)700);
        sheet.addMergedRegion(new CellRangeAddress((rownum-1), (rownum-1), 1, 5));
        addLeftBorderToRegion((rownum-1), (rownum-1), 1, 5);
        
    }
    
    private void addDateLine() {
        rownum++;
        rownum++;
        
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Dato");
        
        XSSFCell cell2 = row.createCell(1);
        cell2.setCellValue(data.date);
        
        setFontSize(cell1, 10, true);
        setFontSize(cell2, 10, true);
        
        sheet.addMergedRegion(new CellRangeAddress((rownum-1), (rownum-1), 1, 5));
        addLeftBorderToRegion((rownum-1), (rownum-1), 1, 5);
    }

    private void addCommentField() {
        XSSFRow row = sheet.createRow(rownum++);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("Utfyllende kommentarer:");
        setFontSize(cell1, 10, true);
        
        cell1.getCellStyle().setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
        
        sheet.addMergedRegion(new CellRangeAddress((rownum-1), (rownum+2), 0, 5));
        
        XSSFRow lastRow = sheet.createRow(rownum+2);
        addLeftBorderToRegion((rownum-1), (rownum+2), 0, 5);
        
        rownum = rownum + 4;
        sheet.addMergedRegion(new CellRangeAddress((rownum-1), (rownum-1), 0, 5));
        
        XSSFRow row2 = sheet.createRow(rownum-1);
        XSSFCell cell2 = row2.createCell(0);
        
        cell2.setCellValue("*Andre driftskostnader: Direkte prosjektrelaterte kostnader som ikke inngår i timesats: reiseutgifter, evt. utstyrsleie, materialer og komponenter knyttet til eksperimentelt arbeid");
        setFontSize(cell2, 10, false);
        
        cell2.getCellStyle().setWrapText(true);
        row2.setHeight((short)600);
        
        cell2.getCellStyle().setWrapText(true);
        cell2.getCellStyle().setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
        
        addLeftBorderToRegion((rownum-1), (rownum-1), 0, 5);
    }

    private void addTotalRow() {
        rownum++;
        
        XSSFRow rowh = sheet.createRow(rownum++);
        XSSFCell cell_1 = rowh.createCell(0);
        XSSFCell cell_2 = rowh.createCell(1);
        XSSFCell cell_3 = rowh.createCell(2);
        XSSFCell cell_4 = rowh.createCell(3);
        XSSFCell cell_5 = rowh.createCell(4);
        XSSFCell cell_6 = rowh.createCell(5);

        cell_1.setCellValue("Sum totalt");
        cell_2.setCellValue(" ");
        cell_3.setCellValue(" ");
        cell_4.setCellValue(overTotalNfr);
        cell_5.setCellValue(overTotalInkind);
        cell_6.setCellValue(overTotal);

        setFontSize(cell_1, 10, true);
        setFontSize(cell_2, 10, false);
        setFontSize(cell_3, 10, false);
        setFontSize(cell_4, 10, false);
        setFontSize(cell_5, 10, false);
        setFontSize(cell_6, 10, false);

        setBorder(cell_4);
        setBorder(cell_5);
        setBorder(cell_6);

        
        
    }

    private boolean shouldTransferToOtherWp11(String wpId) {
        if (wps.get(wpId) == null)
            return false;
        
        return wps.get(wpId).shouldRemoveOnePercent(end);
    }

    private void addRemovalLine(int nfr, int inkind, int total, List postList, String wpId, int type) { 
        if (nfr == 0 && inkind == 0)
            return;
        
        WorkPackage packageToUse = wps.get(wpId);
        
        XSSFRow rowh = sheet.createRow(rownum++);
        XSSFCell cell_1 = rowh.createCell(0);
        XSSFCell cell_2 = rowh.createCell(1);
        XSSFCell cell_3 = rowh.createCell(2);
        XSSFCell cell_4 = rowh.createCell(3);
        XSSFCell cell_5 = rowh.createCell(4);
        XSSFCell cell_6 = rowh.createCell(5);
        
        
        cell_1.setCellValue("Overføring til WP11");
        cell_4.setCellValue(nfr * -1);
        cell_5.setCellValue(inkind * -1);
        cell_6.setCellValue(total * -1);
        
        setFontSize(cell_1, 10, false);
        setFontSize(cell_2, 10, false);
        setFontSize(cell_3, 10, false);
        setFontSize(cell_4, 10, false);
        setFontSize(cell_5, 10, false);
        setFontSize(cell_6, 10, false);
            
        setBorder(cell_1);
        setBorder(cell_2);
        setBorder(cell_3);
        setBorder(cell_4);
        setBorder(cell_5);
        setBorder(cell_6);
        
        SFIExcelReportDataPost nfrPost = null;
                
        if (type == 0)
            nfrPost = new SFIExcelReportDataPost11();
        
        if (type == 1)
            nfrPost = new SFIExcelReportDataPost13();
        
        if (type == 2)
            nfrPost = new SFIExcelReportDataPost14();
        
        nfrPost.navn = "Overføring fra " + packageToUse.name + "(1%)";
        nfrPost.nfr = nfr;
        nfrPost.inkind = inkind;
        nfrPost.totalt = nfr + inkind;
        postList.add(nfrPost);
    }

    private boolean shouldAddWp11() {
        if (isWp11Included()) {
            return false;
        }
        
        for (SFIExcelReportData data : datas) {
            if (wps.get(data.wpId).shouldRemoveOnePercent(end))
                return true;
        }
        
        return false;
    }

    private boolean isWp11Included() {
        for (SFIExcelReportData data : datas) {
            if (data.wpId != null && data.wpId.equals("de20c1c3-faee-4237-8457-dc9efed16364")) {
                return true;
            }
        }
        return false;
    }

    private SFIExcelReportData getWp11Data() {
        for (SFIExcelReportData data : datas) {
            if (data.wpId.equals("de20c1c3-faee-4237-8457-dc9efed16364")) {
                return data;
            }
        }
        
        return null;
    }
}
