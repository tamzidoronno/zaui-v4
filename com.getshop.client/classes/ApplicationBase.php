<?php

class ApplicationBase extends FactoryBase {
    public $pageSingleton = false;
    private $events = array();
    private $skinVariables;
    private $cell;
    private $depth;
    private $wrappedAppes;
    private $parentWrappedApp;
    private $wrapAppRefernce;
    private $modulePageId;
    
    
    /** @var core_common_AppConfiguration */
    public $configuration;
    
    /** @var core_applicationmanager_Application */
    public $applicationSettings;
    
    public function getYoutubeId() {
        return false;
    }
    
    public function sortGetShopTable() {
        
    }
    
    public function getPageIdModule() {
        if(isset($this->page) && $this->page->getId()) {
            return $this->page->getId();
        }
    }
    
    public function clearSessionOnIdentifierForTable() {
        $identifier = $_POST['data']['functioname'];
        unset($_SESSION['gs_moduletable_'.$identifier]);
    }
    
    public function indexList($list) {
        $list2 = array();
        
        if (!is_array($list)) {
            $list = array();
        }
        
        foreach($list as $l) {
            $list2[$l->id] = $l;
        }
        return $list2;
    }
    
    public function autosavetext() {
        $key = 'autosaved_'.$this->getConfiguration()->id . "_" . $_POST['data']['name'];
        $_SESSION[$key] = $_POST['data']['value'];
    }
    
    public function clearAutoSavedText() {
        foreach($_SESSION as $key => $value) {
            if(stristr($key, "autosaved_")) {
                unset($_SESSION[$key]);
            }
        }
    }

    public function printCell($areaname) {
        $page = new \Page($this->getPage()->javapage, $this->getFactory());
        $pageId = $this->getPage()->javapage->id;
        $cell = $this->getApi()->getPageManager()->getLooseCell($pageId, $areaname);
        
        $page->printCell($cell, 0, 1, 0, false, null, false);
    }
    
    public function getAutoSaved($name) {
        $key = 'autosaved_'.$this->getConfiguration()->id . "_" . $name;
        if(isset($_SESSION[$key])) {
            return $_SESSION[$key];
        }
        return "";
    }
    
    public function postProcess() {
    }

    public function getApplications() {
        
    }
    
    public function getAppInstanceId() {
//        print_r($this->getConfiguration());
        return $this->getConfiguration()->id;
    }
    
    public function startAdminImpersonation($managerName, $function) {
        $userToImersonate = $this->getCredentials($managerName, $function);
        if ($userToImersonate != null) {
            $this->startImpersionation($userToImersonate[4], $userToImersonate[5]);
        }
    }
    
    private function getCredentials($managerName, $function) {
        $namespace = $this->getFactory()->convertUUIDtoString($this->getApplicationSettings()->id);
        $privateFolder = "../../private/$namespace/private";
        $passwordFile = $privateFolder."/password";
        if (file_exists($passwordFile)) {
            $contents = file_get_contents($passwordFile);
            foreach (explode("\n", $contents) as $content) {
                
                $content2 = explode(";", $content);
                if (count($content2) < 3) {
                    continue;
                }
                
                if ($content2[0] == $managerName 
                        && $content2[1] == $function 
                        && $content2[2] == $this->getFactory()->getStore()->id 
                        && $content2[3] == $this->getConfiguration()->id) {
                    return $content2;
                }
            }
        }
        
        return null;
    }
    
    public function requestAdminRight($managerName, $function, $descriptions) {
        $user = $this->getApi()->getUserManager()->requestAdminRight($managerName, $function, $this->getConfiguration()->id);
        $username = $user->username;
        $password = $user->password;
        $storeId = $this->getConfiguration()->storeId;
        $appId = $this->getConfiguration()->id;
        $saveString = "$managerName;$function;$storeId;$appId;$username;$password";
        $this->addToPasswordStore($managerName, $function, $saveString, $storeId, $appId);
    }
    
    private function addToPasswordStore($managerName, $function, $saveString, $storeId, $appId) {
        $namespace = $this->getFactory()->convertUUIDtoString($this->getApplicationSettings()->id);
        $privateFolder = "../../private/$namespace/private";
        
        if (!file_exists($privateFolder)) {
            $success = mkdir($privateFolder, 0777, true);
            if (!$success) {
                echo "UnExpected error 2001230919192039212451597 .. Contact support for more information: ".$privateFolder;
                die();
            }
        }
        
        $passwordFile = $privateFolder."/password";
        if (!file_exists($passwordFile)) {
            $success = touch($passwordFile);
            if (!$success) {
                echo "UnExpected error 2001230919192039212451598 .. Contact support for more information";
                die();
            }
        }
        
        $addContent = "";
        $contents = file_get_contents($passwordFile);
        $result = "";
        foreach (explode("\n", $contents) as $content) {
            $content2 = explode(";", $content);
            if (count($content2) < 3) {
                continue;
            }
            if ($content2[0] == $managerName && $content2[1] == $function && $appId == $content2[3] && $storeId == $content2[2]) {
                continue;
            }
            $addContent .= $content."\n";
        }
        
        $addContent .= $saveString;
        file_put_contents($passwordFile, $addContent."\n");
    }
    
