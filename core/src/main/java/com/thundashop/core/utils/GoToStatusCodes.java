package com.thundashop.core.utils;

public enum GoToStatusCodes {
    FETCHING_HOTEL_INFO_SUCCESS(1000, "Successfully Returned Hotel Information"),
    FETCHING_HOTEL_INFO_FAIL(1009, "Failed to Fetch Hotel Information.. Reason: Unknown"),
    FETCHING_ROOM_TYPE_INFO_SUCCESS(1100, "Successfully Returned RoomType Information"),
    FETCHING_ROOM_TYPE_INFO_FAIL(1109, "Failed to Fetch Room Type Information.. Reason: Unknown"),
    FETCHING_PRICE_ALLOTMENT_SUCCESS(1200, "Successfully Returned Price and Allotment List"),
    LARGER_DATE_RANGE(1201, "Failed to Fetch Price-Allotment.. Reason: Date Range is Larger than one month.."),
    INVALID_DATE_RANGE(1202, "Failed to Fetch Price-Allotment.. Reason: Invalid Date range"),
    FETCHING_PRICE_ALLOTMENT_FAIL(1209, "Failed to Fetch Price-Allotment.. Reason: Unknown"),
    SAVE_BOOKING_SUCCESS(1300, "Successfully received the HoldBooking"),
    DIFFERENT_CURRENCY(1301, "Goto Booking Failed.. Reason: Different Currency"),
    ROOM_TYPE_NOT_FOUND(1302, "Goto Booking Failed.. Reason: Room type not found for roomCode-> "),
    EMPTY_ROOM_LIST(1303, "Goto Booking Failed.. Reason: Empty room list"),
    OVERBOOKING(1304, "Goto Booking Failed.. Reason: Overbooking"),
    INVALID_CHECKIN_CHECKOUT_FORMAT(1305, "Goto Booking Failed.. Reason: Invalid checkin/ checkout date format"),
    SAVE_BOOKING_FAIL(1309, "Goto Booking Failed.. Reason: Unknown"),
    BOOKING_CONFIRMATION_SUCCESS(1400, "Goto Booking has been Confirmed"),
    BOOKING_NOT_FOUND(1401, "Goto Booking Confirmation Failed.. Reason: Booking Not Found"),
    PAYMENT_FAILED(1402, "Goto Booking Confirmation Failed.. Reason: Payment failed"),
    PAYMENT_METHOD_NOT_FOUND(1403, "Goto Booking Payment Confirmation Failed.. " +
            "Reason: No Payment Method found in Goto Configuration"),
    PAYMENT_METHOD_ACTIVATION_FAILED(1404, "Goto Booking Confirmation Failed.. Reason: Payment method activation failed"),
    BOOKING_DELETED(1405, "Goto Booking Confirmation Failed.. Reason: Booking Has Been Deleted"),
    BOOKING_CONFIRMATION_FAILED(1409, "Goto Booking Confirmation Failed.. Reason: Unknown"),
    BOOKING_CANCELLATION_SUCCESS(1500, "Goto Booking has been Cancelled"),
    BOOKING_CANCELLATION_NOT_FOUND(1501, "Goto Booking Cancellation Failed.. Reason: Booking Not Found"),
    ORDER_SYNCHRONIZATION_FAILED(1502, "Goto Booking Cancellation Failed.. Reason: Order Synchronization Failed"),
    CANCELLATION_DEADLINE_PASSED(1503, "Goto Booking Cancellation Failed.. Reason: Cancellation DeadLine Has Passed"),
    BOOKING_CANCELLATION_ALREADY_CANCELLED(1504, "Goto Booking Cancellation Failed.. Reason: Booking is Already Cancelled"),
    BOOKING_CANCELLATION_FAILED(1509, "Goto Booking Cancellation Failed.. Reason: Unknown");

    public final long code;
    public final String message;

    private GoToStatusCodes(long code, String message) {
        this.code = code;
        this.message = message;
    }

}
