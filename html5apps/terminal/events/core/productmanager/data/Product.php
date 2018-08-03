<?php
class core_productmanager_data_Product extends core_common_DataCommon  {
	/** @var java.util.Map */
	public $images;

	/** @var String */
	public $imagesAdded;

	/** @var String */
	public $descriptions;

	/** @var String */
	public $selectedProductTemplate;

	/** @var String */
	public $currentSelectedProducTemplate;

	/** @var String */
	public $description;

	/** @var String */
	public $shortDescription;

	/** @var String */
	public $mainImage;

	/** @var String */
	public $price;

	/** @var String */
	public $discountedPrice;

	/** @var String */
	public $overriddenPrice;

	/** @var String */
	public $tag;

	/** @var String */
	public $minPeriode;

	/** @var String */
	public $progressivePriceModel;

	/** @var String */
	public $dynamicPriceInPercent;

	/** @var core_productmanager_data_ProductDynamicPrice[] */
	public $prices;

	/** @var String */
	public $campaign_price;

	/** @var String */
	public $original_price;

	/** @var String */
	public $name;

	/** @var String */
	public $uniqueName;

	/** @var String */
	public $stockQuantity;

	/** @var String */
	public $pageId;

	/** @var String */
	public $freeShipping;

	/** @var String */
	public $promoted;

	/** @var String */
	public $hideShippingPrice;

	/** @var String */
	public $taxgroup;

	/** @var String */
	public $privateExcluded;

	/** @var String */
	public $incrementalProductId;

	/** @var String */
	public $accountingSystemId;

	/** @var String */
	public $accountingAccount;

	/** @var java.util.HashMap */
	public $addedAttributes;

	/** @var String */
	public $groupPrice;

	/** @var String */
	public $categories;

	/** @var String */
	public $subProductIds;

	/** @var core_productmanager_data_TaxGroup */
	public $taxGroupObject;

	/** @var core_pagemanager_data_Page */
	public $page;

	/** @var core_productmanager_data_Product[] */
	public $subProducts;

	/** @var String */
	public $attributesToSave;

	/** @var String */
	public $isGroupedProduct;

	/** @var String */
	public $isFood;

	/** @var String */
	public $weight;

	/** @var String */
	public $supplier;

	/** @var String */
	public $cost;

	/** @var String */
	public $simpleAppsAdded;

	/** @var String */
	public $taxes;

	/** @var String */
	public $sku;

	/** @var String */
	public $metaData;

	/** @var String */
	public $additionalMetaData;

	/** @var String */
	public $externalReferenceId;

	/** @var String */
	public $isNotRecurring;

	/** @var String */
	public $priceExTaxes;

	/** @var String */
	public $variationCombinations;

	/** @var core_listmanager_data_JsTreeList */
	public $variations;

}
?>