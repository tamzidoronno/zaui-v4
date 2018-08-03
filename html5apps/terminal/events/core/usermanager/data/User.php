<?php
class core_usermanager_data_User extends core_common_DataCommon  {
	/** @var String */
	public $resetCode;

	/** @var core_usermanager_data_UserPrivilege[] */
	public $privileges;

	/** @var core_usermanager_data_UserCard[] */
	public $savedCards;

	/** @var core_usermanager_data_UploadedFiles[] */
	public $files;

	/** @var String */
	public $lastRegisteredToken;

	/** @var String */
	public $triedToFetch;

	/** @var String */
	public $suspended;

	/** @var String */
	public $visibleOnlyInMainCompany;

	/** @var String */
	public $primaryCompanyUser;

	/** @var String */
	public $showExTaxes;

	/** @var String */
	public $lastBooked;

	/** @var String */
	public $lastOrdered;

	/** @var String */
	public $orderAmount;

	/** @var java.util.HashMap */
	public $oAuths;

	/** @var String */
	public $hasAccessToModules;

	/** @var String */
	public $userRoleIds;

	/** @var String */
	public $mainCompanyId;

	/** @var core_usermanager_data_Company */
	public $companyObject;

	/** @var String */
	public $virtual;

	/** @var String */
	public $useGroupId;

	/** @var String */
	public $externalAccountingId;

	/** @var String */
	public $internalPassword;

	/** @var core_usermanager_data_UserCompanyHistory[] */
	public $companyHistory;

	/** @var String */
	public $annotionsAdded;

	/** @var String */
	public $createInSendRegning;

	/** @var String */
	public $autoConfirmBookings;

	/** @var String */
	public $totpKey;

	/** @var String */
	public $lastTotpVerificationCodeUsed;

	/** @var String */
	public $passwordResetCode;

	/** @var String */
	public $emailVerified;

	/** @var core_pmsmanager_AdditionalCrmData */
	public $additionalCrmData;

	/** @var core_usermanager_data_Address */
	public $address;

	/** @var String */
	public $fullName;

	/** @var String */
	public $emailAddress;

	/** @var String */
	public $relationship;

	/** @var String */
	public $emailAddressToInvoice;

	/** @var String */
	public $prefix;

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
	public $prevLoggedIn;

	/** @var String */
	public $expireDate;

	/** @var String */
	public $birthDay;

	/** @var String */
	public $cellPhone;

	/** @var java.util.HashMap */
	public $comments;

	/** @var String */
	public $metaData;

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
	public $accountingId;

	/** @var String */
	public $referenceKey;

	/** @var String */
	public $pinCode;

	/** @var String */
	public $isCompanyOwner;

	/** @var String */
	public $isCompanyMainContact;

	/** @var String */
	public $wantToBecomeCompanyOwner;

	/** @var String */
	public $preferredPaymentType;

	/** @var String */
	public $enabledPaymentOptions;

	/** @var String */
	public $invoiceDuePeriode;

	/** @var String */
	public $sessionTimeOut;

	/** @var String */
	public $canChangeLayout;

	/** @var String */
	public $smsDisabled;

	/** @var String */
	public $couponId;

	/** @var String */
	public $profilePicutreId;

	/** @var String */
	public $company;

	/** @var String */
	public $subUsers;

	/** @var String */
	public $customerId;

	/** @var String */
	public $discount;

	/** @var String */
	public $showHiddenFields;

	/** @var String */
	public $showLoguotCounter;

	/** @var String */
	public $applicationAccessList;

	/** @var core_usermanager_data_User[] */
	public $subUserList;

	/** @var String */
	public $partnerid;

	/** @var String */
	public $appId;

	/** @var String */
	public $groups;

}
?>