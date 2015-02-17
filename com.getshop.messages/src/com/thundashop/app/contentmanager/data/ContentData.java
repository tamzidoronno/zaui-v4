/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.app.contentmanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Translation;
import com.thundashop.core.common.TranslationHandler;
import java.io.Serializable;

/**
 *
 * @author boggi
 */
public class ContentData extends DataCommon implements Serializable {
    @Translation
    public String content = "";
    public String appId;
}
