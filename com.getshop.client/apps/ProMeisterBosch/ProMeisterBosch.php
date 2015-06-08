<?php
namespace ns_8cc26060_eef2_48ac_8174_914f533dc7ed;

class ProMeisterBosch extends \ApplicationBase implements \Application {
    private $company;
    
    public $courses = ['Opel_Insignia_Sports_Tourer','BMW_320i_E90','DIAGNOSTIC_MB_E_CLASS','Ford_Mondeo','Renault_Megane_II','VW_Golf_V','VW_Passat_2_0_TDI'];
    
    

    
    public function getDescription() {
        return "A bosch application for ProMeister";
    }

    public function getName() {
        return "ProMeisterBosch";
    }

    public function render() {
        $this->includefile("coursesignon");
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $this->includefile("admin");
        }
    }
    
    public function hasAccess($path) {
        
        if (!isset($_SESSION['current_bosch_user']))
            return;
        
        foreach ($this->getAllCustomers() as $customer) {
            if ($customer->id == $_SESSION['current_bosch_user'] && strpos($path, $customer->referenceKey) > -1) {
                return true;
            }
        }
    }

    public function doLogin() {
        foreach ($this->getAllCustomers() as $customer) {
            if ($_POST['Username'] == $customer->username && $_POST['Password'] == $customer->password) {
                unset($_SESSION['last_bosch_ping']);
                $_SESSION['current_bosch_user'] = $customer->id;
            }
        }
    }
    
    public function setGroup() {
        if (!isset($_POST['data']['groupId'])) {
            unset($_SESSION['bosch_group']);
        } else {
            $_SESSION['bosch_group'] = $_POST['data']['groupId'];
        }
    }
    
    public function getCurrentGroup() {
        if (!isset($_SESSION['bosch_group'])) {
            return false;
        }
        return $_SESSION['bosch_group'];
    }

    public function findCompanies() {
        $name = $_POST['data']['name'];
        $result = $this->getApi()->getUtilManager()->getCompaniesFromBrReg($name);
        if (!is_array($result) || sizeof($result) == 0) {
            return;
        }

        echo "<table width='100%'>";
        foreach ($result as $company) {
            $orgnr = $company->vatNumber;
            $name = $company->name;
            echo "<tr orgnr='$orgnr' class='company_selection'>";
            echo "<td>" . $orgnr . "</td>";
            echo "<td class='selected_name'>" . $name . "</td>";
            echo "<td><span class='gs_button small select_searched_company'>" . $this->__w("select") . "</span></td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
    public function getCompanyInformation() {
        $this->setCompany($_POST['data']['vatnumber'], false);
        $this->includefile('company');
    }
    
    public function getCompany() {
        return $this->company;
    }
    
    private function setCompany($orgNumber, $fetch) {
        if ($fetch) {
            $this->company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($orgNumber);
        } else {
            $this->company = $this->getApi()->getUtilManager()->getCompanyFree($orgNumber);
        }
    }
    
    public function signOn() {
        $this->startAdminImpersonation("PageManager", "setApplicationSettings");
        $user = new \core_usermanager_data_User();
        $user->id = uniqid();
        $user->company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($_POST['data']['birthday']);
        $user->fullName = $_POST['data']['name'];
        $user->emailAddress = $_POST['data']['email'];
        $user->emailAddressToInvoice = $_POST['data']['invoiceemail'];
        $user->cellPhone = $_POST['data']['cellphone'];
        $user->birthDay = $_POST['data']['birthday'];
        $user->username = $this->getNextId();
        $user->password = rand(0,9).rand(0,9).rand(0,9).rand(0,9);
        $user->referenceKey = $this->getCurrentCourse();
        $user->secondsRemaining = ((int)$this->getCurrentHours() * 60 * 60);
        $user->group = $this->getCurrentGroup();
                
        $customers = $this->getAllCustomers();
        if (!$customers) {
            $customers = [];
        }
        
        $customers[] = $user;
        $this->setConfigurationSetting("customers", json_encode($customers));
        $this->stopImpersionation();
        
        $this->sendEmail($user);
    }
    
    public function sendEmail($user) {
        
        if ($this->getFactory()->getStore()->id == "d27d81b9-52e9-4508-8f4c-afffa2458488") {
            $this->sendMailSwedish($user);
        } else {
            $this->sendMailNorwegian($user);
        }
        
    }

    public function getNextId() {
        $counter = $this->getConfigurationSetting("idCounter");
        if (!$counter) {
            $counter = 0;
        }
        
        if ($counter < 10000) {
            $counter = 10000;
        }
        
        $counter++;
        
        $this->setConfigurationSetting("idCounter", $counter);
        return $counter; 
   }
    
    public function requestAdminRights() {
        $this->requestAdminRight("PageManager", "setApplicationSettings", $this->__o("This application stores data in the the local storage."));
    }

    public function getAllCustomers() {
        $custs = $this->getConfigurationSetting("customers");
        if (!$custs) {
            $custs = [];
        } else {
            $custs = json_decode($custs);
        }
        
        return $custs;
    }

    public function setSelectedCourse() {
        $this->setConfigurationSetting("selected_course", $_POST['data']['selected_course']);
    }
    
    public function getCurrentCourse() {
        return $this->getConfigurationSetting("selected_course");
    }
    
    public function setSelectedHours() {
        $this->setConfigurationSetting("selected_hours", $_POST['data']['selected_hours']);
    }
    
    public function getCurrentHours() {
        return $this->getConfigurationSetting("selected_hours");
    }
    
    public function getCurrentUser() {
        foreach ($this->getAllCustomers() as $customer) {
            if ($customer->id == $_SESSION['current_bosch_user']) {
                return $customer;
            }
        }
        
        return null;
    }
    
    public function ping() {
        $this->startAdminImpersonation("PageManager", "setApplicationSettings");
        $secondsPassed = 0;
        
        if (isset($_SESSION['last_bosch_ping'])) {
            $secondsPassed = time() - $_SESSION['last_bosch_ping'];
        }
        
        $_SESSION['last_bosch_ping'] = time();
        
        $user = $this->getCurrentUser();
        $user->secondsRemaining = $user->secondsRemaining - $secondsPassed;
        $this->saveUser($user);
        $this->stopImpersionation();
        echo $user->secondsRemaining;
    }
    
    function saveUser($user) {
        $saveUsers = [];
        foreach ($this->getAllCustomers() as $custom) {
            if ($custom->id == $user->id) {
                $saveUsers[] = $user;
            } else {
                $saveUsers[] = $custom;
            }
        }
        
        $this->setConfigurationSetting("customers", json_encode($saveUsers));
    }

    public function sendMailNorwegian($user) {
        $course = $user->referenceKey;
        $subject = $course;
        
        $body = "Hei ".$user->fullName;
        $body .= "<br/> ";
        $body .= "<br/> Du kan nå starte kurset ved å gå inn på følgende adresse:";
        $body .= "<br/> <a href='http://promeister.academy/bosch/$course/index.php'>http://promeister.academy/bosch/$course/index.php</a>";
        $body .= "<br/>";
        $body .= "<br/> Du har ".$this->getCurrentHours()." timer til rådighet, og antall innlogginger er ubegrenset i perioden.";
        $body .= "<br/>";
        $body .= "<br/> Brukernavn: ".$user->username;
        $body .= "<br/> Pinkode: ".$user->password;
        $body .= "<br/>";
        $body .= "<br/> Med Vennlig Hilsen";
        $body .= "<br/> ProMeister Academy";
        
        $this->getApi()->getMessageManager()->sendMail($user->emailAddress, $user->fullName, $subject, $body, "noreply@promeister.com", "GetShop");
        $this->getApi()->getMessageManager()->sendMail($user->emailAddressToInvoice, $user->fullName, $subject, $body, "noreply@promeister.com", "GetShop");
    }

    public function sendMailSwedish($user) {
        $course = $user->referenceKey;
        $subject = "Bestilling av ".$course;
        
        
        
        
        $body = "Hej ".$user->fullName;
        $body .= "<br/> ";
        $body .= "<br/> Du kan nu starta utbildningen på följande adress:";
        $body .= "<br/> <a href='http://www.promeisteracademy.se/bosch/$course/index.php'>http://www.promeisteracademy.se/bosch/$course/index.php</a>";
        $body .= "<br/>";
        $body .= "<br/> Du har ".$this->getCurrentHours()." timmars tillgång till utbildningen och kan logga ut och in så många gånger du vill. Tiden räknas bara så länge du är inloggad.";
        $body .= "<br/>";
        $body .= "<br/> Användarnamn: ".$user->username;
        $body .= "<br/> Lösenord: ".$user->password;
        $body .= "<br/>";
        $body .= "<br/> Med Vänliga Hälsningar";
        $body .= "<br/> ProMeister Academy";
        
        $this->getApi()->getMessageManager()->sendMail($user->emailAddress, $user->fullName, $subject, $body, "noreply@promeister.se", "ProMeister Academy");
        $this->getApi()->getMessageManager()->sendMail($user->emailAddressToInvoice, $user->fullName, $subject, $body, "noreply@promeister.se", "ProMeister Academy");
    }

}
?>