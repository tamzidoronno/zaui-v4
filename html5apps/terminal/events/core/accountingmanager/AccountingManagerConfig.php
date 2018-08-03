<?php
class core_accountingmanager_AccountingManagerConfig extends core_common_DataCommon  {
	/** @var String */
	public $statesToInclude;

	/** @var java.util.HashMap */
	public $configrations;

	/** @var String */
	public $vendor;

	/** @var String */
	public $username;

	/** @var String */
	public $password;

	/** @var String */
	public $hostname;

	/** @var String */
	public $path;

	/** @var String */
	public $invoice_path;

	/** @var String */
	public $extension;

	/** @var String */
	public $useSftp;

	/** @var String */
	public $useActiveMode;

	/** @var String */
	public $transferAllUsersConnectedToOrders;

	/** @var String */
	public $port;

}
?>