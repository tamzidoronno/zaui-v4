<?php
class core_pmsmanager_PmsBooking extends core_common_DataCommon  {
	/** @var core_pmsmanager_PmsBookingRooms[] */
	public $rooms;

	/** @var String */
	public $notificationsSent;

	/** @var java.util.HashMap */
	public $comments;

	/** @var String */
	public $sessionId;

	/** @var String */
	public $sessionStartDate;

	/** @var String */
	public $sessionEndDate;

	/** @var String */
	public $silentNotification;

	/** @var String */
	public $bookingEngineAddons;

	/** @var core_bookingengine_data_RegistrationRules */
	public $registrationData;

	/** @var String */
	public $language;

	/** @var String */
	public $userId;

	/** @var String */
	public $bookedByUserId;

	/** @var String */
	public $state;

	/** @var String */
	public $orderIds;

	/** @var String */
	public $incOrderIds;

	/** @var String */
	public $priceType;

	/** @var String */
	public $confirmed;

	/** @var String */
	public $confirmedDate;

	/** @var String */
	public $completedDate;

	/** @var String */
	public $unConfirmed;

	/** @var String */
	public $isDeleted;

	/** @var String */
	public $payedFor;

	/** @var String */
	public $avoidCreateInvoice;

	/** @var String */
	public $createOrderAfterStay;

	/** @var String */
	public $testReservation;

	/** @var core_pmsmanager_PmsRepeatingData */
	public $lastRepeatingData;

	/** @var String */
	public $invoiceNote;

	/** @var String */
	public $dueDays;

	/** @var String */
	public $periodesToCreateOrderOn;

	/** @var String */
	public $discountType;

	/** @var String */
	public $couponCode;

	/** @var String */
	public $pmsPricingCode;

	/** @var String */
	public $wubookchannelid;

	/** @var String */
	public $wubookchannelreservationcode;

	/** @var String */
	public $wubookreservationid;

	/** @var String */
	public $alternativeOrginasation;

	/** @var String */
	public $wubookModifiedResId;

	/** @var String */
	public $wubookNoShow;

	/** @var String */
	public $transferredToRateManager;

	/** @var String */
	public $avoidAutoDelete;

	/** @var String */
	public $incrementBookingId;

	/** @var String */
	public $countryCode;

	/** @var String */
	public $needCapture;

	/** @var String */
	public $wubookChannelReservationId;

	/** @var String */
	public $channel;

	/** @var String */
	public $ignoreCheckChangesInBooking;

	/** @var String */
	public $deletedBySource;

	/** @var String */
	public $totalPrice;

	/** @var String */
	public $paymentType;

	/** @var String */
	public $orderCreatedAfterStay;

	/** @var String */
	public $isConference;

	/** @var String */
	public $bookingAmountDiff;

	/** @var String */
	public $totalUnsettledAmount;

	/** @var String */
	public $unsettled;

	/** @var String */
	public $nonrefundable;

	/** @var String */
	public $secretBookingId;

	/** @var String */
	public $ignoreNoShow;

	/** @var String */
	public $quickReservation;

	/** @var String */
	public $latestwubookreservationid;

	/** @var String */
	public $agreedToTermsAndConditions;

	/** @var String */
	public $startDate;

	/** @var String */
	public $endDate;

	/** @var String */
	public $isAddedToEventList;

}
?>