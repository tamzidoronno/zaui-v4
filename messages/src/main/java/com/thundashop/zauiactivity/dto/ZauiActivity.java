package com.thundashop.zauiactivity.dto;

import java.util.List;

import com.thundashop.core.productmanager.data.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ZauiActivity extends Product {
    private List<ActivityOption> activityOptionList;
    private Integer productId;
    private Integer supplierId;
    private String supplierName;
    private String currency;
}
