/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.cartuning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author boggi
 */
public class CarTuningData implements Serializable {
    public List<CarTuningData> subEntries = new ArrayList();
    public String name = "";
    public int originalHp = 0;
    public int originalNm = 0;
    public int maxHp = 0;
    public int maxNw = 0;
    public int normalHp = 0;
    public int normalNw = 0;
}
