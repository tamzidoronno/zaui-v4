<?php
class core_usermanager_events_ResetPassword extends core_common_MessageBase  {
	/** @var String */
	public $email;

	/** @var String */
	public $confirmCode;

	/** @var String */
	public $newPassword;

}
?>