/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.thundashop.core.common.GetShopApi;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ILasGruppenPDFGenerator {
    public String generatePdf();
}
