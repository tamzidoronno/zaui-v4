/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.twozeroscripts;

import java.net.UnknownHostException;

/**
 *
 * @author ktonder
 */
public class All {
	public static void main(String[] args) throws UnknownHostException {
		RenameAppConfigrationToApplicationInstance.main(args);
		RenameAppSettingsToApplication.main(args);
		SetDefaultActivatedFlag.main(args);
	}
}
