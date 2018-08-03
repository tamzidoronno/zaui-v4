<?php
class core_wubook_WubookBooking {
	/** @var String */
	public $channelId;

	/** @var String */
	public $reservationCode;

	/** @var String */
	public $channel_reservation_code;

	/** @var String */
	public $delete;

	/** @var String */
	public $customerNotes;

	/** @var String */
	public $phone;

	/** @var String */
	public $wasModified;

	/** @var String */
	public $email;

	/** @var String */
	public $city;

	/** @var String */
	public $postCode;

	/** @var String */
	public $address;

	/** @var String */
	public $name;

	/** @var String */
	public $depDate;

	/** @var String */
	public $arrivalDate;

	/** @var core_wubook_WubookBookedRoom[] */
	public $rooms;

	/** @var String */
	public $modifiedReservation;

	/** @var String */
	public $status;

	/** @var String */
	public $countryCode;

	/** @var String */
	public $isExpediaCollect;

	/** @var String */
	public $isNonRefundable;

	/** @var String */
	public $isBookingComVirtual;

	/** @var String */
	public $isPrePaid;

	/** @var String */
	public $isAddedToPms;

}
?>