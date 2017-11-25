package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class PmsAdditionalTypeInformation extends DataCommon {
    public String typeId = "";
    public Integer defaultNumberOfBeds = 1;
    public Integer defaultNumberOfChildBeds = 0;
    public Integer maxNumberOfBeds = 2;
    public Integer maxNumberOfChildBeds = 0;
    public Integer numberOfChildren = 1;
    public Integer numberOfAdults = 2;
    public List<PmsTypeImages> images = new ArrayList();
    public String dependsOnTypeId = "";
    public List<String> accessories = new ArrayList();

    void update(PmsAdditionalTypeInformation info) {
        defaultNumberOfChildBeds = info.defaultNumberOfChildBeds;
        numberOfChildren = info.numberOfChildren;
        numberOfAdults = info.numberOfAdults;
        defaultNumberOfBeds = info.defaultNumberOfBeds;
        maxNumberOfBeds = info.maxNumberOfBeds;
        maxNumberOfChildBeds = info.maxNumberOfChildBeds;
        images = info.images;
        dependsOnTypeId = info.dependsOnTypeId;
        accessories = info.accessories;
    }
}
