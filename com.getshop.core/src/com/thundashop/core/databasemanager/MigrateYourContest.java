package com.thundashop.core.databasemanager;

import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MigrateYourContest {
    public static void main(String[] args) throws SQLException {
        Connection connect = DriverManager.getConnection("jdbc:mysql://sql.nbtxathcx.net/nbtx_yourcontests?user=boggi&password=8fbAhUt7B5");
        PreparedStatement stmt = connect.prepareStatement("select * from users");
        ResultSet result = stmt.executeQuery();
        
        while(result.next()) {
//            User user = new User();
//            user.username = result.getString("username");
//            user.password = result.getString("password");
//            user.emailAddress = result.getString("email");
//            user.passkey = result.getString("passkey");
//            String[] ips = result.getString("ip").split(" ");
//            for(String ip : ips) {
//                user.ipAddress.add(ip);
//            }
//            user.rowCreatedDate = result.getDate("joinDate");
//            try {
//                user.lastLoggedIn = result.getDate("accessDate");
//            }catch(Exception e) {
//            }
//            Address address = new Address();
//            address.countryname = result.getString("Country");
//            address.province = result.getString("province");
//            if(result.getInt("admin") == 1) {
//                user.type = User.Type.ADMINISTRATOR;
//            }
        }
        
    }
}
