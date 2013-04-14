<?php
class core_usermanager_events_CreateUser extends core_common_MessageBase  {
	/** @var String */
	public $autoGenerateUserName;

	/** @var core_usermanager_data_User */
	public $user;

}
?>