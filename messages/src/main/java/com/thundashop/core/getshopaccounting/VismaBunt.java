/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

/**
 *
 * @author boggi
 */
public class VismaBunt {
    public String VoNo = ""; //Bilagsnr
    public String VoDt = ""; //Bilagsdato
    public String VoTp = ""; //Bilagsart (3 = Kasse)
    public String Txt = ""; //Tekst
    public String DbAcNo = ""; //Debetkonto
    public String DbTxCd = ""; //Debet avgiftskode         
    public String CrAcNo = ""; //Kreditkonto      
    public String CrTxCd = ""; //Kredit avgiftskode
    public String Am = ""; //Beløp
    
    
    public String getHeader() {
        return "@WaVo (VoNo, VoDt, VoTp, Txt, DbAcNo, DbTxCd, CrAcNo, CrTxCd, Am)";
    }
    
    public String toString() {
        String res = "\"" + VoNo + "\" ";
        res += "\"" + VoDt + "\" ";
        res += "\"" + VoTp + "\" ";
        res += "\"" + Txt + "\" ";
        res += "\"" + DbAcNo + "\" ";
        res += "\"" + DbTxCd + "\" ";
        res += "\"" + CrAcNo + "\" ";
        res += "\"" + CrTxCd + "\" ";
        res += "\"" + Am + "\" ";
        return res;
    }
}
