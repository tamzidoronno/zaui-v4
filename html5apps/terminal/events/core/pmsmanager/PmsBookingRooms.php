<?php
class core_pmsmanager_PmsBookingRooms {
	/** @var String */
	public $bookingItemTypeId;

	/** @var String */
	public $bookingItemId;

	/** @var String */
	public $orderUnderConstructionId;

	/** @var String */
	public $pmsBookingRoomId;

	/** @var core_pmsmanager_PmsGuests[] */
	public $guests;

	/** @var core_pmsmanager_PmsBookingDateRange */
	public $date;

	/** @var String */
	public $numberOfGuests;

	/** @var String */
	public $count;

	/** @var String */
	public $price;

	/** @var String */
	public $priceWithoutDiscount;

	/** @var String */
	public $priceMatrix;

	/** @var String */
	public $taxes;

	/** @var String */
	public $bookingId;

	/** @var core_pmsmanager_PmsBookingAddonItem[] */
	public $addons;

	/** @var String */
	public $currency;

	/** @var String */
	public $cleaningComment;

	/** @var String */
	public $checkedin;

	/** @var String */
	public $checkedout;

	/** @var String */
	public $blocked;

	/** @var String */
	public $code;

	/** @var core_getshoplocksystem_LockCode */
	public $codeObject;

	/** @var String */
	public $cardformat;

	/** @var String */
	public $intervalCleaning;

	/** @var String */
	public $addedByRepeater;

	/** @var String */
	public $invoicedTo;

	/** @var String */
	public $invoicedFrom;

	/** @var String */
	public $keyIsReturned;

	/** @var String */
	public $ended;

	/** @var String */
	public $sentCloseSignal;

	/** @var String */
	public $notificationsSent;

	/** @var String */
	public $addedToArx;

	/** @var String */
	public $canBeAdded;

	/** @var String */
	public $isAddon;

	/** @var String */
	public $forcedOpenDate;

	/** @var String */
	public $forcedOpenNeedClosing;

	/** @var String */
	public $warnedAboutAutoExtend;

	/** @var String */
	public $credited;

	/** @var String */
	public $deleted;

	/** @var String */
	public $deletedDate;

	/** @var String */
	public $totalCost;

	/** @var String */
	public $requestedEndDate;

	/** @var String */
	public $undeletedDate;

	/** @var String */
	public $forceUpdateLocks;

	/** @var String */
	public $deletedByChannelManagerForModification;

	/** @var String */
	public $inWorkSpace;

	/** @var String */
	public $addedToWaitingList;

	/** @var String */
	public $overbooking;

	/** @var String */
	public $lastBookingChangedItem;

	/** @var String */
	public $isUsingNewBookingEngine;

	/** @var core_bookingengine_data_Booking */
	public $booking;

	/** @var core_bookingengine_data_BookingItem */
	public $item;

	/** @var core_bookingengine_data_BookingItemType */
	public $type;

	/** @var String */
	public $maxNumberOfGuests;

	/** @var String */
	public $paidFor;

	/** @var String */
	public $forceAccess;

	/** @var String */
	public $printablePrice;

}
?>