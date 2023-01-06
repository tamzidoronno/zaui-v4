<?php
class zauiactivity_dto_OctoBookingReserve {
	/** @var String */
	public $id;

	/** @var String */
	public $uuid;

	/** @var String */
	public $supplierReference;

	/** @var String */
	public $status;

	/** @var String */
	public $resellerReference;

	/** @var String */
	public $utcCreatedAt;

	/** @var String */
	public $utcUpdatedAt;

	/** @var String */
	public $utcExpiresAt;

	/** @var String */
	public $utcRedeemedAt;

	/** @var String */
	public $utcConfirmedAt;

	/** @var String */
	public $productId;

	/** @var String */
	public $freesale;

	/** @var String */
	public $availabilityId;

	/** @var String */
	public $notes;

	/** @var String */
	public $optionId;

	/** @var zauiactivity_dto_OctoProductAvailability */
	public $availability;

	/** @var zauiactivity_dto_OctoSupplierContact */
	public $contact;

	/** @var String */
	public $deliveryMethods;

	/** @var String */
	public $voucher;

	/** @var class com_thundashop_zauiactivity_dto_UnitItemReserveResponse[] */
	public $unitItems;

	/** @var zauiactivity_dto_Cancellation */
	public $cancellation;

}
?>