    /**
     * Returns a array with the following information
     * 
     * @return ['font-awesome-icon', 'javascript.function.to.ChartDrawer', 'title'];
     */
    public function getDashboardChart($year=false) {
        return false;
    }
    
    public function renderDashBoardWidget() {
        return false;
    }
    
    public function convertToJavaDate($time) {
        $res = date("c", $time);
        return $res;
    }
    
    /**
     * This function should be overridden when 
     * an application need to ask for admin rights.
     * 
     * This function is executed after application has been added.
     */
    public function requestAdminRights() {
        
    }
    
    
    public function setSkinVariable($variableName, $defaultValue, $description) {
        $this->skinVariables[$variableName] = $defaultValue;
    }
    
    public function showSettings() {
        return $this->includefile("settings");
    }
    
    public function saveAppInstanceSettings() {
        foreach($_POST['data'] as $key => $val) {
            if(!stristr($key, "setting_")) {
                continue;
            }
            $key = str_replace("setting_", "", $key);
            $this->setConfigurationSetting($key, $val);
        }
    }
    
    public function getSkinVariable($variableName) {
        return $this->skinVariables[$variableName];
    }
    
    public function setSkinVariables($skinVaribles) {
        $this->skinVariables = $skinVaribles;
    }
    
    public function getSkinVariables() {
        return $this->skinVariables;
    }
    
    public function preProcess() {
    }
    
    public function showDescription() {
        $this->includefile('applicationdescription', 'Common');
    }
    
    public function renderStandAlone() {
        $this->renderApplication(true);
    }

    public function renderApplication($appNotAddedToPage=false, $fromApplication=false, $fastRender = false) {
        $changeable = '';
        $appSettingsId = $this->getApplicationSettings() ? $this->getApplicationSettings()->id : "";
        $id = isset($this->configuration) ? $this->configuration->id : "";
        $callbackInstance = "";
        $normalId = "";
        
        
        if ($appNotAddedToPage) {
            $id = get_class($this);
            $arrId = explode("\\", $id);
            $normalId = str_replace("_", "-", str_replace("ns_", "",$arrId[0]));
        }
        
        $fromId = "";
        if ($fromApplication) {
            $fromId = isset($fromApplication->configuration) ? $fromApplication->configuration->id : "";
            if(!$fromId) {
                $fromId = get_class($fromApplication);
            }
            $callbackInstance = " fromapplication='$fromId' ";
        }
        
        if (isset($_GET['onlyShowApp']) && isset($id) && $id != $_GET['onlyShowApp']) {
            return;
        }
        
        $className = get_class($this);
        if(strrpos($className, "\\")) {
            $className = substr($className, strrpos($className, "\\")+1);
            $_SESSION['cachedClasses'][$fromId] = get_class($this);
            $_SESSION['cachedClasses'][$id] = get_class($this);
        } else {
            $_SESSION['cachedClasses'][$fromId] = $fromId;
            $_SESSION['cachedClasses'][$id] = $id;
        }

        if(!isset($_SESSION['cachedClasses'])) {
            $_SESSION['cachedClasses'] = array();
        }

        echo "<div $callbackInstance appid='$id' ".$this->getExtraAttributesToAppArea()." app='" . $className . "' class='app $changeable " . $className . "' appid2='$normalId' appsettingsid='$appSettingsId'>";
        if(!$fastRender && $this->isEditorMode() && !$this->getFactory()->isMobile()) {
            echo "<div class='mask'><div class='inner'>".$this->__f("Click to delete")."</div></div>";
            echo "<div class='order_mask'>";

            echo "</div>";
        }
        
        echo "<div class='applicationinner'>";
        if(!$fastRender && $this->isEditorMode() && !$changeable && !$this->getFactory()->isMobile()) {
            if($this->hasWriteAccess()) {
                echo "<div class='application_settings inline gs_icon'><i class='fa fa-cog' style='font-size:18px;'></i></div>";
            }
        }
        $this->render();
        echo "</div>";
        echo "</div>";
    }
    
