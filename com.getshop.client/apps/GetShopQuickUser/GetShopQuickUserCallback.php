<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_b5e9370e_121f_414d_bda2_74df44010c3b;

/**
 *
 * @author ktonder
 */
interface GetShopQuickUserCallback {
    /**
     * Save the user.
     * 
     * @param type $user
     */
    public function saveUser($user);
    
    /**
     * Request to change the user.
     * @param \core_usermanager_data_User $user
     */
    public function changeUser($user);
    
    /**
     * Should create the user that correspnd to the company
     */
    public function createCompany();
    
    /**
     * should return the user created.
     */
    public function createNewUser();
}
