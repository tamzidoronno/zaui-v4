package com.thundashop.core.pagemanager.data;

import java.io.Serializable;

public class PageCellSettings implements Serializable {
    public boolean displayWhenLoggedOn = true;
    public boolean displayWhenLoggedOut = true;
    public Integer editorLevel = 0;
    public String anchor = "";
}
