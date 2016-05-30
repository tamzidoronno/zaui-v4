<?php
namespace ns_1716c7f7_1c12_4488_9b70_4996cdcf303d;

class NewsUserFilter extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "NewsUserFilter";
    }

    public function render() {
        $users = $this->getApi()->getNewsManager()->getNewsUsers($this->getConfigurationSetting("newslistid"));
        
        foreach ($users as $user) {
            $activeFilter = $this->getApi()->getNewsManager()->isFiltered($this->getConfigurationSetting("newslistid"), $user->userId) ? "filter_active" : "";;
            echo "<div class='userprofile $activeFilter' gsclick='applyUserNewsFilter' userid='$user->userId'>";
                echo "<i class='fa fa-check'></i>";
                echo "<div>";
                $profilePrinter = new \ns_7332bf2f_8fd3_422a_aabc_d77db883e472\ProfilePrinter();
                $profilePrinter->setUserId($user->userId);
                $profilePrinter->render();
                echo "</div>";
            echo "</div>";
        }
    }
    
    public function applyUserNewsFilter() {
        $this->getApi()->getNewsManager()->applyUserFilter($this->getConfigurationSetting("newslistid"), $_POST['data']['userid']);
    }
}
?>