    public function renderApp($appNotAddedToPage=false, $fromApplication=false) {
        $changeable = '';
        $appSettingsId = $this->getApplicationSettings() ? $this->getApplicationSettings()->id : "";
        $id = isset($this->configuration) ? $this->configuration->id : "";
        $callbackInstance = "";
        
        if ($appNotAddedToPage) {
            $id = get_class($this);
        }
        $fromId = "";
        if ($fromApplication) {
            $fromId = isset($fromApplication->configuration) ? $fromApplication->configuration->id : "";
            if(!$fromId) {
                $fromId = get_class($fromApplication);
            }
            $callbackInstance = " fromapplication='$fromId' ";
        }
        
        if (isset($_GET['onlyShowApp']) && isset($id) && $id != $_GET['onlyShowApp']) {
            return;
        }
        
        $className = get_class($this);
        if(strrpos($className, "\\")) {
            $className = substr($className, strrpos($className, "\\")+1);
            $_SESSION['cachedClasses'][$fromId] = get_class($this);
            $_SESSION['cachedClasses'][$id] = get_class($this);
        } else {
            $_SESSION['cachedClasses'][$fromId] = $fromId;
            $_SESSION['cachedClasses'][$id] = $id;
        }
        
        if(!isset($_SESSION['cachedClasses'])) {
            $_SESSION['cachedClasses'] = array();
        }

        $hasInstance = $this->getConfiguration() ? "yes" : "no";
        $inData = json_encode($_GET);
        
        $ignoreGsTyeps = $this->disableGsTypes() ? "gstypes_disabled='true'" : "";
        echo "<div $ignoreGsTyeps $callbackInstance appid='$id' hasinstance='$hasInstance' app='" . $className . "' class='app $changeable " . $className . "' appsettingsid='$appSettingsId' gs_getvariables='$inData'>";

        echo "<div class='applicationinner'>";
        if($appSettingsId == "1ba01a11-1b79-4d80-8fdd-c7c2e286f94c") {
            echo "<div style='height:41px'></div>";
        } else if($appSettingsId == "a5599ed1-60be-43f4-85a6-a09d5318638f") {
            echo "<div style='height:78px'></div>";
        } else {
            $dayofyear = date('z') + 1;
            if($dayofyear > 353) {
                echo "<center><img class='fa fa-spin' style='margin-top: 100px;' src='data:image/png;base64,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'></img>";
            } else {        
                echo "<center><img class='fa fa-spin' style='margin-top: 100px;' src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHQAAAB0CAYAAABUmhYnAAAABmJLR0QAAAAAAAD5Q7t/AAAACXBIWXMAAC4jAAAuIwF4pT92AAAAB3RJTUUH4gkLDAombLFhSgAAEKVJREFUeNrtnXtwVFWexz/n3tuddDqPzoNADAFFEAakEN/oCIMOPhB846wrMFvqOLWyOr52192yaqZ2p3at2tWpsXZHp5zZmXFURAVnMSiDL1CUx+IDMAKCoyTkQUKe3Uk63fees390J+lgErrTr5u2T9WppDvn3Nw+n/P9nd/vd8+9LRjHpa6uTiXjuFVVVWK8jonIAswswCILMbPgiizEzIIrshAzC67IgswssCILMrPAiizMzIIqsiAzC6zIgswssCILM7OgiizMzIIqsiAzC6zIwswsqCILM7OgiizMzIIqsjAzC6rIwswsqCILM7OgiizMzIKqjUeYpR92UPjUwYwGOtYxNsYbzKL1tbTfvxmA/NYefI+em9FQY1WqNp4+oGd7Gx33vz7wuueZjyg/lrX8YwaaTnUWbG6g+ba1Q9+UirabX8ya3rEATSvMt5s4/qMNw/5NNngpebM5CzUWLzedMN3bjtO0eh261DAQONDRI+ehAFHswtq7Kuv52n0Nza2upW71C6M3UqC8fUw4EswuoNEATZc68z5soXbNy9E1Dkqsus6MhxUNC1sqVN9whCO3/S6mPv7XDmXleSqg6VCnc3sTXz2wPuZ+fa9/8a0Adiommp1gqvWHOLjqd2Pr2/3tWUNHY2MbkyverePwgy+DzCYK4imGHdR57MMmOu/+Aw6bDlLe0aP01B7FCgbQXC7yz5pJd2lZ2lU6XBhjpHuw3q1TlNz+NA6l2cZguAyDhjVraN+9i6DXiyUYrICFQgFaUSGTb7qFivsfts3kE+lU5+ZWF02r/pvzPq9FR8NQGgYaBjoGIvxTCyUTRkoshIvjwkr61l8bX9z7zru0PL+Wjg8/RAqwhMISIOEbUCUq/L7CAiZfdyMz7vwR5tQz0ppsSJtCX6x38vT6vTy9/yABPS9+Va2cR98Y+3ocTuqXXk/LwQNIEWEnlABNhaa9OlkGAoVCVwKE4tjGVzm68VUmLlzEd5582r6JhWSU9zpcPLKnk5/++kV6NGdC7IxxZsmYuhZ/fojG+Rdj1hxAlyF+OqCr0ODoSkT8HlEBHYHob0PovePvbeOjFddT5HCkH2gqzO3aOp3bNzcy5+Bhirp8KBH/PjXhycU4uzx2mHs+pWn5CmhtGwQlB4FpKgxYjgRVoSMGwYeh+g5/QfWF8yjs6055CJNShe705vHI9hYAlm7bTVA3ErLt0LmgiibNF1OfsgOHab1xJbop0eRJsKJQaujvIYjaSUrVAKu7mzeWXE6R2ZeZJvelOoNbq48hETgDAQq9iZu9vb9eHFP7ku27OHHljSE4MqKeBHWIUuVQpRpD2gyv1GBXF69feTkFPd7UA02mud3ldfPw9hZUWI+5UlHW0ZWQYxc9vTym9hO+qqXttjuH+Df9QEdVaniwTja/oylVB4JtbWy6/LKkKjWSXdIV+lqjkxXVdUOcRIeUFPniV6h+Vint11ZEr8z3d9KycOmwsVvUSmU0pTKsUqW/jz8vvzolSk0q0PfbXazZenxAmaFQQGEoyDMDcR8/+PbN0Svzy69p++u7Rg3Io1HqcFAHlNrfJqxULQKqv7GJ6ssuxiOTm3M2kmVut3vzWbm5dijMgdFTCKy4vNrymr+jUUZntkt2f0zLzaujyrLo8qTp3j8yMvRaCDBVuLEKB6gChB90FxRPN8kplhhuiSMv1M3fDX6v4MQxnU1LlrL0jY10Ol1JSQUmJbGwL1DIbdVHGWmHiwJkHDmNWGCW7vuc1ltWRz9ZooCqRyYalEA4JJOXBahc3IcMjPCxVahv46EjbLzkXBbtOZA8hSa63PNmwygjJjCFwJeTi9OMzTBoHheTDt5PYzA6mGU1hzhx7a1jyoeOClUHLHCWSQrPC1Kx1A8KpAmnusJQOdfkhrN9HPqPOyn/+9/aH+ij+xRHu0ZfJwJCo70wn4ltsTkJlXvv43iUMEs+2M2J2++KJ/k0IlTVC/nzLSru8KE5QIVNsIgiOdQ/L2bO3oI8sYPOsgWJdYoSuX4epZg/1HScsp3f0GktLIh+cPOclDU+xPEokwcT6hpo+6s7wJLxZhSHeL/9jlHFXb1MeciHlhtSqzDGUB1g7PubhK+jCfVyH99zIqp2psNBe1lReGqfalQF0z69n54o4zhP9RZaLrs6YZ9pAKoFehAq/81LwWUBZACEHl9F9VDe+Lg9wxaroJy3j0aZflOKZ6+9HKc0ORXSSbUP05zjj/o8+rbviFuZw0ItKKBy2xqMConQQgpLRFXtu+wJ9GBbH10BGTXQVk8xh6ZPwRhl8M9Y90MC0owtDfjYTyl87GeJdTRmTOO0mp2cmPa39J3zL2hG2NQmoOLdT2WxZT+ge5p6Y+vg7+Of7l6JMYJG886rovvisW3z6Lr9FibseBOttDi+W5qdDvIfuAfznY00hs/TmrqaHUVTMRxhjzbeKnwof739gEZtbiPNNPC/i85HDLOWlqyI7zbBlskVyE/fp/iZJzFmTIu5f/7D91L+wRZ8D94z1AK06TSXLeYtZwl6glQqW7bYD+j+E/6Y+yjgqRuW0FXoHrIjQADytpkJOa/2qy7HfGcjk+pqKPrPfw2pdoTiWnED5bvehrrP8P3kxzRPmjBsu7MnLaNGK2CLUYqRgHWU7r32i0Nbe8e4DpgWdz78Q556ci2TW7xJe1hdEwp+cGOoAhW6geruAYdBkx6a173hekpPOu90NKH4SBaiKcUSRxtxuWFWu/0UOuZgVim6nU7uu/cHBHMcpGqHWqNl0pTrHIAZk9crBAKJC8knsoBNwVIMA7QxVqEL+wEtztHj6t/hcrHqH1fS6skfdk21U+nqrRscQAG7g0XsMgvQw95vzNVZaD+gs8ty4juAVDTn53P3T27Bm5+LvuFL2wI93PQqQoAQoYnnEpLN3WUcsVyhdTHWrFHBHPsB/V6VOwF2W9Gal8eqh1ZwtPozW8L0lDpo7vxkSJJLhLd+bvGWILXYFSo8i+0H9OLT8hJzIKU47nZz3cKLMNsN2wE90ryenmAzmghd6RWC8E9FbSCXeisHLRaVOlzgmm4/oLNKcjAStbYrRUtODpe802IrmM7C4+z+8meI8EVtxFCnPEeT/Kp+cih20KKs7rnUtzjsBzS3u4Ulp+cn1hMNwLxXu7AKytMO01FwjOpPrhri14uIwFkAQgmEgLqenOijL/cFCT3PhF5tuWNuScIHss1vseD59DpIhaW9/HnfNQMQNRG6EaL/dSRYQyj2+vKjjqfrtUfsC/Rit5crprgTPqCN3SazXulAFqZeqTmFtWz6+NwBcCIcKYe83Ah1hr1eATT4c6O7zXXqMwk/34Tv+nv+qnJy9cSne7qDiktSrNSi0h62Hvh+aFObUAMAQ49e6VeoGkir9P+5y9Tok6cY2oIl1Puvtj/QoK+D566tSsoA1/tMpq1rx/BMTDrMPE8jb9XMHVAkEepEDHq3/WqNaESv1DHVKJNaK4SqJ5Jy3knZl3uR20v1TVOTcsJBCRc+l1ylFpZ2su3gdwcHSTvJ1KJC6T+hBqhGJhospY2+GWPOZ9S3esYPUIB5OV08teS0pBy7zhtk6tr2pMF8u+bciKSBClnUMDxBhNmNcIj63wqZXTmsU6SEC3nm69Q3J+/rcpK6c35ZWS/rlifH/Erg0mofjvzEzXR3cdMQmINU5YBDJPrDlVCcEuEQDZriHF2hD3O+4jt7aPTNTap10ZL91U2X5Pv4/TWTk3LsWm+Q0575KiHHKp4QZOuBS7/JUhC+sYHwTnki4A6a2cG1VFGgmzg0OUSZ4qxt1LcWJhVmVVWVSMnthFd4uvmfqyuTqlQVR0iTW/Q1W/bPGi11FfJyNRn2ePvVGeHdhk2zAIodJs4waAkwcxsNndNSMdSpuz90SXEPL103JWlKPes3Y3uSWGm5xvuHrhi5gejPCIV2U4uB3/oVOahETYREPNvdHVamEzHrYxraK1I1zKm9g3uB28t/XZEcR8lvKRa85iWYH71Stfy/sHnf6E8tiVwj+2NOLVKdAoQ2GM4EpMZsd3cI8/QtNLRPSGnsPLB+pvJxNh/3FnD9hq+ScuxcXbBz1QxKg22jtlvf2MXp4hxkFDk6y9IwpY4pjfDvBqYE09JDry0DUwr8lsZ8dyfLyzqRs/bT0FqQMpD9vlBanoJyrsvLE4srwLISXv0Bk3N+e4B7d/bxTlsuDcKD7pmILCjnC7OQPzXm8g+7tjNDnxMVzIFEQoSHK5AD62WkLHI0ydKSNqxpm1MKM+0K7S//53Nz08uHk/o/XIYgz6GjlMLbJyh2NfCrhbeiieg3tUmlYZn9KtUxLR1TapiWhhXx+vy8ThYt+ICGJHuzUSk0HV8AfkF+N79YUgVShnKjSai9QUlrT4C2Hsm0wn2sv+o6hIhxj54avP7Zn2yIzAwpBPm6xcLzX0krTLDBs/5umWwx4YYzWbn+cJK2cCqQBlOKjvDzBWto8TuG5GejMmOaAqkQSg3JDImwF5yjmaz+7ms0dqT/uq0tnpa4qLSPp5ZNQZPBBK+pJphwhucgLyy9HV2YYzq/gRBFRKb7Qqo1tBxuvmgD7TaAOWQNTeda2l+2tvh4cFMNLb4JIGT8yvSXctWctfz4nF+EY8exf7TB9dLAkjpBqSFlDsvmr0d2V6YN4MlLpa2AAjTLz1j+bCuNXRUoMfZTcQqTBxY8zvemvkmvGf/zBC2pYZo6ljQwpYYij2Xz38DfWZxWRdoeKIDMeYEntr3HW18uoaZ2ATi6QTMHTN7wgtQgkIfHU8+iKdu5Yc46Sl0tmNIBCdiPL6VGUGpYlo6UbhbPfg56Z6Z7qE4N1C5QrdxP+OTrmwlYLn654yH2N83G2+v5Jhul4czpYWLBce447/dcNPkD/Gbin4QppcCUOkrmcs05O/G2uWwH09ZAATp5iS8a/5lcR194HXNQ11VJa89Eggo8zl6mFv8Fl8OHLqDP0jClI661cuTIRaCUm4umPws982zhAEUN1E5QlauGPV9dz+AFZcXQB5OlJnxWSnDl3Bo6TuTYFqZtwpZR3fDeOcyseCwiWBBhtYiUwdS1PBbMeMU2MGMKW+yo0tCaupePv74pLf/7+2fvo/OE2zbQRsvqaWPtmOqi++cx+7RfkrQ7gkdQ5qUzNo0bmOPC5EYWt1zGvCnPpuz/LZy1lYB31ngaolMDtZNKAZyBS5g7+TdJVaquuVg4ayu+tlJbwRr33x86Usk1FzP/9OR8lYZA59IZr9PbUTUehyb6aW4nB6m/5HkaeP/QEqTyJ2aiOCpZPHsHJ47b74vxorWUMdktO0ItLPVyqPHfOda2buyzWhhML78Pj1hjS9XFsuzFvBDZESqAp6yPmvpHaWjfEFO/OZU/Z1L+aro6g+MeZkYBHTCbLoO2wB/p7N2HP9hMX7AZS/YihMDQCsh1TCLXWUGx+wIcfVdj95J0oOMB6jfCnXwDywJ/rzmeTntMEcaYff/xBnW8lbGGi3EFc1mo9oIZN9AsVHvBTAjQLFT7wEwY0CxUe8BMKNAs1PTDTDjQLNT0wkwK0CzU9MFMGtAs2NSDTAnQLNTUwkwJ0CzU1MFMGdBvO9hU7vpIy/aSbwvYdGzfSdt+oUyHmq69WGnfAJZpYNO9qc42O/rGO1i77I4Udhyc8QLXbltcbQvUznDtCHFcAbUDXLtDHLdAUwV4PAE8ufw/r25wvkNmeZAAAAAASUVORK5CYII='></img>";
            }
            echo "</center>";
        }
        echo "</div>";
        echo "</div>";
    }
    
