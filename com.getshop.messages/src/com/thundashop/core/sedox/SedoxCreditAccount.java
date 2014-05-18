/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SedoxCreditAccount implements Serializable {
    public List<SedoxCreditHistory> history = new ArrayList();
    public double balance;
}
