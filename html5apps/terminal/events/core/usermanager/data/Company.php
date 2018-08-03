<?php
class core_usermanager_data_Company extends core_common_DataCommon  {
	/** @var core_usermanager_data_Address */
	public $invoiceAddress;

	/** @var core_usermanager_data_Address */
	public $address;

	/** @var String */
	public $vatNumber;

	/** @var String */
	public $type;

	/** @var String */
	public $name;

	/** @var String */
	public $prefix;

	/** @var String */
	public $phone;

	/** @var String */
	public $vatRegisterd;

	/** @var String */
	public $invoiceEmail;

	/** @var String */
	public $website;

	/** @var String */
	public $email;

	/** @var String */
	public $contactPerson;

	/** @var String */
	public $groupId;

	/** @var String */
	public $reference;

	/** @var String */
	public $description;

	/** @var String */
	public $streetAddress;

	/** @var String */
	public $postnumber;

	/** @var String */
	public $country;

	/** @var String */
	public $city;

	/** @var String */
	public $invoiceReference;

	/** @var core_usermanager_data_Company[] */
	public $subCompanies;

	/** @var String */
	public $companyLeaderUserId;

	/** @var String */
	public $needConfirmation;

}
?>