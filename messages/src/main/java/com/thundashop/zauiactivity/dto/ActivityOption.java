package com.thundashop.zauiactivity.dto;

import java.util.List;

import lombok.Data;

@Data
public class ActivityOption {
    private String id;
    private String internalName;
    private List<OctoProductUnit> units;
}
