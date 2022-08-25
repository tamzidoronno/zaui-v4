
package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.Translation;
import com.thundashop.core.common.TranslationHandler;

import java.util.ArrayList;
import java.util.List;

public class RegistrationRulesField extends TranslationHandler {
    public boolean required = false;
    public boolean active = false;
    public boolean visible = false;
    public String type = "";
    public List<String> additional = new ArrayList<>();
    public String dependsOnCondition = "";
    @Translation
    public String title = "";
    public String name = "";
    public String width = "100";
}
