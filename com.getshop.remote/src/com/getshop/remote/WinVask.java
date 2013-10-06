/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.remote;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author boggi
 */
public class WinVask {
    private Connection Cnx;
    private Statement st;

    public void startClient() throws SQLException, ClassNotFoundException {
        connectToDB();
        dumpTable("kunde");
    }
    
    /**
     * @param args the command line arguments
     */
    public void connectToDB() throws SQLException, ClassNotFoundException {
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        String url = "jdbc:odbc:MyODBC";
        Cnx = DriverManager.getConnection(url);
        st = Cnx.createStatement();
    }

    public void dumpTable(String table) throws SQLException {
        PreparedStatement st = Cnx.prepareStatement("select * from " + table);
        st.execute();
        ResultSet res = st.getResultSet();
        int count = res.getMetaData().getColumnCount();

        for(int i = 1; i <= count; i++) {
            System.out.print(res.getMetaData().getColumnName(i) + "\t");
        }
        System.out.println();
        while (res.next()) {
            for (int i = 1; i <= count; i++) {
                System.out.print(res.getObject(i) + "\t");
            }
            System.out.println();
        }
    }   

    private void createCustomer(String name, String address, String postcode, String city, String phone) throws SQLException {
        String query = "insert into kunde (navn, gate_adr, post_nr, sted, telefon_nr, beregn_mva, kundenr) values(?,?,?,?,?,?,?)";
        PreparedStatement stmt = Cnx.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setString(2, address);
        stmt.setString(3, postcode);
        stmt.setString(4, city);
        stmt.setString(5, phone);
        stmt.setInt(6, 1);
        stmt.setInt(7, 90000);
        stmt.execute();
    }
}
