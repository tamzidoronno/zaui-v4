
package com.thundashop.core.storemanager.data;

public class SettingsRow {
     String settingsKey;
        String description;
        String name;
        String type = "string";

        public SettingsRow(String settingsKey, String desc, String type, String name) {
            this.settingsKey = settingsKey;
            this.description = desc;
            this.type = type;
            this.name = name;
        }
}
