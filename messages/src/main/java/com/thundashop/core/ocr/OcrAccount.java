/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class OcrAccount extends DataCommon {
    public String accountId;
    public List<OcrFileLines> lines = new ArrayList();
}
