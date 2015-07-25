<?php
class core_messagemanager_SmsLogEntry extends core_common_DataCommon  {
	/** @var String */
	public $to;

	/** @var String */
	public $message;

	/** @var String */
	public $responseCode;

	/** @var String */
	public $errorCode;

	/** @var String */
	public $apiId;

	/** @var String */
	public $prefix;

	/** @var String */
	public $date;

	/** @var String */
	public $delivered;

	/** @var String */
	public $status;

	/** @var String */
	public $from;

	/** @var String */
	public $msgId;

	/** @var String */
	public $network;

	/** @var String */
	public $tryPoll;

}
?>