<?php
class core_eventbooking_Event extends core_common_DataCommon  {
	/** @var String */
	public $bookingItemId;

	/** @var String */
	public $subLocationId;

	/** @var String */
	public $eventHelderUserId;

	/** @var String */
	public $freeTextEventHelder;

	/** @var core_eventbooking_Day[] */
	public $days;

	/** @var String */
	public $markedAsReady;

	/** @var core_eventbooking_Reminder[] */
	public $reminders;

	/** @var String */
	public $isCanceled;

	/** @var String */
	public $smsReminderSent;

	/** @var String */
	public $mailReminderSent;

	/** @var String */
	public $questBackSent;

	/** @var String */
	public $isLocked;

	/** @var String */
	public $isHidden;

	/** @var String */
	public $extraInformation;

	/** @var core_calendarmanager_data_EntryComment[] */
	public $eventComments;

	/** @var String */
	public $encryptedPersonalIds;

	/** @var java.util.HashMap */
	public $comments;

	/** @var String */
	public $participationStatus;

	/** @var String */
	public $groupInvoiceStatus;

	/** @var core_bookingengine_data_BookingItem */
	public $bookingItem;

	/** @var core_bookingengine_data_BookingItemType */
	public $bookingItemType;

	/** @var core_eventbooking_Location */
	public $location;

	/** @var core_eventbooking_SubLocation */
	public $subLocation;

	/** @var String */
	public $mainStartDate;

	/** @var String */
	public $mainEndDate;

	/** @var String */
	public $eventPage;

	/** @var String */
	public $canBook;

	/** @var String */
	public $canBookWaitingList;

	/** @var String */
	public $price;

	/** @var String */
	public $isInFuture;

}
?>