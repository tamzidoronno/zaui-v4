<?php
class core_storemanager_data_Store extends core_common_DataCommon  {
	/** @var String */
	public $webAddress;

	/** @var String */
	public $webAddressPrimary;

	/** @var String */
	public $additionalDomainNames;

	/** @var String */
	public $registeredDomain;

	/** @var String */
	public $readIntroduction;

	/** @var String */
	public $isExtendedMode;

	/** @var core_storemanager_data_StoreConfiguration */
	public $configuration;

	/** @var String */
	public $isVIS;

	/** @var String */
	public $isDeepFreezed;

	/** @var String */
	public $deepFreezePassword;

	/** @var String */
	public $isTemplate;

	/** @var core_usermanager_data_User */
	public $registrationUser;

}
?>