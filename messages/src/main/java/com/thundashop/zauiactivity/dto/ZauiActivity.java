package com.thundashop.zauiactivity.dto;

import com.thundashop.core.productmanager.data.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ZauiActivity extends Product {
    public List<ActivityOption> activityOptionList;
    public Integer productId;
    public Integer supplierId;
}
