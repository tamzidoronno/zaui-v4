<?php
class core_hotelbookingmanager_Room extends core_hotelbookingmanager_DomainControlledObject  {
	/** @var String */
	public $reservationParts;

	/** @var String */
	public $name;

	/** @var String */
	public $roomTypeId;

	/** @var core_hotelbookingmanager_RoomType */
	public $roomType;

}
?>