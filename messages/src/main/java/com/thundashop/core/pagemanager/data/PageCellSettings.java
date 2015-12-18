package com.thundashop.core.pagemanager.data;

import java.io.Serializable;

public class PageCellSettings implements Serializable {
    public boolean displayWhenLoggedOn = true;
    public boolean displayWhenLoggedOut = true;
    public Integer editorLevel = 0;
    
    public boolean paralexxRow = false;
    public boolean scrollFadeIn = false;
    public boolean isFlipping = false;
    public int scrollFadeInDuration = 300;
    public Double scrollFadeInStartOpacity = 0.5;
    public Double scrollFadeInEndOpacity = 1D;
    public int slideLeft = 0;
    public int slideTop = 0;
}
