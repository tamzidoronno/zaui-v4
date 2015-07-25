<?php
class core_usermanager_data_Group extends core_common_DataCommon  {
	/** @var String */
	public $groupName;

	/** @var String */
	public $imageId;

	/** @var String */
	public $extraAddresses;

	/** @var core_usermanager_data_Address */
	public $defaultDeliveryAddress;

	/** @var core_usermanager_data_Address */
	public $invoiceAddress;

}
?>