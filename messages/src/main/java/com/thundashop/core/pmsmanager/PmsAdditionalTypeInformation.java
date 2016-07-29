package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;

public class PmsAdditionalTypeInformation extends DataCommon {
    public String typeId = "";
    public Integer numberOfBeds = 2;
    public Integer maxNumberOfSingle = 2;
    public Integer maxNumberOfDoubleBeds = 2;
    public Integer numberOfChildrenBed = 1;
    public Integer numberOfChildren = 1;
    public Integer numberOfAdults = 2;

    void update(PmsAdditionalTypeInformation info) {
        numberOfBeds = info.numberOfBeds;
        maxNumberOfSingle = info.maxNumberOfSingle;
        numberOfChildrenBed = info.numberOfChildrenBed;
        numberOfChildren = info.numberOfChildren;
        numberOfAdults = info.numberOfAdults;
        maxNumberOfDoubleBeds = info.maxNumberOfDoubleBeds;
    }
}
