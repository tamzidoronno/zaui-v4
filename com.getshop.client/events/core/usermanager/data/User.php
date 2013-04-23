<?php
class core_usermanager_data_User extends core_common_DataCommon  {
	/** @var String */
	public $resetCode;

	/** @var core_usermanager_data_Address */
	public $address;

	/** @var String */
	public $fullName;

	/** @var String */
	public $emailAddress;

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
	public $key;

	/** @var String */
	public $userAgent;

	/** @var String */
	public $hasChrome;

}
?>