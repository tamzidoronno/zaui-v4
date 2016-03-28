<?php

namespace ns_1de1b272_2de3_482f_90cd_6bb8ceb77aca;

class ProMeisterSpiderDiagram extends \MarketingApplication implements \Application {

    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterSpiderDiagram";
    }

    public function preProcess() {
        
        
        if (!isset($_SESSION['ProMeisterSpiderDiager_current_user_id'])) {
            $_SESSION['{ProMeisterSpiderDiagram.FullName}'] = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->fullName;
        } else {
            $_SESSION['ProMeisterSpiderDiager_current_user_id_toshow'] = $_SESSION['ProMeisterSpiderDiager_current_user_id'];
            $user = $this->getApi()->getUserManager()->getUserById($_SESSION['ProMeisterSpiderDiager_current_user_id']);
            
            if (!$user) {
                $_SESSION['ProMeisterSpiderDiager_current_user_id_toshow'] = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
                $_SESSION['{ProMeisterSpiderDiagram.FullName}'] = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->fullName;
            } else {
                $_SESSION['{ProMeisterSpiderDiagram.FullName}'] = $user->fullName;
            }
        }
    }
    
    /**
     * 
     * @return \core_usermanager_data_User
     */
    public function getLoggedOnUser() {
        return $this->loggedOnUser;
    }
    
    public function render() {
        $this->loggedOnUser = $this->getApi()->getUserManager()->getUserById(\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id);;
        if ($this->loggedOnUser->isCompanyOwner) {
            $this->includefile('userselection');
        }
        
        $this->requirement = $this->getApi()->getQuestBackManager()->getResultRequirement();
        $this->includefile("diagram");
    }

    public function getRequirements() {
        return $this->requirement;
    }

    public function translateToSixScore($score) {
        $foo = (float) $score / ((float) 100 / (float) 6);
        return number_format((float) $foo, 2, '.', '');
    }

    public function validateHasEnoughInformation($result, $groupRequirement) {
        
    }

    public function getDataProvider($user) {
        $requirement = $this->getRequirements();

        $group = @$this->getCurrentUser()->companyObject->groupId;

        if (!$group) {
            $this->includefile("unsufficentdata");
            return;
        }

        $groupRequirement = $requirement->groupRequiments->{$group}->requirements;

        if (!$groupRequirement) {
            $this->includefile("unsufficentdata");
            return;
        }

        $result = null;
        if ($user != "company") {
            $result = $this->getApi()->getQuestBackManager()->getTestResultForUser($requirement->testId, $user->id);
        }
        
        
        $dataProvider = "";

        foreach ($groupRequirement as $catid => $cat) {
            if (!in_array($catid, $requirement->catsThatShouldBeUsed)) {
                continue;
            }

            $catObject = $this->getApi()->getQuestBackManager()->getQuestion($catid);
            $behov = $this->translateToSixScore($cat->mandatory);
            $krav = $this->translateToSixScore($cat->required);
            $catName = $catObject->name;

            $catScore = $this->getCatScore($result, $catid, $user, $requirement->testId);

            $score = $this->translateToSixScore($catScore);


            $dataProvider .= "{";
            $dataProvider .= "\"country\": \"$catName\",";
            $dataProvider .= "\"nuläge\": \"$score\",";
            $dataProvider .= "\"krav\": \"$krav\",";
            $dataProvider .= "\"behov\": \"$behov\"";
            $dataProvider .= "}";
            $dataProvider .= ",";
        }
        
        return $dataProvider;
    }

    public function getCurrentUser() {
        if (isset($_SESSION['ProMeisterSpiderDiager_current_user_id']) && $_SESSION['ProMeisterSpiderDiager_current_user_id'] == "company") {
            return $this->getLoggedOnUser();
            
        }
        if (isset($_SESSION['ProMeisterSpiderDiager_current_user_id'])) {
            return $this->getApi()->getUserManager()->getUserById($_SESSION['ProMeisterSpiderDiager_current_user_id']);
        }
        
        return $this->getApi()->getUserManager()->getUserById(\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id);
    }

    public function setUserId() {
        $_SESSION['ProMeisterSpiderDiager_current_user_id'] = $_POST['data']['userId'];
    }

    public function isCompanySelected() {
        return isset($_SESSION['ProMeisterSpiderDiager_current_user_id']) && $_SESSION['ProMeisterSpiderDiager_current_user_id'] == "company";
    }

    public function getCatScore($result, $catid, $user, $testid) {
        if ($this->isCompanySelected()) {
            $bestTestResult = $this->getApi()->getQuestBackManager()->getBestCategoryResultForCompany($testid, $catid);
            return \ns_4194456a_09b3_4eca_afb3_b3948d1f8767\QuestBackResultPrinter::getResult($bestTestResult, $catid);    
        }
        
        return \ns_4194456a_09b3_4eca_afb3_b3948d1f8767\QuestBackResultPrinter::getResult($result, $catid);
    }

}

?>
