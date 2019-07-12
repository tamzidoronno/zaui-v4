/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.thundashop.core.common.DoubleKeyMap;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.usermanager.data.Company;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ktonder
 */
public class ESAReport {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private int rownumber = 0;
    private Date endDate;
    private final List<Company> companies;
    private List<WorkPackage> workPackages;
    
    private final String ousVertsId = "70b90eb2-80de-4a86-9820-cfb06bb5442c";
    
    public ESAReport(List<Company> companies, List<WorkPackage> workPackages, DoubleKeyMap<String, String, Double> totalCosts, DoubleKeyMap<String, String, Double> inKind, Date endDate) {
        this.companies = companies;
        this.workPackages = workPackages;
        this.workPackages = this.workPackages.stream().sorted(WorkPackage.getComperator()).collect(Collectors.toList());
        this.endDate = endDate;
       
        transferCostsToWp11(totalCosts, inKind);
        calculateRcnGrant(totalCosts, inKind);
        mergeOus(totalCosts, inKind);

        totalCosts = devideAllNumbersOn1000(totalCosts);
        inKind = devideAllNumbersOn1000(inKind);
        
        createSheetAndWorkbook();
        setColumnWidth();
        addTitles();
        writeCompanyRow(true);
        writeWorkPackages(totalCosts, true);
        writeExplenations();
        
        rownumber++;
        writeCompanyRow(false);
        writeTypeOfPartnerRow();
        writeWorkPackages(inKind, false);
        writeExplenations2();
    }
    
    public static void main(String[] args) {
        
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        Database db = context.getBean(Database.class);
        List<Company> allUserManagerData = db.getAll("UserManager", "f2d0c13c-a0f7-41a7-8584-3c6fa7eb68d1")
                .filter(data -> data.className.equals(Company.class.getCanonicalName()))
                .map(data -> (Company)data)
                .collect(Collectors.toList());
        
        List<WorkPackage> workPackages = db.getAll("C3Manager", "f2d0c13c-a0f7-41a7-8584-3c6fa7eb68d1")
                .filter(data -> data.className.equals(WorkPackage.class.getCanonicalName()))
                .map(data -> (WorkPackage)data)
                .collect(Collectors.toList());
        
//        fo
        
        DoubleKeyMap<String, String, Double> totalCosts = new DoubleKeyMap();
        
        double j = 0;
        for (WorkPackage wp : workPackages) {
            for (Company comp : allUserManagerData) {
                totalCosts.put(wp.id, comp.id, j);
                j++;
            }
            
        }
        
        DoubleKeyMap<String, String, Double> inKind = new DoubleKeyMap();
        
        for (WorkPackage wp : workPackages) {
            for (Company comp : allUserManagerData) {
                inKind.put(wp.id, comp.id, j);
                j++;
            }
         
            inKind.put(wp.id, "rcngrant", j);
            j++;
        }
        
//        ESAReport report = new ESAReport(allUserManagerData, workPackages, totalCosts, inKind);
//        report.writeFile();
//        System.exit(0);
    } 

