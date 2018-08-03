<?php
class core_pmsmanager_PmsAdditionalTypeInformation extends core_common_DataCommon  {
	/** @var String */
	public $typeId;

	/** @var String */
	public $defaultNumberOfBeds;

	/** @var String */
	public $defaultNumberOfChildBeds;

	/** @var String */
	public $maxNumberOfBeds;

	/** @var String */
	public $maxNumberOfChildBeds;

	/** @var String */
	public $numberOfChildren;

	/** @var String */
	public $numberOfAdults;

	/** @var core_pmsmanager_PmsTypeImages[] */
	public $images;

	/** @var String */
	public $dependsOnTypeId;

	/** @var String */
	public $accessories;

}
?>