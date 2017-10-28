package getshoppaymentservice;

import java.util.UUID;

public class PasswordUser {
    public String storeId;
    public String hostname;
    public String engine;
    public String wubookCreditCardPassword;
    public String getshopUsername;
    public String getshopPassword;

    PasswordUser() {
        
    }
    
    PasswordUser(String line) {
        String[] entries = line.split(";---;");
        storeId = entries[0];
        hostname = entries[1];
        wubookCreditCardPassword = entries[2];
        getshopUsername = entries[3];
        getshopPassword = entries[4];
        engine = entries[6];
    }
    
    public String toString() {
        return storeId + ";---;" + hostname + ";---;" + wubookCreditCardPassword + ";---;" + getshopUsername + ";---;" + getshopPassword + ";---;" + engine;
    }
}
