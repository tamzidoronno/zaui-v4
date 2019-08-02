/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

/**
 *
 * @author ktonder
 */
public class Setting extends DataCommon {
    public String name = "";
    public String type = "";
    public String value = "";
    public boolean secure = false;

    public Setting secureClone() {
        Setting clonedSetting = new Setting();
        clonedSetting.name = this.name;
        clonedSetting.value = this.value;
        clonedSetting.type = this.type;

        if (secure  && value != null && !value.isEmpty()) {
            clonedSetting.value = "****************";
        }
        return clonedSetting;
    }
}
