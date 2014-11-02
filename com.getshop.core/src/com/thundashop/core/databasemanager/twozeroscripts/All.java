package com.thundashop.core.databasemanager.twozeroscripts;

import com.thundashop.core.databasemanager.AddApplicationsToDatabase;
import java.net.UnknownHostException;

/**
 *
 * @author ktonder
 */
public class All {
	public static void main(String[] args) throws UnknownHostException {
		AddApplicationsToDatabase.main(args);
		RenameAppConfigrationToApplicationInstance.main(args);
		RenameAppSettingsToApplication.main(args);
		SetDefaultActivatedFlag.main(args);
	}
}
