package com.thundashop.core.pagemanager.data;

import java.io.Serializable;

public class CarouselConfig implements Serializable {
    public Integer height = 200;
    public Integer heightMobile = 200;
    public Integer time = 5000;
    public String type = "fade";
    public Boolean hideDots = false;
    public Boolean displayNumbersOnDots = false;
    public Boolean avoidRotate = false;
    public Boolean navigateOnMouseOver = false;
    public Boolean keepAspect = false;
    public Boolean heightIsMaximumHeight = false;
    public Integer windowWidth = -1;
    public Integer innerWidth = -1;
}
