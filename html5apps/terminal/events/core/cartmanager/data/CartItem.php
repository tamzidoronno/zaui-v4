<?php
class core_cartmanager_data_CartItem {
	/** @var String */
	public $cartItemId;

	/** @var String */
	public $variations;

	/** @var core_productmanager_data_Product */
	public $product;

	/** @var String */
	public $count;

	/** @var String */
	public $startDate;

	/** @var String */
	public $endDate;

	/** @var String */
	public $newStartDate;

	/** @var String */
	public $newEndDate;

	/** @var String */
	public $periodeStart;

	/** @var String */
	public $groupedById;

	/** @var String */
	public $addedBy;

	/** @var String */
	public $removedAfterDeleted;

	/** @var core_pmsmanager_PmsBookingAddonItem[] */
	public $itemsAdded;

	/** @var String */
	public $priceMatrix;

	/** @var String */
	public $hideDates;

	/** @var String */
	public $disabled;

	/** @var String */
	public $addedByGetShopModule;

	/** @var String */
	public $pmsBookingId;

	/** @var String */
	public $orderId;

}
?>