package com.thundashop.core.databasemanager.twozeroscripts;

import com.thundashop.core.databasemanager.AddApplicationsToDatabase;
import java.net.UnknownHostException;

/**
 *
 * @author ktonder
 */
public class All {

    public static void main(String[] args) throws UnknownHostException {
        args = new String[1];
        RenameAppConfigrationToApplicationInstance.main(args);
        RenameAppSettingsToApplication.main(args);
        AddApplicationsToDatabase.main(args);
        SetApplicationModules.main(args);
        SetDefaultActivatedFlag.main(args);
        System.out.println("DONE");
        System.exit(1);
    }
}
