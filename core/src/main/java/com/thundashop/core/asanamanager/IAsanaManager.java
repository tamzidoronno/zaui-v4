/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.asanamanager;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
interface IAsanaManager {
    @Administrator
    public AsanaProjects getProjects() throws UnirestException;
    
    @Administrator
    public List<AsanaTask> getTasks(long projectId, int year, int month) throws Exception;
}
