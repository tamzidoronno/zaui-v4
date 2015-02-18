/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.vismawilhelmsenhouse;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ktonder
 */
public class VismaSql {
    private String vismaAddress;
    private String vismaDatabaseName;
    private String vismaUserName;
    private String vismaPassword;
    
    private Connection conn;

    public VismaSql(Application application, String vismaPassword) throws ClassNotFoundException, SQLException {
        this.vismaAddress = application.getSetting("vismaaddress");
        this.vismaDatabaseName = application.getSetting("vismadb");
        this.vismaUserName = application.getSetting("vismausername");
        this.vismaPassword = vismaPassword;
        connect();
    }

    private void connect() throws ClassNotFoundException, SQLException {
        String constring = "jdbc:sqlserver://" + vismaAddress + ":1433;databaseName=" + vismaDatabaseName;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        this.conn = DriverManager.getConnection(constring, vismaUserName, vismaPassword);
    }
    
    public boolean checkIfUserExists(User user) throws SQLException {
        String sql = "select * from dbo.Actor where CustNo = " + user.customerId;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        boolean exists = rs.next();
        rs.close();
        stmt.close();
        return exists;
    }
    
    public boolean checkInVismaIfUserExists(Order order) throws SQLException {
        String sql = "select ordno from dbo.Ord where csOrdNo = " + order.incrementOrderId;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        boolean exists = rs.next();
        rs.close();
        stmt.close();
        return exists;
    }
    
    public void close() throws SQLException {
        conn.close();
    }

}
