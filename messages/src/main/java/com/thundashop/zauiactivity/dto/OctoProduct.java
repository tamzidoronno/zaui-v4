package com.thundashop.zauiactivity.dto;

import java.util.List;

import com.thundashop.core.common.DataCommon;

public class OctoProduct extends DataCommon {
    private Integer id;
    private String title;
    private List<ActivityOption> options;
    private String country;
    private String location;
    private String coverImage;
    private String shortDescription;
    private String primaryDescription;

    public class ActivityOption {
        private String id;
        private String internalName;
        private List<Unit> units;
    }
    public class Unit {
        private String id;
        private String title;
        private String internalName;
    }
}
