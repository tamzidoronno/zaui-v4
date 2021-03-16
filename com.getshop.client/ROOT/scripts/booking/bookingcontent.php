<?php

// connect to framework
chdir("../../");
include '../loader.php';

header('Access-Control-Allow-Origin: *');

// get contents from GslBooking app
$content = file_get_contents("../apps/GslBooking/template/gslfront_1.phtml");

// find out where we want getshop_endpoint to point to - only used to fetch the javascript

/* @var $this \ns_81edf29e_38e8_4811_a2c7_bc86ad5ab948\GslBooking */
$factory = IocContainer::getFactorySingelton();
$prod = $factory->getApi()->getStoreManager()->isProductMode();
$endpoint = "https://" . $_SERVER['SERVER_NAME'];

# NOT SURE WHY WE NEED THIS; makes it impossible to test on cluster 9 and not sure if www.getshop.com
# gets a new version of this file when deploying to the repective clusters.... maybe deploy to c3 would be needed
#if($prod || $_SERVER['SERVER_NAME'] == "www.getshop.com") {
#    $endpoint = "https://www.getshop.com/";
#}
$content = str_replace("{getshop_endpoint}", $endpoint, $content);

//add country select option for customers
if( $_GET['display_countryselect'] )
{
    $cs = '';
    $cs .= '                    <div id="gsCountrySelectContainer" style="display:none; padding:10px 0;">';
    $cs .= "                        <div><span gstype='bookingtranslation' gstranslationfield='country'></span></div>";
    $cs .= '                        <select id="gsCountrySelect" class="gsniceselect1" gsname="countryCode">';
    $cs .= '                            <option value="" gstype="bookingtranslation" gstranslationfield="country_please_select"></option>';

    foreach (CountryCodes::getCodes() as $code => $country) {
        $cs .= "<option value='$code'>$country</option>";
    }
    $cs .= '                        </select>';
    $cs .= '                        <input type="hidden" gsname="user_address_countrycode" value="" />';
    $cs .= '                        <input type="hidden" gsname="user_address_countryname" value="" />';
    $cs .= '                        <input type="hidden" gsname="company_address_countrycode" value="" />';
    $cs .= '                        <input type="hidden" gsname="company_address_countryname" value="" />';
    $cs .= '                    </div>';

    $content = str_replace('<!--GS_COUNTRY_LIST-->', $cs, $content);
}

echo $content;
?>