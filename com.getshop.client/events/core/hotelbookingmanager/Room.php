<?php
class core_hotelbookingmanager_Room extends core_common_DataCommon  {
	/** @var String */
	public $productId;

	/** @var String */
	public $currentCode;

	/** @var String */
	public $roomName;

	/** @var String */
	public $isActive;

	/** @var String */
	public $isClean;

	/** @var core_hotelbookingmanager_BookingReference */
	public $lastReservation;

	/** @var String */
	public $lockId;

}
?>