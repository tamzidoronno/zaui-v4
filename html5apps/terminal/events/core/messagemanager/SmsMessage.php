<?php
class core_messagemanager_SmsMessage extends core_common_DataCommon  {
	/** @var String */
	public $to;

	/** @var String */
	public $from;

	/** @var String */
	public $message;

	/** @var String */
	public $prefix;

	/** @var String */
	public $status;

	/** @var String */
	public $response;

	/** @var String */
	public $smsHander;

	/** @var String */
	public $outGoing;

}
?>