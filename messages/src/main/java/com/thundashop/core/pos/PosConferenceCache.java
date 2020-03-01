/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.pos;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 * A dirty conference is when there needs to be autocreated orders for.
 * 
 * @author ktonder
 */
public class PosConferenceCache extends DataCommon {
    private List<PosConferenceCacheDirty> dirtyConferences = new ArrayList();
    
    public void markDirty(String pmsConferenceId, String tabId) {
        PosConferenceCacheDirty ob = new PosConferenceCacheDirty();
        ob.pmsConferenceId = pmsConferenceId;
        ob.tabId = tabId;
        dirtyConferences.add(ob);
    }
    
    public List<PosConferenceCacheDirty> getDirtyConferences() {
        return dirtyConferences;
    }

    void clear() {
        dirtyConferences.clear();
    }
    
}


