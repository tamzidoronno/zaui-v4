package com.thundashop.core.pagemanager.data;

import java.io.Serializable;

public class CarouselConfig implements Serializable {
    public Integer height = 200;
    public Integer heightMobile = 200;
    public Integer time = 5000;
    public String type = "fade";
    public Boolean displayNumbersOnDots = false;
    public Boolean avoidRotate = false;
    public Boolean navigateOnMouseOver = false;
    public Boolean keepAspect = false;
    public Integer windowWidth = -1;
    public Integer innerWidth = -1;
}