    public function getExtraAttributesToAppArea() {
        return "";
    }
    
    public function renderBottomArea() {
        $changeable = !isset($this->applicationSettings) || $this->getApplicationSettings()->type == "SystemApplication" ? 'systemapplication' : '';
        $appSettingsId = $this->getApplicationSettings() ? $this->getApplicationSettings()->id : "";
        $id = isset($this->configuration) ? $this->configuration->id : "";
        $className = get_class($this);
        if(strrpos($className, "\\")) {
            $className = substr($className, strrpos($className, "\\")+1);
        }
        
        echo "<div appid='$id' app='" . $className . "' class='app bottom_app $changeable " . $className . "' appsettingsid='$appSettingsId'>";
        echo "<div class='applicationinner'>";
        if($this->isEditorMode()) {
            if($this->hasWriteAccess() && !$this->getFactory()->isMobile()) {
                echo "<div class='application_settings inline gs_icon'><i class='fa fa-cog' style='font-size:20px;'></i></div>";
            }
        }
        $this->render();
        echo "</div>";
        echo "</div>";
    }
    
    public function hasWriteAccess() {
        if($this->getFactory()->getStore()->id == "f2d0c13c-a0f7-41a7-8584-3c6fa7eb68d1") {
            return $this->isEditorMode();
        }
        
        if(!$this->isEditorMode()) {
            return false;
        }
        $accesslist = @$this->getUser()->applicationAccessList;
        
        
        $type = -1;
        if(isset($this->applicationSettings) && isset($accesslist->{$this->applicationSettings->id})) {
            $type = $accesslist->{$this->applicationSettings->id};
        }
        if(@sizeof($accesslist) == 0 || $type == 0 || $type == 2) {
            return true;
        }
        if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            return true;
        }
        return false;
    }
    
    public function hasReadAccess() {
        if(!$this->isEditorMode()) {
            return false;
        }
        $accesslist = $this->getUser()->applicationAccessList;
        $type = -1;
        if(isset($accesslist->{$this->applicationSettings->id})) {
            $type = $accesslist->{$this->applicationSettings->id};
        }
        if(sizeof($accesslist) == 0  || $type == 0 || $type == 1) {
            return true;
        }
        if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            return true;
        }
        return false;
    }
    
    public function getEvents() {
        return $this->events;
    }

    public final function getConfiguration() {
        return $this->configuration;
    }
   
    public function setConfiguration($configuration) {
        if(isset($configuration->settings)) {
            foreach ($configuration->settings as $config) {
                if (isset($config->type) && $config->type == "table") {
                    $config->value = json_decode($config->value, true);
                }
            }
        }

        $this->configuration = $configuration;
    }

    /**
     * Gets the pagearea that this application
     * is added to.
     * 
     * @return PageArea
     */
    public function getPageArea() {
        $pageArea = null;
        if ($this->configuration) {
            $id = $this->configuration->id;
            $pageArea = $this->getPage()->getApplicationAreaByAppId($id);
        }
        return $pageArea;
    }
    
    public function getGlobalConfigurationSetting($key) {
        if(isset($this->applicationSettings->settings->{$key})) {
            return $this->applicationSettings->settings->{$key}->value;
        }
        return null;
    }
    
    public function getConfigurationSetting($key) {
        if (isset($this->configuration)) {
            if(isset($this->configuration->settings->{$key}->value)) {
                return $this->configuration->settings->{$key}->value;
            }
        } else {
            if(isset($this->applicationSettings->settings->{$key}->value)) {
                return $this->applicationSettings->settings->{$key}->value;
            }
        }
        
        return null;
    }

    public function getSettings() {
        return $this->getConfiguration()->settings;
    }
    
    public function getAnswersImmediatly() {
        $answers = IocContainer::getFactorySingelton()->getEventHandler()->sendRequests($this->getEvents());
        $this->events = array();
        return $answers;
    }

    public function setConfigurationSetting($key, $value, $secure = false) {
        $setting = new core_common_Setting();
        $setting->id = $key;
        $setting->value = $value;
        $setting->name = $key;
        $setting->secure = $secure;
        
        if ($this->configuration) {
            $newSettings = array();
            $newSettings[] = $setting;
            $sendCore = $this->getApiObject()->core_common_Settings();
            $sendCore->settings = $newSettings; 
            $sendCore->appId = $this->getConfiguration()->id;
            $this->getApi()->getStoreApplicationInstancePool()->setApplicationSettings($sendCore);
            $this->configuration->settings->{$key} = $setting;
        } else if($this->applicationSettings) {
            $this->applicationSettings->settings->{$key} = $setting;
            $this->getApi()->getStoreApplicationPool()->setSetting($this->applicationSettings->id, $setting);
        }
    }
    
    public function showConfigurationMenu() {
        $this->includefile('ApplicationConfiguration', 'Common');
        $this->includefile('ConfigurationMenu');
    }

    public function createUploadImageForm($event, $id, $title, $searchFrom, $appendImageToElementWithClass, $width = 100, $height = 100) {
        
        $scopeid = $_POST['scopeid'];
        //For production
        $cssInput = "position: absolute; right: 0px; top: 0px; z-index: 1; font-size: 460px; margin-top: 0px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; cursor: pointer; opacity:0;";
        $widthpx = $width."px;";
        $cssText = "overflow:hidden;position:relative; width:$widthpx";
        
        echo '
            <form style="' . $cssText . '" name="fileuploadform" id="fileupload" height="' . $height . '" width="' . $width . '" searchFrom="' . $searchFrom . '" imageTo="' . $appendImageToElementWithClass . '" class="imageUploader" style="" action="handler.php" method="POST" enctype="multipart/form-data">
                <div class="upload_image_text" >
                    <input type="file" id="file_selection" name="files[]" class="file_upload_descriptor" style="'.$cssInput.'"></input>
                    <input type="hidden" name="id" value="' . $id . '">
                    <input type="hidden" name="scopeid" value="' . $scopeid . '">';
                    if(isset($this->getConfiguration()->id)) {
                        echo '<input type="hidden" name="core[appid]" value="' . $this->getConfiguration()->id . '">';
                    }
                    echo '<input type="hidden" name="event" value="' . $event . '">
                    <input type="hidden" name="synchron" value="true">' . $title . '
                </div>
            </form>
        ';
    }

    
    
    public function isAvailable() {
        
        $hasImage = file_exists("../app/" . strtolower(get_class($this)) . "/".$this->getName().".png");
        $hasName = ($this->getName() != null);

        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() && \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->username == "aaa")
            return true;

        return ($hasImage && $hasName);
    }

    /**
     * @return core_usermanager_data_User
     */
    public function getUser() {
        return \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
    }
    
    public function getAvailablePositions() {
        if (isset($this->singleton) && $this->singleton)
            return "";
        
        return "right middle left header footer";
    }
    
    /**
     * @Event 
     * 
     * This event will be invoked before application is deleted.
     */
    public function applicationDeleted() {}
    
    /**
     * @Event 
     * 
     * This event will be invoked before application is deleted.
     */
    public function applicationAdded() {}
    
    /**
     * @Event
     * 
     * This function is called if the application is a singleton
     * and it is added to the page.
     * 
     * It will only be fired on page load, and when singleton is being added/removed.
     */
    public function renderBottom() {
    }
    
    public function getStarted() {
//        if (!$success) {
            echo $this->__f("Just click the button below");
//        }
    }
    
    /**
     * 
     * @return core_applicationmanager_ApplicationSettings
     */
    public function getApplicationSettings() {
        return $this->applicationSettings;
    }

    public function setApplicationSettings($applicationSettings) {
        $this->applicationSettings = $applicationSettings;
    }

    public function setCell($cell) {
        $this->cell = $cell;
    }
    
    public function getCell() {
        return $this->cell;
    }
    
    public function setDepth($depth) {
        $this->depth = $depth;
    }
    
    public function getDepth() {
        return $this->depth;
    }

    public function gs_show_fragment() {
        $this->includefile($_POST['gss_fragment']);
    }
    
    public function formatTimeToJavaDate($time) {
        return date("M d, Y h:m:s A", $time);
    }
    
    public function formatJavaDateToTime($time) {
        return strtotime($time);
    }
    
    public function wrapApp($applicationId, $referenceName) {
        $appInstance = $this->getWrappedApp($applicationId, $referenceName);
        
        if (!$appInstance) {
            return;
        }

        $appInstance->renderApplication();
    }
    
    public function setParentWrappedApp($app, $reference) {
        $_SESSION["parent_wrapped_app".$this->getAppInstanceId()] = $app->getAppInstanceId();
        $_SESSION["parent_wrapped_app_ref".$this->getAppInstanceId()] = $reference;
    }
    /**
     * 
     * @param type $referenceName
     * @return ApplicationBase
     */
    public function getWrappedApp($applicationId, $referenceName) {
        
        $key = $this->getConfigurationSetting($referenceName);
        if ($key == null) {
            $app = $this->getApi()->getStoreApplicationInstancePool()->createNewInstance($applicationId);
            if ($app) {
                $this->setConfigurationSetting($referenceName, $app->id);
            }
        }
        
        $instanceId = $this->getConfigurationSetting($referenceName);
        if (!$instanceId) {
            return null;
        }
        
        if (!$this->wrappedAppes) {
            $this->wrappedAppes = new stdClass();
        }
        
        if (isset($this->wrappedAppes->{$instanceId})) {
            return $this->wrappedAppes->{$instanceId};
        }
        
        $app = $this->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($instanceId);
        $appInstance = $this->getFactory()->getApplicationPool()->createAppInstance($app);
        $appInstance->wrapAppRefernce = $referenceName;
        
        $appInstance->setParentWrappedApp($this, $referenceName);
                
        $this->wrappedAppes->{$instanceId} = $appInstance;
        return $appInstance;
    }
    
    /**
     * This is not an ideal solution. 
     * The reason is that if a wrapped app is printed 
     * and the user does not leave the and then tru to use the 
     * wrapped app, it might be shown as a difference instance :(
     * 
     * The id should be stored more permanantly in app configuration or something
     */
    public function getParentWrappedApplication() {
        if (isset($_SESSION["parent_wrapped_app".$this->getAppInstanceId()]) && !$this->parentWrappedApp) {
            $app = $this->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($_SESSION["parent_wrapped_app".$this->getAppInstanceId()]);
            $this->parentWrappedApp = $this->getFactory()->getApplicationPool()->createAppInstance($app);
        }
        
        return $this->parentWrappedApp;
    }
    
    public function getModalVariable($variableName) {
        if (isset($_SESSION['gs_currently_showing_rightWidget'])) {
            return @$_SESSION['modal_variable_'.$_SESSION['gs_currently_showing_rightWidget']][$variableName];
        }
        
        if (!isset($_SESSION['gs_currently_showing_modal'])) {
            return null;
        }
        
        return @$_SESSION['modal_variable_'.$_SESSION['gs_currently_showing_modal']][$variableName];
    }
    
    public function changeModalVariable($variableName, $value) {
        if (isset($_SESSION['gs_currently_showing_rightWidget'])) {
            @$_SESSION['modal_variable_'.$_SESSION['gs_currently_showing_rightWidget']][$variableName] = $value;
        }
        @$_SESSION['modal_variable_'.$_SESSION['gs_currently_showing_modal']][$variableName] = $value;
    }
    
    /**
     * 
     * Notify parent if exists and method exists
     * 
     * @param String $functionName
     * @param [] $args
     */
    public function notifyParent($method_name, $args) {
        $app = $this->getParentWrappedApplication();
        
        if (isset($_SESSION["parent_wrapped_app_ref".$this->getAppInstanceId()])) {
            $this->wrapAppRefernce = $_SESSION["parent_wrapped_app_ref".$this->getAppInstanceId()];
        }
        
        if ($app && method_exists($app, $method_name)) {
            $args[] = $this;
            call_user_func_array(array($app, $method_name), $args);
        }
    }
    
    public function wrapContentManager($referenceName, $defaultText) {
        if (!$this->getConfigurationSetting($referenceName)) {
            $app = $this->getWrappedApp("320ada5b-a53a-46d2-99b2-9b0b26a7105a", $referenceName);
            $_POST['data']['content'] = $defaultText;
            $app->saveContent();
        }
        
        $this->wrapApp("320ada5b-a53a-46d2-99b2-9b0b26a7105a", $referenceName);
    }
    
    public function getAvailableProductTemplates() {
        return ["ecommerce_product_template_1"];
    }
    
    public function getWrapAppReference() {
        return $this->wrapAppRefernce;
    }
    
    public function getStdErrorObject() {
        $obj = new \stdClass();
        $obj->fields = new stdClass();
        $obj->gsfield = new stdClass();
        
        $obj->version = 2;
        $obj->appName = @$this->getApplicationSettings()->appName;
        
        if (!isset($this->getApplicationSettings()->appName) && isset($_POST['core']['appid']) && $_POST['core']['appid']) {
            $arr = explode("\\", $_POST['core']['appid']);
            $obj->appName = $arr[1];
        }
        
        return $obj;
    }
    
    public function doError(\stdClass $obj) {
        http_response_code(400);
        echo json_encode($obj);
        die();
    }
    
    public function closeModal() {
        $appManager = new ApplicationManager();
        $appManager->closemodal();
    }
    
    public function getSelectedMultilevelDomainName() {
        if (!isset($_SESSION['selected_multilevel_name'])) {
            $_SESSION['selected_multilevel_name'] = $this->getApi()->getStoreManager()->getMyStore()->defaultMultilevelName;
        }
        
        if (!$_SESSION['selected_multilevel_name']) {
            $_SESSION['selected_multilevel_name'] = "default";
        }
        
        return $_SESSION['selected_multilevel_name'];
    }
    
    public function changeMultiLevelName($name) {
        $_SESSION['selected_multilevel_name'] = $name;
    }

    
    public function setGetShopTableRowId() {
        $_SESSION['gs_moduletable_'.$_POST['data']['functionName']] = $_POST['data'];
        die();
    }
    
    public function getCallBackApp() {
        if (!isset($_POST['core']['fromappid'])) {
            return null;
        }
        if(stristr($_POST['core']['fromappid'], "\\")) {
            $callbackApp = new $_POST['core']['fromappid']();
            return $callbackApp;
        }
        
        $callbackApp = $this->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($_POST['core']['fromappid']);
        $instance = $this->getFactory()->getApplicationPool()->createAppInstance($callbackApp);
        return $instance;
    }

    public function handleGetShopModulePaging() {
        
    }
    
    public function gsLog() {
        $appName = get_class($this);
        $appNameArr = explode("\\", $appName);
        $appName = $appNameArr[1];
        $this->getApi()->getTrackerManager()->logTracking($appName, $_POST['data']['gslog_type'], $_POST['data']['gslog_value'], $_POST['data']['gslog_description']);
    }
    
    public function gsAlsoUpdate() {
        return array();
    }
    
    /**
     * If the app returns this as true there will be
     * shown a warning if the app uses the convetials
     * gstypes.
     */
    public function disableGsTypes() {
        return false;
    }
}
?>