<?php
class core_pagemanager_data_Page extends core_common_DataCommon  {
	/** @var String */
	public $hideHeader;

	/** @var String */
	public $hideFooter;

	/** @var core_pagemanager_data_Page */
	public $parent;

	/** @var String */
	public $type;

	/** @var String */
	public $userLevel;

	/** @var String */
	public $description;

	/** @var String */
	public $pageAreas;

	/** @var core_pagemanager_data_PageLayout */
	public $layout;

	/** @var core_listmanager_data_Entry */
	public $linkToListEntry;

}
?>