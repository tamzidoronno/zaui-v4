package com.thundashop.core.gotohub.constant;

public enum GoToStatusCodes {
        FETCHING_HOTEL_INFO_SUCCESS(1000, "Successfully Returned Hotel Information"),
        FETCHING_HOTEL_INFO_FAIL(1009, "Failed to Fetch Hotel Information.. Reason: Unknown"),
        FETCHING_ROOM_TYPE_INFO_SUCCESS(1100, "Successfully Returned RoomType Information"),
        FETCHING_ROOM_TYPE_INFO_FAIL(1109, "Failed to Fetch Room Type Information.. Reason: Unknown"),
        FETCHING_PRICE_ALLOTMENT_SUCCESS(1200, "Successfully Returned Price and Allotment List"),
        LARGER_DATE_RANGE(1201, "Failed to Fetch Price-Allotment.. Reason: Date Range is Larger than one month.."),
        INVALID_DATE_RANGE_ALLOTMENT(1202, "Failed to Fetch Price-Allotment.. Reason: Invalid Date range"),
        FETCHING_PRICE_ALLOTMENT_FAIL(1209, "Failed to Fetch Price-Allotment.. Reason: Unknown"),
        SAVE_BOOKING_SUCCESS(1300, "Successfully received the HoldBooking"),
        DIFFERENT_CURRENCY(1301, "Goto Booking Failed.. Reason: Different Currency"),
        ROOM_TYPE_NOT_FOUND(1302, "Goto Booking Failed.. Reason: Room type not found"),
        EMPTY_BOOKING_ITEM(1303, "Goto Booking Failed.. Reason: Empty room and activity list," +
                        " you need to book at least one room or activity"),
        NO_ALLOTMENT(1304, "Goto Booking Failed.. Reason: No Allotment Found for the time period"),
        INVALID_CHECKIN_CHECKOUT_FORMAT(1305, "Goto Booking Failed.. Reason: Invalid checkin/checkout date format"),
        INVALID_RATE_PLAN_CODE(1306, "Goto Booking Failed.. Reason: Invalid rate-plan code"),
        NUMBER_OF_GUESTS_RATE_PLAN_CODE_MISMATCHED(1307,
                        "Goto Booking Failed.. Reason: Number of guests not matched with rate-plan"),
        OVERFLOW_MAX_NUMBER_OF_GUESTS(1308,
                        "Goto Booking Failed.. Reason: Number of guests overflowed max number of guests"),
        SAVE_BOOKING_FAIL(1309, "Goto Booking Failed.. Reason: Unknown"),
        PRICE_MISSING(1310, "Goto Booking Failed.. Reason: Price/daily price is missing or invalid"),
        INVALID_DATE_RANGE_BOOKING(1311, "Goto Booking Failed.. Reason: Invalid checkin - checkout date range"),
        INCORRECT_DAILY_PRICE_MATRIX(1312, "Goto Booking Failed.. Reason: Daily price matrix is incorrect"),
        EMAIL_OR_PHONE_MISSING(1313, "Goto Booking Failed.. Reason: " +
                        "You must provide either orderer email address or phone number or both correctly"),
        OCTO_RESERVATION_RESPONSE_MISSING(1314, "Goto Booking Failed.. Reason: Activity list has object " +
                        "but Octo reservation response is missing"),
        ACTIVITY_OPTION_ID_MISSING(1315,
                        "Goto Booking Failed.. Reason: Option Id is missing in Octo reservation response"),
        ZAUI_ACTIVITY_NOT_AVAILABLE(1316,
                        "Goto Booking Failed.. Reason: The option or the activity is not available in ZauiStay"),
        ACTIVITY_AVAILABILITY_MISSING(1317, "Goto Booking Failed.. Reason: Activity availability is missing"),
        ACTIVITY_AVAILABILITY_ID_MISSING(1318, "Goto Booking Failed.. Reason: Activity availability Id is missing"),
        ACTIVITY_UNIT_ITEM_INCORRECT(1319, "Goto Booking Failed.. Reason: Activity unit item is missing or empty"),
        ACTIVITY_START_TIME_MISSING(1320, "Goto Booking Failed.. Reason: Activity availability start time is missing"),
        ACTIVITY_END_TIME_MISSING(1321, "Goto Booking Failed.. Reason: Activity availability end time is missing"),
        ACTIVITY_PRICING_MISSING(1322, "Goto Booking Failed.. Reason: Activity pricing is missing"),
        RESERVATION_INCLUDED_TAX_RATE_MISSING(1323, "Goto Booking Failed.. " +
                "Reason: Pricing information doesn't have taxRate information"),
        ACTIVITY_PRICING_INVALID_TAX_RATE(1324,
                        "Goto Booking Failed.. Reason: Activity pricing has tax rates which are not in Zauistay"),
        ZAUI_ACTIVITY_CONFIG_FETCH_FAILED(1325,
                        "Goto Booking Failed..  Reason: Hotel Zaui Activity configuration has problem." +
                                        " Please contact with responsible person"),
        ACTIVITY_SUPPLIER_ID_INVALID(1326, "Goto Booking Failed.. Reason: Activity has invalid supplier id"),
        ACTIVITY_SUPPLIER_ID_NOT_CONNECTED(1327, "Goto Booking Failed.. Reason: Supplier is not connected with hotel"),

