/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PostLoad;

/**
 *
 * @author hjemme
 */
@Entity("Credentials")
public class DataCommon extends TranslationHandler implements Serializable, Cloneable {

    @Id
    public String id = "";
    public String storeId = "";
    public Date deleted = null;
    public String className = getClass().getName();
    public Date rowCreatedDate;
    public Date lastModified = null;
    public String gs_manager;
    public String colection;
    public String gsDeletedBy;
    public String lastModifiedByUserId = "";
    public String getshopModule = "";
    
    public boolean deepFreeze = false;

    @PostLoad
    public void postLoad() {
        if (rowCreatedDate == null) {
            rowCreatedDate = new Date(0);
        }
    }

    public boolean isOf(Class check) {
        return this.getClass().isAssignableFrom(check);
    }

    public boolean createdBetween(Date start, Date end) {
        if (start.equals(rowCreatedDate)) {
            return true;
        }

        if (end.equals(rowCreatedDate)) {
            return true;
        }

        if (start.before(rowCreatedDate) && end.after(rowCreatedDate)) {
            return true;
        }

        return false;
    }
    
    public boolean createdOnDay(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(rowCreatedDate);
        String first = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.DAY_OF_YEAR);
        
        cal.setTime(day);
        String second = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.DAY_OF_YEAR);
        return second.equals(first);
    }

    public DataCommon clone() throws CloneNotSupportedException {
        return (DataCommon) super.clone();
    }

    boolean isCms() {
        return getshopModule == null || getshopModule.isEmpty() || getshopModule.equals("cms");
    }
}
