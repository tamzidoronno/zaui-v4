
package com.thundashop.core.dibs;

import com.thundashop.core.common.Administrator;

public interface IDibsManager {
    @Administrator
    public void checkForOrdersToCapture();
}
