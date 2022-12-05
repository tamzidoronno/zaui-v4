<?php
class zauiactivity_dto_BookingReserveRequest {
	/** @var String */
	public $productId;

	/** @var String */
	public $optionId;

	/** @var String */
	public $availabilityId;

	/** @var String */
	public $notes;

	/** @var zauiactivity_dto_UnitItemReserveRequest[] */
	public $unitItems;

}
?>