<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_0f70b6b0_97a6_4cd1_9b22_cc30332054b5;

/**
 * Description of ModuleTheme
 *
 * @author ktonder
 */
class ModuleTheme extends \ThemeApplication {
    //put your code here
    
    public function getThemeClasses() {
        return array("module_toprow", "module_grayrow", 'top_row_width_left_sidebar');
    }
}
