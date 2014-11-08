package com.thundashop.core.databasemanager.twozeroscripts;

import com.thundashop.core.databasemanager.AddApplicationsToDatabase;
import java.net.UnknownHostException;

/**
 *
 * @author ktonder
 */
public class All {
	public static void main(String[] args) throws UnknownHostException {
		RenameAppConfigrationToApplicationInstance.main(args);
		RenameAppSettingsToApplication.main(args);
		AddApplicationsToDatabase.main(args);
		
		SetDefaultActivatedFlag.main(args);
	}
}
