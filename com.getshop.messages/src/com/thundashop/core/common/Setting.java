/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.util.ArrayList;
import java.util.List;

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
        clonedSetting.value = this.value;
        clonedSetting.type = this.type;

        if (this.secure) {
            clonedSetting.value = "****************";
        }
        return clonedSetting;
    }
}
