<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of InformationScreen
 *
 * @author ktonder
 */
namespace ns_b01e9b84_f8d3_46f8_87c3_07eeaf964e20;

class InformationScreen extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "GetShop Information screen management";
    }

    public function getName() {
        return $this->__f("InformatoinScreen");
    }

    public function render() {
        $this->includeFile("infoscreens");
    }

    public function printInfoScreensSelectors($screens, $holder=null) {
        $currentId = $this->getCurrentTvId();
        
        echo '<div class="tvs">';
            foreach ($screens as $screen) {
                $activeClass = "";
                if ($screen->infoScreenId == $currentId) {
                    $activeClass = "tv_active";
                }
                echo '<div class="tv_selector '.$activeClass.'" tv_id="'.$screen->infoScreenId.'"><i class="fa fa-desktop"></i><div class="tv_title">'.$screen->name.'</div></div>';

            }

            if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
                echo "<div class='tv_selector add_information_screen' customerId='$holder->customerId'><i class='fa fa-desktop'></i><div class='tv_title'>Create new</div></div>";
            }
        
        echo '</div>';
        
        
    }

    public function addInformationScreen() {
        $this->getApi()->getInformationScreenManager()->registerTv($_POST['data']['customerId']);
    }
    
    public function selectTv() {
        $_SESSION['information_screen_current_id'] = $_POST['data']['id'];
    }
    
    public function getCurrentTvId() {
        if (isset($_SESSION['information_screen_current_id'])) {
            return $_SESSION['information_screen_current_id'];
        }
        
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $holders = $this->getApi()->getInformationScreenManager()->getHolders();
            foreach ($holders as $holder) {
                foreach ($holder->screens as $screen) {
                    return $screen->infoScreenId;
                }
            }
        } else {
            $screens = $this->getApi()->getInformationScreenManager()->getInformationScreens();
            foreach ($screens as $screen) {
                return $screen->infoScreenId;
            }
        }
    }

    public function getCurrentTv() {
        $id = $this->getCurrentTvId();
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $holders = $this->getApi()->getInformationScreenManager()->getHolders();
            foreach ($holders as $holder) {
                foreach ($holder->screens as $screen) {
                    if ($screen->infoScreenId == $id) {
                        return $screen;
                    }
                }
            }
        } else {
            $screens = $this->getApi()->getInformationScreenManager()->getInformationScreens();
            foreach ($screens as $screen) {
                if ($screen->infoScreenId == $id) {
                    return $screen;
                }
            }
        }
    }

    public function saveSlider() {
        $slider = $this->getSelectedSlider();
        
        if (!$slider) {
            $slider = new \core_informationscreen_Slider();
            $slider->texts = [];
        }
        
        foreach ($_POST['data']['texts'] as $text) {
            $slider->texts->{$text['key']} = $text['value'];
        }
        
        $slider->id = $_POST['data']['gs_slider_id'];
        $slider->name = $_POST['data']['gs_slider_title'];
        $this->getApi()->getInformationScreenManager()->addSlider($slider, $this->getCurrentTvId());
    }
    
    public function getSelectedSlider() {
        if (!isset($_SESSION['slider_info_selected'])) {
            return null;
        }
        
        $retSlider = null;
        
        if ($_SESSION['slider_info_selected'] == "new") {
            $slider = new \core_informationscreen_Slider();
            $slider->id = "new";
            $retSlider = $slider;
        }
        
        $tv = $this->getCurrentTv();
        if ($tv) {
            foreach ($tv->sliders as $slider) {
                if ($slider->id == $_SESSION['slider_info_selected']) {
                    $retSlider = $slider;
                }
            }
        }
        
        return $retSlider;
    }
    
    public function selectSlider() {
        $_SESSION['slider_info_selected'] = $_POST['data']['sliderId'];
        
    }
    
    public function setSliderType() {
        $slider = $this->getSelectedSlider();
        if ($slider->id == "new") {
            $slider->name = $this->__f("New slider");
            $slider->id = "";
        }
        
        if ($slider) {
            $slider->sliderType = $_POST['data']['type'];
            $this->getApi()->getInformationScreenManager()->addSlider($slider, $this->getCurrentTvId());
        }
        
    }
    
    public function deleteSlider() {
        $this->getApi()->getInformationScreenManager()->deleteSlider($_POST['data']['sliderId'], $this->getCurrentTvId());
    }
    
    public function savePicture() {
        $base64 = substr($_POST['data']['fileBase64'], strpos($_POST['data']['fileBase64'], "base64,")+7);
        $content = base64_decode($base64);
        $imgId = \FileUpload::storeFile($content);
        
        if (isset($_POST['data']['pictureType']) && $_POST['data']['pictureType'] == "gs_background_image_for_tv") {
            $tv = $this->getCurrentTv();
            if ($tv) {
                $tv->backgroundImage = $imgId;
                $this->getApi()->getInformationScreenManager()->saveTv($tv);
            }
        } else {
            $slider = $this->getSelectedSlider();
            if ($slider) {
                $slider->images->{$_POST['data']['pictureType']} = $imgId;
                $this->getApi()->getInformationScreenManager()->addSlider($slider, $this->getCurrentTvId());
            }
        }
    }
    
    public function changeShowNewsFeed() {
        $tv = $this->getCurrentTv();
        if ($tv) {
            $tv->showNewsFeed = $_POST['data']['showNewsFeed'];
            $this->getApi()->getInformationScreenManager()->saveTv($tv);
        }
    }
}
