<?php
namespace ns_fb076580_c7df_471c_b6b7_9540e4212441;

class OpenSRS extends \SystemApplication implements \Application {

    public $singleton = true;

    public function getDescription() {
        return "OpenSRS is a domain name API.";
    }

    public function getName() {
        return $this->__("Domain name");
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function removeDomainName() {
        $this->getApi()->getStoreManager()->removeDomainName($_POST['data']['domain']);
        $this->getFactory()->reloadStoreObject();
    }

    public function createSuggestions($domain) {
        include_once("opensrs_php/opensrs/openSRS_loader.php");
        $callArray = array(
            "func" => "suggestDomain",
            "data" => array(
                "domain" => $domain,
                "selected" => ".no;.net",
                "maximum" => 3,
                "alldomains" => ".no;.com;.net;.org;.ca")
        );

        $callstring = json_encode($callArray);
        $osrsHandler = processOpenSRS("json", $callstring);
        $data = json_decode($osrsHandler->resultFormated, true);
        echo "<table width='100%'>";
        foreach ($data as $id => $arr) {
            echo "<tr>";
            $price = $this->getPrice($arr['domain']);

            echo "<td width='100%'>" . $arr['domain'] . "</td>";
            echo "<td width='80'>$" . $price . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
    public function savePrimaryDomain() {
        $this->getApi()->getStoreManager()->setPrimaryDomainName($_POST['data']['domain']);
        $this->getFactory()->reloadStoreObject();
        
        $content = "Webshop: " . $this->getFactory()->getStore()->webAddress . " (" . $this->getFactory()->getStore()->configuration->emailAdress . ")";
        $title = "Domain name registered: " . $_POST['data']['domain'];
        $to = "post@getshop.com";
        $toName = "Post Getshop";
        $from = "system@getshop.com";
        $fromName = "GetShop System";
        $this->getApi()->getMessageManager()->sendMail($to, $toName, $title, $content, $from, $fromName);
    }

    private function parseResult($resString) {
        $resultReturn = "";

        if ($resString != "") {
            $temArra = explode(" ", $resString);
            return $temArra[0];
        } else {
            $resultReturn = "Read error";
        }

        return $temArra[0];
    }

    public function checkDomain() {
        include_once("opensrs_php/opensrs/openSRS_loader.php");
        $domain = $_POST['data']['domain'];

        $check = new \openSRS_fastlookup();
        $res = $check->checkDomain($domain);
        $res = $this->parseResult($res);
        if ($res == 211) {
            echo "<div class='not_available'>";
            echo $domain . " " . $this->__f("is not available, please try another one.");
            echo "</div>";
        } else {
            $price = $this->getPrice($domain);
            if ($price == 0) {
                echo "<div class='not_available'>";
                echo $domain . " " . $this->__f("is not a valid domain name.");
                echo "</div>";
            } else {
                echo "<div class='available'>";
                echo $domain . " " . $this->__f("is available.");
                echo "<span class='price'>$" . $price . "/year</span>";
                echo "</div>";
                echo "<script>";
                echo "thundashop.app.opensrs.display_contact_information($price);";
                echo "</script>";
            }
        }
    }

    public function getPrice($domain) {

        $callArray = array(
            "func" => "lookupGetPrice",
            "data" => array(
                "domain" => $domain
            )
        );

        $callstring = json_encode($callArray);
        $osrsHandler = processOpenSRS("json", $callstring);
        $data = json_decode($osrsHandler->resultFormated, true);
        $price = round($data['price'] * 1.2);

        return $price;
    }

    public function render() {
        include_once("opensrs_php/opensrs/openSRS_loader.php");
        echo '<div class="OpenSRS OpenSRS_inner" appid="dd">';
        echo "<table width='90%'>";
        echo "<tr>";
        echo "<td width='200' valign='top'>";
        $this->includefile("domainmenu");
        echo "</td><td valign='top'>";
        if (isset($_GET['type']))
            $type = $_GET['type'];
        else
            $type = "";
        
        switch($type) {
            case "register":
                $this->includefile("registrationpage");
                break;
            case "transfer":
                $this->includefile("transfer");
                break;
            default:
                $this->includefile("overview");
                
        }
        echo "</td>";
        echo "</table>";
        echo '</div>';
    }

    public function saveContactData() {
        
        $data = $_POST['data'];
        $storeConf = $this->getFactory()->getStoreConfiguration();
        $storeConf->phoneNumber = $data['phone'];
        $storeConf->emailAdress = $data['email'];
        $storeConf->shopName = $data['org_name'];
        $storeConf->streetAddress = $data['address'];
        $storeConf->postalCode = $data['postal_code'];
        $storeConf->contactFirstName = $data['firstname'];
        $storeConf->contactLastName = $data['lastname'];
        $storeConf->city = $data['city'];
        $storeConf->state = $data['state'];
        $storeConf->country = $data['country'];
        
        $this->getApi()->getStoreManager()->saveStore($storeConf);
        $this->savePrimaryDomain();
        echo "OK";
        
    }
    
    public function registerDomainName($username, $password, $domain, $firstName, $lastName, $phone, $email, $org_name, $address, $postal_code, $city, $state, $country) {
        $res = array();
        $res['func'] = "provSWregister";
        $res['data'] = array();
        $res['data']['domain'] = $domain;
        $res['data']['custom_tech_contact'] = "0";
        $res['data']['custom_nameservers'] = "0";
        $res['data']['period'] = "1";
        $res['data']['reg_username'] = $username;
        $res['data']['reg_password'] = $password;
        $res['data']['reg_type'] = "new";
        $res['data']['handle'] = "process";

        $res['personal'] = array();
        $res['personal']['first_name'] = $firstName;
        $res['personal']['last_name'] = $lastName;
        $res['personal']['phone'] = $phone;
        $res['personal']['fax'] = "";
        $res['personal']['email'] = $email;
        $res['personal']['org_name'] = $org_name;
        $res['personal']['address1'] = $address;
        $res['personal']['address2'] = "";
        $res['personal']['address3'] = "";
        $res['personal']['postal_code'] = $postal_code;
        $res['personal']['city'] = $city;
        $res['personal']['state'] = $state;
        $res['personal']['country'] = $country;
        $res['personal']['lang_pref'] = "EN";

        $callstring = json_encode($res);
        print_r($callstring);
        $osrsHandler = processOpenSRS("json", $callstring);
        $data = json_decode($osrsHandler->resultFormated, true);
        print_r($data);
    }

}

?>
