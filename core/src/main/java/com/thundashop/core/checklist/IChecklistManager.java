/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.checklist;

import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IChecklistManager {
    @Editor
    public List<CheckListError> getErrors(Date from, Date to);
}
