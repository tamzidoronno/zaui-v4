package com.thundashop.core.wubook;

public enum WuBookApiCalls {
    ACQUIRE_TOKEN("acquire_token"),
    FETCH_BOOKINGS_CODES("fetch_bookings_codes"),
    FETCH_BOOKING("fetch_booking"),
    BCOM_NOTIFY_NOSHOW("bcom_notify_noshow"),
    RPLAN_UPDATE_RPLAN_VALUES("rplan_update_rplan_values"),
    BCOM_NOTIFY_INVALID_CC("bcom_notify_invalid_cc"),
    NEW_ROOM("new_room"),
    NEW_VIRTUAL_ROOM("new_virtual_room"),
    MOD_ROOM("mod_room"),
    FETCH_BOOKINGS("fetch_bookings"),
    GET_OTAS("get_otas"),
    UPDATE_SPARSE_ROOMS_VALUES("update_sparse_rooms_values"),
    MOD_VIRTUAL_ROOM("mod_virtual_room"),
    BCOM_ROOMS_RATES("bcom_rooms_rates"),
    EXP_ROOMS_RATES("exp_rooms_rates"),
    PUSH_ACTIVATION("push_activation"),
    UPDATE_PLAN_PRICES("update_plan_prices");

    private final String value;
    WuBookApiCalls(String value) {
        this.value = value;
    }
    public String value() {
        return value;
    }
}
