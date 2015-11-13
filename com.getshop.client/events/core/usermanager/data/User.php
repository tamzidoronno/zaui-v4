<?php
class core_usermanager_data_User extends core_common_DataCommon  {
	/** @var String */
	public $resetCode;

	/** @var String */
	public $privileges;

	/** @var String */
	public $lastRegisteredToken;

	/** @var String */
	public $triedToFetch;

	/** @var core_usermanager_data_Address */
	public $address;

	/** @var String */
	public $fullName;

	/** @var String */
	public $emailAddress;

	/** @var String */
	public $emailAddressToInvoice;

	/** @var String */
	public $password;

	/** @var String */
	public $username;

	/** @var String */
	public $type;

	/** @var String */
	public $loggedInCounter;

	/** @var String */
	public $lastLoggedIn;

	/** @var String */
	public $expireDate;

	/** @var String */
	public $birthDay;

	/** @var String */
	public $companyName;

	/** @var String */
	public $cellPhone;

	/** @var String */
	public $comments;

	/** @var String */
	public $key;

	/** @var String */
	public $group;

	/** @var String */
	public $userAgent;

	/** @var String */
	public $hasChrome;

	/** @var String */
	public $isTransferredToAccountSystem;

	/** @var String */
	public $referenceKey;

	/** @var String */
	public $isPrivatePerson;

	/** @var String */
	public $mvaRegistered;

	/** @var String */
	public $parents;

	/** @var core_usermanager_data_ProMeisterScoreSettings */
	public $proMeisterScoreSettings;

	/** @var String */
	public $isMaster;

	/** @var core_usermanager_data_Company */
	public $company;

	/** @var String */
	public $customerId;

	/** @var String */
	public $applicationAccessList;

	/** @var String */
	public $partnerid;

	/** @var String */
	public $appId;

	/** @var String */
	public $groups;

}
?>