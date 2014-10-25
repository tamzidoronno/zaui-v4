<?php
class core_hotelbookingmanager_BookingReference extends core_common_DataCommon  {
	/** @var String */
	public $bookingReference;

	/** @var String */
	public $startDate;

	/** @var String */
	public $endDate;

	/** @var String */
	public $language;

	/** @var String */
	public $codes;

	/** @var String */
	public $roomIds;

	/** @var String */
	public $isApprovedForCheckIn;

	/** @var core_hotelbookingmanager_ContactData */
	public $contact;

	/** @var String */
	public $bookingFee;

	/** @var String */
	public $updateArx;

	/** @var String */
	public $sentWelcomeMessages;

	/** @var String */
	public $failed;

}
?>