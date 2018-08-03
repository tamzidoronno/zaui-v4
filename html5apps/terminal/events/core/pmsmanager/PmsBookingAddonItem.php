<?php
class core_pmsmanager_PmsBookingAddonItem extends core_common_TranslationHandler  {
	/** @var String */
	public $addonId;

	/** @var String */
	public $date;

	/** @var String */
	public $overrideName;

	/** @var String */
	public $price;

	/** @var String */
	public $priceExTaxes;

	/** @var String */
	public $productId;

	/** @var String */
	public $addonType;

	/** @var String */
	public $addonSubType;

	/** @var String */
	public $count;

	/** @var String */
	public $isActive;

	/** @var String */
	public $isSingle;

	/** @var String */
	public $noRefundable;

	/** @var String */
	public $alwaysAddAddon;

	/** @var String */
	public $isAvailableForBooking;

	/** @var String */
	public $isAvailableForCleaner;

	/** @var String */
	public $dependsOnGuestCount;

	/** @var String */
	public $isIncludedInRoomPrice;

	/** @var String */
	public $channelManagerAddonText;

	/** @var String */
	public $bookingicon;

	/** @var String */
	public $includedInBookingItemTypes;

	/** @var String */
	public $onlyForBookingItems;

	/** @var String */
	public $displayInBookingProcess;

	/** @var String */
	public $addedBy;

	/** @var core_pmsmanager_PmsBookingAddonItemValidDateRange[] */
	public $validDates;

	/** @var String */
	public $atEndOfStay;

	/** @var String */
	public $variations;

	/** @var String */
	public $description;

	/** @var String */
	public $descriptionWeb;

	/** @var String */
	public $name;

	/** @var String */
	public $addedToRoom;

}
?>