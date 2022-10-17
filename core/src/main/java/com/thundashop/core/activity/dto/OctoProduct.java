package com.thundashop.core.activity.dto;

import java.io.Serializable;
import java.util.List;

public class OctoProduct implements Serializable {
    public Integer id;
    public String title;
    public List<ActivityOption> options;
    public String country;
    public String location;
    public String coverImage;
    public String shortDescription;
    public String primaryDescription;

    public class ActivityOption {
        public String id;
        public String internalName;
        public List<Unit> units;
    }
    public class Unit {
        public String id;
        public String title;
        public String internalName;
    }
}
