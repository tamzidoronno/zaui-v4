
package com.thundashop.core.pagemanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class SavedCommonPageData extends DataCommon {
    public LinkedHashMap<Long, CommonPageData> datas = new LinkedHashMap();
    
    public void saveData(CommonPageData data) {
        int i = 0;
        
        List<Long> toRemove = new ArrayList();
        for(Long date : datas.keySet()) {
            if(i > 15) {
                toRemove.add(date);
            }
            i++;
        }
        
        for(Long date : toRemove) {
            datas.remove(date);
        }
        
        datas.put(new Date().getTime(), data);
    }

    public CommonPageData getClosestLayout(Long fromTime) {
        long diff = -1;
        long toUse = 0;
        for(Long time : datas.keySet()) {
            long tmpdiff = fromTime - time;
            if(tmpdiff < 0) {
                tmpdiff = tmpdiff * -1;
            }
            if(diff < 0 || tmpdiff < diff) {
                diff = tmpdiff;
                toUse = time;
            }
        }
        
        return datas.get(toUse);
    }
}
