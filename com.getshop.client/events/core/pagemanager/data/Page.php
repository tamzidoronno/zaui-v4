<?php
class core_pagemanager_data_Page extends core_common_DataCommon  {
	/** @var core_pagemanager_data_Page */
	public $parent;

	/** @var String */
	public $type;

	/** @var String */
	public $userLevel;

	/** @var String */
	public $description;

	/** @var core_pagemanager_data_PageLayout */
	public $layout;

	/** @var String */
	public $title;

	/** @var String */
	public $customCss;

	/** @var core_listmanager_data_Entry */
	public $linkToListEntry;

}
?>