<?php
class core_pmsmanager_PmsPricing extends core_common_DataCommon  {
	/** @var String */
	public $code;

	/** @var String */
	public $defaultPriceType;

	/** @var String */
	public $dailyPrices;

	/** @var java.util.HashMap */
	public $progressivePrices;

	/** @var String */
	public $pricesExTaxes;

	/** @var String */
	public $privatePeopleDoNotPayTaxes;

	/** @var String */
	public $channelDiscount;

	/** @var String */
	public $derivedPrices;

	/** @var String */
	public $derivedPricesChildren;

	/** @var String */
	public $price_mon;

	/** @var String */
	public $price_tue;

	/** @var String */
	public $price_wed;

	/** @var String */
	public $price_thu;

	/** @var String */
	public $price_fri;

	/** @var String */
	public $price_sat;

	/** @var String */
	public $price_sun;

	/** @var String */
	public $productPrices;

	/** @var String */
	public $longTermDeal;

	/** @var String */
	public $coveragePrices;

	/** @var String */
	public $coverageType;

}
?>