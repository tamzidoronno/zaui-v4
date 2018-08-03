<?php
class core_bookingengine_data_BookingItem extends core_common_DataCommon  {
	/** @var String */
	public $bookingItemTypeId;

	/** @var String */
	public $bookingItemName;

	/** @var String */
	public $bookingItemAlias;

	/** @var String */
	public $doorId;

	/** @var String */
	public $pageId;

	/** @var String */
	public $description;

	/** @var String */
	public $lockGroupId;

	/** @var String */
	public $bookingSize;

	/** @var String */
	public $waitingListSize;

	/** @var String */
	public $fullWhenCountHit;

	/** @var String */
	public $availabilitieIds;

	/** @var String */
	public $bookingIds;

	/** @var String */
	public $waitingListBookingIds;

	/** @var String */
	public $isFull;

	/** @var String */
	public $freeSpots;

	/** @var core_bookingengine_data_Availability[] */
	public $availabilities;

	/** @var String */
	public $bookingOwnerUserId;

	/** @var core_bookingengine_data_RegistrationRules */
	public $rules;

	/** @var String */
	public $order;

}
?>