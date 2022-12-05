<?php
class zauiactivity_dto_OctoProductAvailabilityRequestDto {
	/** @var String */
	public $productId;

	/** @var String */
	public $optionId;

	/** @var String */
	public $localDateStart;

	/** @var String */
	public $localDateEnd;

	/** @var String */
	public $currency;

	/** @var class com_thundashop_zauiactivity_dto_AvailabilityUnit[] */
	public $units;

}
?>