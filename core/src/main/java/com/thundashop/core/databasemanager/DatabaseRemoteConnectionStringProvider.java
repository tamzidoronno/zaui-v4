package com.thundashop.core.databasemanager;

import com.thundashop.core.common.GetShopLogHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseRemoteConnectionStringProvider {

    private static Logger logger = LoggerFactory.getLogger(DatabaseRemoteConnectionStringProvider.class);

    public static String getConnectionString() {
        String connectionString = "mongodb://getshopreadonly:readonlypassword@192.168.100.1/admin";

        if (GetShopLogHandler.isDeveloper) {
            String[] linesFromFile = readLines("../commonpassword.txt");
            if (linesFromFile != null && linesFromFile.length > 0) {
                connectionString = linesFromFile[0];
            }
        }

        return connectionString;
    }

    private static String[] readLines(String filename) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            return lines.toArray(new String[lines.size()]);
        } catch (IOException ex) {
            logger.error("Warning, you do not have the commonpassword.txt file on your computer, you will not be able to write to the common database. file should be located ../commonpassword.txt, next to the secret.txt", ex);
        }

        return new String[0];
    }

}
