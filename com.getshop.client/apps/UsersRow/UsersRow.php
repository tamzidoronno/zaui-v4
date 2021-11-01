<?php
namespace ns_7b68e527_b6fe_4d56_8628_0f981e0b87eb;

class UsersRow extends \WebshopApplication implements \Application {

    private $user;

    public function getDescription() {
        
    }

    public function getName() {
        return "UsersRow";
    }

    public function getUser() {
        if(!$this->user) {
            $this->user = $this->getApi()->getUserManager()->getUserById($_SESSION['usersrow_lastuserid']);
        }
        return $this->user;
    }
    
    public function render() {
        echo "<table cellspacing='0' cellpadding='0' width='100%'>";
        echo "<tr>";
        echo "<td valign='top' style='width:150px;'>";
        $this->includefile("leftmenu");
        echo "</td>";
        echo "<td valign='top'>";
        echo "<div style='padding-left: 20px;' class='mainarea'>";
        $this->loadSelectedArea();
        echo "</div>";
        echo "</td>";
        echo "</tr>";
        echo "</table>";
    }

    public function changeArea() {
        $_SESSION['usersrow_selectedarea'] = $_POST['data']['area'];
        $this->loadSelectedArea();
    }
    
    public function loadUser($id) {
        $_SESSION['usersrow_lastuserid'] = $id;
        $this->user = $this->getApi()->getUserManager()->getUserById($id);
    }

    public function getSelectedArea() {
        if(isset($_SESSION['usersrow_selectedarea'])) {
            return $_SESSION['usersrow_selectedarea'];
        }
        return "userinfo";
    }
    
    public function loadSelectedArea() {
        $area = $this->getSelectedArea();
        $this->includefile($area);
    }
    
    public function updateUsersRight() {
        $modules = $this->getUserAccessModules();
        $user = $this->getUser();
        $user->hasAccessToModules = array();
        foreach($modules as $module) {
            if($_POST['data'][$module->id] == "true") {
                $user->hasAccessToModules[] = $module->id;
            }
        }
        $this->getApi()->getUserManager()->saveUser($user);
    }

}
?>
