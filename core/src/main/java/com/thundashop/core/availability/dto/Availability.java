package com.thundashop.core.availability.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Naim Murad (naim)
 * @since 6/15/22
 */
@Data
public class Availability {

    private String date;
    private BigDecimal lowestPrice;
    private String currencyCode;
    private long totalNoOfRooms;
    private boolean allCategoryAvailable;
    private Map<String, Integer> totalNoOfRoomsByCategory;
}
