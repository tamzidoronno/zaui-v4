package com.thundashop.core.jomres.dto;

import lombok.Data;

@Data
public class JomresProperty {
    int propertyId = 0;
    String propertyName = "";

    public JomresProperty() {
    }

    public JomresProperty(int propertyId, String propertyName) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
    }
}
