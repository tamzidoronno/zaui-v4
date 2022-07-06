package com.thundashop.core.jomres.dto;

public class JomresProperty {

    // TODO: Aceess modifier will be added in refactor branch
    int propertyId = 0;
    String propertyName = "";

    public JomresProperty() {
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getPropertyId() {
        return propertyId;
    }


    //TODO: @DATA annotation will be used instead of getter-setter
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public JomresProperty(int propertyId, String propertyName) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
    }
}