        BOOKING_CONFIRMATION_SUCCESS(1400, "Goto Booking has been confirmed"),
        BOOKING_CONFIRMATION_SUCCESS_WITH_PAYMENT_LINK(1400, "Payment link has been attached"),
        BOOKING_NOT_FOUND(1401, "Goto Booking Confirmation Failed.. Reason: Booking Not Found"),
        PAYMENT_FAILED(1402, "Goto Booking Confirmation Failed.. Reason: Payment failed"),
        PAYMENT_METHOD_NOT_FOUND(1403, "Goto Booking Payment Confirmation Failed.. " +
                        "Reason: No Payment Method found in Goto Configuration"),
        PAYMENT_METHOD_ACTIVATION_FAILED(1404,
                        "Goto Booking Confirmation Failed.. Reason: Payment method activation failed"),
        BOOKING_DELETED(1405, "Goto Booking Confirmation Failed.. Reason: Booking Has Been Deleted"),
        OCTO_RESERVATION_ID_MISMATCHED(1406,
                        "Goto Booking Confirmation Failed.. Reason: Activity's Octo reservation ids mismatched"),
        OCTO_RESERVATION_NOT_CONFIRMED(1407, "Goto Booking Confirmation Failed.. " +
                        "Reason: Activities have Octo reservation not in /'CONFIRMED/' status"),
        NON_KABRU_HOLD_BOOKING_HAS_ACTIVITY(1408, "Goto Booking Confirmation Failed.. " +
                "Reason: Non kabru booking with activity is not allowed, hold booking has activity"),
        BOOKING_CONFIRMATION_FAILED(1409, "Goto Booking Confirmation Failed.. Reason: Unknown"),
        CONFIRMATION_RESPONSE_MISSING(1410, "Goto Booking Confirmation Failed.. " +
                "Reason: Activity doesn't contain Octo confirmation response"),
        CONFIRMATION_PRICING_MISSING(1411, "Goto Booking Confirmation Failed.. " +
                "Reason: Activity doesn't have pricing information"),
        CONFIRMATION_INCLUDED_TAX_RATE_MISSING(1412, "Goto Booking Confirmation Failed.. " +
                "Reason: Pricing information doesn't have taxRate information"),
        CONFIRMATION_REQUEST_ACTIVITY_MISSING(1413, "Goto Booking Confirmation Failed.. " +
                "Reason: Hold booking has activity but there is no activity in confirmation request"),
        CONFIRMATION_HOLD_BOOKING_DOESNT_HAVE_ACTIVITY(1414, "Goto Booking Confirmation Failed.. " +
                "Reason: Confirmation request body has activities but hold-booking don't have any activities"),
        NON_KABRU_CONFIRM_BOOKING_REQ_HAS_ACTIVITY(1415, "Goto Booking Confirmation Failed.. " +
                "Reason: Non kabru booking with activity is not allowed, confirmation request body has activity"),
        BOOKING_CANCELLATION_SUCCESS(1500, "Goto Booking has been Cancelled"),
        BOOKING_CANCELLATION_NOT_FOUND(1501, "Goto Booking Cancellation Failed.. Reason: Booking Not Found"),
        ORDER_SYNCHRONIZATION_FAILED(1502, "Goto Booking Cancellation Failed.. Reason: Order Synchronization Failed"),
        CANCELLATION_DEADLINE_PASSED(1503,
                        "Goto Booking Cancellation Failed.. Reason: Cancellation DeadLine Has Passed"),
        BOOKING_CANCELLATION_ALREADY_CANCELLED(1504,
                        "Goto Booking Cancellation Failed.. Reason: Booking is Already Cancelled"),
        CANCELLATION_ACK_FAILED(1505, "Goto Booking Cancellation Succeeded but Failed to Acknowledge"),
        BOOKING_HAS_ACTIVITY(1506, "Goto Booking Cancellation Failed.. Reason: Booking has activities, can't be cancelled"),
        BOOKING_CANCELLATION_FAILED(1509, "Goto Booking Cancellation Failed.. Reason: Unknown");

        public final long code;
        public final String message;

        GoToStatusCodes(long code, String message) {
                this.code = code;
                this.message = message;
        }

}
