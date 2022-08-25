/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.productmanager.data.Product;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
public class DataCommon extends TranslationHandler implements Cloneable {

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

    private void checkAnnotations(Field field, boolean Administrator) throws IllegalArgumentException, IllegalAccessException {
        Object emptyObject = new Product();

        for (Annotation ann : field.getDeclaredAnnotations()) {
            if (ann instanceof Administrator) {
                field.set(this, field.get(emptyObject));
            }

            if (Administrator) {
                continue;
            }

            if (ann instanceof Editor) {
                field.set(this, field.get(emptyObject));
            }
        }

    }

    public void updatePermissionsCustomer() throws IllegalArgumentException, IllegalAccessException {
        for (Field field : this.getClass().getDeclaredFields()) {
            checkAnnotations(field, false);
        }
    }

    public void updatePermissionsEditor() throws IllegalArgumentException, IllegalAccessException {
        for (Field field : this.getClass().getDeclaredFields()) {
            checkAnnotations(field, true);
        }
    }

    public void onSaveValidate() throws ErrorException {
    }

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