    private void writeFile() {

        try {
            FileOutputStream out = new FileOutputStream(new File("/tmp/tmp_esa_report.xlsx"));
            workbook.write(out);
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createSheetAndWorkbook() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("ESA Report");
    }

    private void addTitles() {
        rownumber++;
        XSSFRow row = sheet.createRow(rownumber++);
        XSSFCell cell = createCell(row, 0);
        cell.setCellValue("SFI Excel-template");
        
        
        row = sheet.createRow(rownumber++);
        cell = row.createCell(0);
        cell.setCellValue("Mandatory enclosure for Annual work plan and Annual accounting report");
    }

    private void writeCompanyRow(boolean first) {
        XSSFRow row = sheet.createRow(rownumber++);
        
        XSSFCell cell1 = createCellAndAddText(row, 0, "Item");
        setBorder(cell1, true, true, true, true);
        
        if (first) {
            cell1 = createCellAndAddText(row, 1, "Collaboration project *");
            rotateText(cell1, (short)90);
            setBorder(cell1, true, true, true, true);

            cell1 = createCellAndAddText(row, 2, "Type of  Research**");
            rotateText(cell1, (short)90);
            setBorder(cell1, true, true, true, true);

            cell1 = createCellAndAddText(row, 3, "Incentive effect***");
            rotateText(cell1, (short)90);
            setBorder(cell1, true, true, true, true);
        } else {
            cell1 = createCellAndAddText(row, 1, "Aid Intensity Limit*****");
            rotateText(cell1, (short)90);
            setBorder(cell1, true, true, true, true);
            
            for (int i = 2; i<5; i++) {
                XSSFCell cell = createCellAndAddText(row, i, "");
                setBorder(cell, true, true, true, true);
            }
    
        }
        
        
        cell1 = createCell(row, 4);
        setBackgroundColor(cell1, Color.LIGHT_GRAY);
        setBorder(cell1, true, true, true, true);
        
        int i = 5;
        for (Company comp : companies) {
            XSSFCell cell = createCell(row, i);
            cell.setCellValue(comp.name);
            rotateText(cell, (short)90);
            cell.getCellStyle().setWrapText(true);
            setBorder(cell, true, true, true, true);
            i++;
        }
        
        if (first) {
            XSSFCell cell = createCell(row, i);
            cell.setCellValue("Total cost");
            cell.getCellStyle().setWrapText(true);
            setBorder(cell, true, true, true, true);
            setBackgroundColor(cell, Color.CYAN);
        } else {
            
            XSSFCell cell = createCell(row, i++);
            cell.setCellValue("RCN Grant");
            rotateText(cell, (short)90);
            cell.getCellStyle().setWrapText(true);
            setBorder(cell, true, true, true, true);
            
            cell = createCell(row, i++);
            cell.setCellValue("Other Public funding");
            rotateText(cell, (short)90);
            cell.getCellStyle().setWrapText(true);
            setBorder(cell, true, true, true, true);
            
            cell = createCell(row, i++);
            cell.setCellValue("Total funding");
            cell.getCellStyle().setWrapText(true);
            setBorder(cell, true, true, true, true);
            setBackgroundColor(cell, Color.CYAN);
            
            cell = createCell(row, i++);
            cell.setCellValue("Indirect state aid ******");
            rotateText(cell, (short)90);
            cell.getCellStyle().setWrapText(true);
            setBorder(cell, true, true, true, true);
        }
    }

    private void setBackgroundColor(XSSFCell cell1, Color color) {
        XSSFColor myColor = new XSSFColor(color);
        cell1.getCellStyle().setFillForegroundColor(myColor);
        cell1.getCellStyle().setFillBackgroundColor(myColor);
        cell1.getCellStyle().setFillPattern(CellStyle.SOLID_FOREGROUND);
    }
    
    private void rotateText(XSSFCell cell, short degree) {
        cell.getCellStyle().setRotation((short)degree);
    }

    private XSSFCell createCell(XSSFRow row, int i) {
        XSSFCell cell = row.createCell(i);
        
        XSSFCellStyle myStyle = workbook.createCellStyle();
        cell.setCellStyle(myStyle);

        return cell;
    }

    private void setBorder(XSSFCell cell, boolean b, boolean b0, boolean b1, boolean b2) {
        if (b) 
            cell.getCellStyle().setBorderLeft(BorderStyle.THIN);
        
        if (b0) 
            cell.getCellStyle().setBorderTop(BorderStyle.THIN);
        
        if (b1) 
            cell.getCellStyle().setBorderRight(BorderStyle.THIN);
        
        if (b2) 
            cell.getCellStyle().setBorderBottom(BorderStyle.THIN);
    }

    private XSSFCell createCellAndAddText(XSSFRow row, int i, String item) {
        XSSFCell cell = createCell(row, i);
        cell.setCellValue(item);
        return cell;
    }

    private void writeWorkPackages(DoubleKeyMap<String, String, Double> totalCosts, boolean first) {
        for (WorkPackage wp : workPackages) {
            XSSFRow row = sheet.createRow(rownumber++);
            XSSFCell cell = createCellAndAddText(row, 0, wp.name);
            cell.getCellStyle().setWrapText(true);
            setBorder(cell, true, true, true, true);
            
            for (int i = 1; i<5; i++) {
                cell = createCellAndAddText(row, i, "");
                setBorder(cell, true, true, true, true);
                if (i == 4) {
                    setBackgroundColor(cell, Color.LIGHT_GRAY);
                }
            }
            
            int j = 5;
            for (Company company : companies) {
                writeTotal(row, wp, company.id, j, totalCosts);
                j++;
            }
            
           if (totalCosts.keyExists(wp.id, "rcngrant")) {
                writeTotal(row, wp, "rcngrant", j, totalCosts);
                j++;
                createEmptyCellWithBorder(row, j);
                j++;
            }
            
            writeWpSum(row, wp, j, totalCosts);
            if (!first) {
                j++;
                createEmptyCellWithBorder(row, j);
            }
        }
        
        XSSFRow row = sheet.createRow(rownumber++);
        XSSFCell cell = createCellAndAddText(row, 0, "Totalt Budget");
        setBorder(cell, true, true, true, true);
        setBackgroundColor(cell, Color.LIGHT_GRAY);
        
        double overAll = 0;
        
        for (int i=1; i<5; i++) {
            cell = createEmptyCellWithBorder(row, i);
            setBackgroundColor(cell, Color.LIGHT_GRAY);
        }
        
        int j = 5;
        for (Company company : companies) {
            overAll += writeTotalForCompany(row, j, company.id, totalCosts);
            j++;
        }
        
        
        if (!first) {
            
            overAll += writeTotalForCompany(row, j, "rcngrant", totalCosts);
            j++;
            createEmptyCellWithBorder(row, j);
            j++;
        }
        
        cell = createCell(row, j);
        cell.setCellValue(overAll);
        setBorder(cell, true, true, true, true);
        setBackgroundColor(cell, Color.CYAN);
        
        if (!first) {
            j++;
            createEmptyCellWithBorder(row, j);
        }
    }

    private void setColumnWidth() {
        sheet.setColumnWidth(0, 8000);
    }

    private void writeTotal(XSSFRow row, WorkPackage wp, String companyId, int j, DoubleKeyMap<String, String, Double> totalCosts) {
        XSSFCell cell = createCell(row, j);
        if (totalCosts.get(wp.id, companyId) != null) {
            cell.setCellValue(totalCosts.get(wp.id, companyId));
        }
        setBorder(cell, true, true, true, true);
    }

    private double writeTotalForCompany(XSSFRow row, int j, String inCompanyId, DoubleKeyMap<String, String, Double> totalCosts) {
        double totalForCompany = 0;
        for (String wpId : totalCosts.keySet()) {
            for (String companyId : totalCosts.innerKeySet(wpId)) {
                if (companyId.equals(inCompanyId)) {
                    totalForCompany += totalCosts.get(wpId, companyId);
                }
            }
        }
        
        XSSFCell cell = createCell(row, j);
        cell.setCellValue(totalForCompany);
        setBorder(cell, true, true, true, true);
        
        return totalForCompany;
    }

    private void writeWpSum(XSSFRow row, WorkPackage wp, int j, DoubleKeyMap<String, String, Double> totalCosts) {
        double totalForWorkpackage = 0;
        
        for (String wpId : totalCosts.keySet()) {
            for (String companyId : totalCosts.innerKeySet(wpId)) {
                if (wpId.equals(wp.id)) {
                    totalForWorkpackage += totalCosts.get(wpId, companyId);
                }
            }
        }
        
        XSSFCell cell = createCell(row, j);
        cell.setCellValue(totalForWorkpackage);
        setBorder(cell, true, true, true, true);
        setBackgroundColor(cell, Color.CYAN);
    }

    private void writeExplenations() {
        createCellAndAddText(sheet.createRow(rownumber++), 0, "* Collaboration project: YES / NO. If NO, explain the reasons in a separate annex (see the guidelines).");
        createCellAndAddText(sheet.createRow(rownumber++), 0, "** Type of Research: F= Fundamental research, I=Industrial Research ");
        createCellAndAddText(sheet.createRow(rownumber++), 0, "*** Incentive effect, 1=Present, 0=Not present. First digit: New R&D activity triggered, Second digit: Increase in size of related R&D activity, Third digit: Enhanced scope of related R&D activity, Fourth digit: Increased speed in execution of related R&D activity ");
    }

    private void writeExplenations2() {
        createCellAndAddText(sheet.createRow(rownumber++), 0, "**** Type of partner: R=Research organisation, P=Other public, L=Large Enterprise, SME=Small and medium sized enterprise");
        createCellAndAddText(sheet.createRow(rownumber++), 0, "***** Aid Intensity Limit: Indicate percentage as follows: Fundamental research 100 %. Industrial research 65 % for collaboration projects, 75 % if only SMEs included in the collaboration project .");
        createCellAndAddText(sheet.createRow(rownumber++), 0, "****** State aid: If no indirect state aid, cf. paragraph 28 and the consortium agreement: specify which condition a) - d)  is fulfilled. If none of the conditions are fullfilled, leave the column blank and include a separate calculation of indirect aid as annex (see the guidelines).");
    }
    
    
    public String getBase64Encoded() {
        writeFile();
        
        File file = new File("/tmp/tmp_esa_report.xlsx");
        
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
    

    private void writeTypeOfPartnerRow() {
        XSSFRow row = sheet.createRow(rownumber++);
        XSSFCell cell = createCellAndAddText(row, 0, "Type of partner****");
        setBorder(cell, true, true, true, true);
        
        for (int j = 1; j < companies.size() + 9; j++) {
            cell = createEmptyCellWithBorder(row, j);
            if (j == 4) {
                setBackgroundColor(cell, Color.LIGHT_GRAY);
            }
            
            if (j == companies.size() + 7) {
                setBackgroundColor(cell, Color.CYAN);
            }
        }
    }

    private XSSFCell createEmptyCellWithBorder(XSSFRow row, int j) {
        XSSFCell cell = createCellAndAddText(row, j, "");
        setBorder(cell, true, true, true, true);
        return cell;
    }

    private DoubleKeyMap<String, String, Double> devideAllNumbersOn1000(DoubleKeyMap<String, String, Double> totalCosts) {
        DoubleKeyMap<String, String, Double> newKeySet = new DoubleKeyMap();
        
        for (String wpId : totalCosts.keySet()) {
            for (String companyId : totalCosts.innerKeySet(wpId)) {
                double newValue = Math.round(totalCosts.get(wpId, companyId) / (double)1000);
                newKeySet.put(wpId, companyId, newValue);
            }
        }
        
        return newKeySet;
    }

    private void transferCostsToWp11(DoubleKeyMap<String, String, Double> totalCost, DoubleKeyMap<String, String, Double> inKind) {
        for (String wpId : inKind.keySet()) {
            WorkPackage moveCost = getWorkPackage(wpId);
            inKind.put(wpId, "rcngrant", 0D);
            
            for (String companyId : inKind.innerKeySet(wpId)) {
                
                if (moveCost.shouldRemoveOnePercent(endDate)) {
                    double oldValue = inKind.get(wpId, companyId);
                    double onePercent = oldValue / (double)100;
                    double newValue = oldValue - onePercent;
                    inKind.put(wpId, companyId, newValue);
                    
                    boolean found = false;
                    
                    if (totalCost.get(wpId, companyId) != null) {
                        oldValue = totalCost.get(wpId, companyId);
                        newValue = oldValue - onePercent;
                        totalCost.put(wpId, companyId, newValue);
                        found = true;
                    }

                    if (inKind.keyExists("de20c1c3-faee-4237-8457-dc9efed16364", companyId)) {
                        double toAdd = inKind.get("de20c1c3-faee-4237-8457-dc9efed16364", companyId);
                        toAdd = toAdd + onePercent;
                        inKind.put("de20c1c3-faee-4237-8457-dc9efed16364", companyId, toAdd);
                        if (found)
                            totalCost.put("de20c1c3-faee-4237-8457-dc9efed16364", companyId, toAdd);
                    } else {
                        inKind.put("de20c1c3-faee-4237-8457-dc9efed16364", companyId, onePercent);
                        if (found)
                            totalCost.put("de20c1c3-faee-4237-8457-dc9efed16364", companyId, onePercent);
                    }
                }  
            }
        }
        
    }

    private WorkPackage getWorkPackage(String wpId) {
        for (WorkPackage wp : this.workPackages) {
            if (wp.id.equals(wpId))
                return wp;
        }
        
        return null;
    }

    private void mergeOus(DoubleKeyMap<String, String, Double> totalCosts, DoubleKeyMap<String, String, Double> inKind) {
        String ousBrukerPartnerId = "eb2e16f0-0083-47eb-94bd-297984410350";
        String ousForskningsPartnerId = "4e6e95bc-d362-4362-944c-482176c7c2b4";

        for (String wpId : inKind.keySet()) {
            mergeCompany(wpId, ousBrukerPartnerId, totalCosts);
            mergeCompany(wpId, ousForskningsPartnerId, totalCosts);
        }
        
        for (String wpId : totalCosts.keySet()) {
            mergeCompany(wpId, ousBrukerPartnerId, inKind);
            mergeCompany(wpId, ousForskningsPartnerId, inKind);
        }
        
        this.companies.removeIf(comp -> comp.id.equals(ousBrukerPartnerId));
        this.companies.removeIf(comp -> comp.id.equals(ousForskningsPartnerId));
    }

    private void mergeCompany(String wpId, String companyId, DoubleKeyMap<String, String, Double> totalCosts) {
        Double value = totalCosts.get(wpId, companyId);
        Double oldValue = totalCosts.get(wpId, ousVertsId);
        if (oldValue == null)
            oldValue = 0D;
        
        if (value == null) 
            value = 0D;
        
        double newValue = value + oldValue;
        
        totalCosts.put(wpId, ousVertsId, newValue);
        totalCosts.remove(wpId, companyId);
    }

    private void calculateRcnGrant(DoubleKeyMap<String, String, Double> totalCost, DoubleKeyMap<String, String, Double> inKind) {
        for (String wpId : totalCost.keySet()) {
            
            inKind.put(wpId, "rcngrant", 0D);
            
            for (String companyId : totalCost.innerKeySet(wpId)) {        
                double tCost = totalCost.get(wpId, companyId);
                double iCost = inKind.get(wpId, companyId);
                double rcngrant = tCost - iCost;

                if (inKind.get(wpId, "rcngrant") != null) {
                    rcngrant += inKind.get(wpId, "rcngrant");
                }

                inKind.put(wpId, "rcngrant", rcngrant);
            }
        }
    }
    
}