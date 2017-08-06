package com.thundashop.core.getshop.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Editor;
import java.util.HashMap;
import java.util.Random;

public class GetShopLockMasterCodes extends DataCommon {
    @Editor
    public HashMap<Integer, String> codes = new HashMap();

    public boolean checkIfEmtpy() {
        if(codes.keySet().isEmpty()) {
            for(int i = 1; i <= 5; i++) {
                Integer newcode = new Random().nextInt(99999) + 100000;
                codes.put(i, newcode + "");
            }
            return true;
        }
        return false;
    }
}
