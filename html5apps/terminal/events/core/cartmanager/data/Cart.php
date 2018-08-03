<?php
class core_cartmanager_data_Cart extends core_common_DataCommon  {
	/** @var core_cartmanager_data_CartItem[] */
	public $items;

	/** @var String */
	public $shippingCost;

	/** @var String */
	public $isShippingFree;

	/** @var core_productmanager_data_TaxGroup */
	public $shippingTax;

	/** @var String */
	public $reference;

	/** @var String */
	public $references;

	/** @var String */
	public $createByGetShopModule;

	/** @var core_usermanager_data_Address */
	public $address;

	/** @var core_cartmanager_data_Coupon */
	public $coupon;

	/** @var String */
	public $couponCost;

}
?>