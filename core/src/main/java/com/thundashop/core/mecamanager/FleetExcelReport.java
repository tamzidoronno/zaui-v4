/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.thundashop.core.common.ExcelBase;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author ktonder
 */
public class FleetExcelReport extends ExcelBase {
    private final String pageId;
    private final MecaManager mecaManager;
    private int rownum = 0;

    public FleetExcelReport(MecaManager mecaManager, String pageId) {
        super("FleetReport");
        this.pageId = pageId;
        this.mecaManager = mecaManager;
        createTitle();
        createHeader();
        createReport();
        autoSizeColumns();
    }

    private void autoSizeColumns() {
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
    }
    
    public void createReport() {
        List<MecaCar> carsNeedServiceNow = mecaManager.getCarsServiceList(true).stream().filter(car -> mecaManager.isCarInFleetByPageId(pageId, car.id)).collect(Collectors.toList());
        List<MecaCar> carsNotNeedServiceNow = mecaManager.getCarsServiceList(false).stream().filter(car -> mecaManager.isCarInFleetByPageId(pageId, car.id)).collect(Collectors.toList());
        
        Collections.sort(carsNeedServiceNow, (MecaCar car1, MecaCar car2) -> {
            if (car1.nextService == null || car2.nextService == null)
                return 0;
            
            return car1.nextService.compareTo(car2.nextService);
        });
        
        Collections.sort(carsNotNeedServiceNow, (MecaCar car1, MecaCar car2) -> {
            if (car1.nextService == null || car2.nextService == null)
                return 0;
            
            return car1.nextService.compareTo(car2.nextService);
        });
        
        carsNeedServiceNow.forEach(car -> addDateToRow(car));
        if (carsNeedServiceNow.size() > 0) {
            rownum++;
        }
        
        carsNotNeedServiceNow.stream().filter(car -> mecaManager.isCarInFleetByPageId(pageId, car.id)).forEach(car -> addDateToRow(car));
    }

    private void addDateToRow(MecaCar icar) {
        MecaCar car = mecaManager.getCar(icar.id);
        
        XSSFRow row = sheet.createRow(rownum++);
        
        XSSFCell cell = row.createCell(0);
        cell.setCellValue(car.licensePlate);
        
        cell = row.createCell(1);
        if (car.kilometersToNextService == null) {
            cell.setCellValue("N/A");
        } else {
            cell.setCellValue(car.kilometersToNextService);
        }
        
        
        cell = row.createCell(2);
        cell.setCellValue(car.monthsToNextService);
        
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
        
        cell = row.createCell(3);
        cell.setCellValue(car.nextService);
        cell.setCellStyle(cellStyle);
        if (car.needAttentionToService()) {
            setRedColor(cell);
        }
        
        
        cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
        
        cell = row.createCell(4);
        cell.setCellValue(car.nextControll);
        cell.setCellStyle(cellStyle);
        if (car.needAttentionToControl()) {
            setRedColor(cell);
        }
        
        cell = row.createCell(5);
        cell.setCellValue(car.carOwner);
        cell.setCellStyle(cellStyle);
        
        cell = row.createCell(6);
        cell.setCellValue(car.cellPhone);
        cell.setCellStyle(cellStyle);
    }

    private void setRedColor(XSSFCell cell) {
        Font font = workbook.createFont();
        font.setColor(HSSFColor.RED.index);
        font.setBold(false);
        cell.getCellStyle().setFont(font);
    }

    private void createHeader() {
        XSSFRow row = sheet.createRow(rownum++);
        
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("Regnr");
        
        cell = row.createCell(1);
        cell.setCellValue("Kilomter til neste service");
        
        cell = row.createCell(2);
        cell.setCellValue("Måneder til neste service");
        
        cell = row.createCell(3);
        cell.setCellValue("Dato for neste service");
        
        cell = row.createCell(4);
        cell.setCellValue("Dato for neste PKK");
        
        cell = row.createCell(5);
        cell.setCellValue("Bileier");
        
        cell = row.createCell(6);
        cell.setCellValue("Telefon");
        
        
    }

    private void createTitle() {
        XSSFRow row = sheet.createRow(rownum++);
        
        MecaFleet fleet = mecaManager.getFleetPageId(pageId);
        
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("Flåte:");
        
        cell = row.createCell(1);
        cell.setCellValue(fleet.name);
        
        
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("Dato:");
        
        cell = row.createCell(1);
        cell.setCellValue(new Date());
        
        
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
        
        cell.setCellStyle(cellStyle);
        
        rownum++;
    }
}
