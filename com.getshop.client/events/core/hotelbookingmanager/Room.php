<?php
class core_hotelbookingmanager_Room extends core_common_DataCommon  {
	/** @var String */
	public $bookedDates;

	/** @var String */
	public $roomType;

	/** @var String */
	public $currentCode;

	/** @var String */
	public $roomName;

	/** @var String */
	public $isActive;

	/** @var String */
	public $isClean;

	/** @var String */
	public $lastCleaning;

	/** @var core_hotelbookingmanager_BookingReference */
	public $lastReservation;

	/** @var String */
	public $cleaningDates;

	/** @var String */
	public $lockId;

}
?>