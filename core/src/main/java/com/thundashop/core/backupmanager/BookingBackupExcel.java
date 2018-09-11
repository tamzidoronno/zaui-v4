/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.backupmanager;

import com.thundashop.core.common.ExcelBase;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.usermanager.data.User;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author ktonder
 */
public class BookingBackupExcel extends ExcelBase {
    private int rowNum = 0;
    public BookingBackupExcel(String sheetName) {
        super(sheetName);
        addHeaders();
    }

    void addRow(User user, PmsBooking booking) {
        rowNum++;
        XSSFRow row = sheet.createRow(rowNum);
        for (PmsBookingRooms room : booking.rooms) {
            for (PmsGuests guest : room.guests) {
                // Booker
                row.createCell(0).setCellValue(booking.incrementBookingId);
                row.createCell(1).setCellValue(user.id);
                row.createCell(2).setCellValue(user.customerId);
                row.createCell(3).setCellValue(user.fullName);
                row.createCell(4).setCellValue(user.emailAddress);
                row.createCell(5).setCellValue(user.emailAddressToInvoice);
                row.createCell(6).setCellValue(user.prefix);
                row.createCell(7).setCellValue(user.cellPhone);
                
                // Rooom
                row.createCell(8).setCellValue(room.date.start);
                row.createCell(9).setCellValue(room.date.end);
                row.createCell(10).setCellValue(guest.name);
                row.createCell(11).setCellValue(guest.email);
                row.createCell(12).setCellValue(guest.prefix);
                row.createCell(13).setCellValue(guest.phone);                
                
                row.createCell(14).setCellValue(room.isDeleted() ? "deleted" : "");                
            }
        }
        

    }

    private void addHeaders() {
        XSSFRow row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue("GetShop Booking Id");
        row.createCell(1).setCellValue("User id");
        row.createCell(2).setCellValue("Booker id2");
        row.createCell(3).setCellValue("Booker name");
        row.createCell(4).setCellValue("Booker email address");
        row.createCell(5).setCellValue("Booker email invoice email");
        row.createCell(6).setCellValue("Booker prefix");
        row.createCell(7).setCellValue("Booker phone");

        // Rooom
        row.createCell(8).setCellValue("Room start date");
        row.createCell(9).setCellValue("Room end date");
        row.createCell(10).setCellValue("Guest name");
        row.createCell(11).setCellValue("Guest email");
        row.createCell(12).setCellValue("Guest prefix");
        row.createCell(13).setCellValue("Guest phone");                
        row.createCell(14).setCellValue("Is deleted");     
    }
    
    
    
}

