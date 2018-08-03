<?php
class core_accountingmanager_AccountingTransferConfig extends core_common_DataCommon  {
	/** @var String */
	public $delay;

	/** @var String */
	public $includeUsers;

	/** @var core_accountingmanager_AccountingTransferConfigTypes[] */
	public $paymentTypes;

	/** @var core_accountingmanager_TransferFtpConfig */
	public $ftp;

	/** @var String */
	public $transferType;

	/** @var String */
	public $orderFilterPeriode;

	/** @var String */
	public $subType;

	/** @var String */
	public $paymentTypeCustomerIds;

	/** @var String */
	public $username;

	/** @var String */
	public $password;

	/** @var String */
	public $postingDateType;

	/** @var String */
	public $startCustomerCodeOffset;

}
?>