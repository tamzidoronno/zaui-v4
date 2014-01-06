/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.remote;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class WinVaskDBIntegration {

    private void error_log(String string) {
        System.out.println(string);
    }

    class Vare {
        String description;
        double tax;
        int varenr;
        double pris;
        int count;
        int momskode;
    }

    private Vare getVareConnectedToItem(CartItem item) throws SQLException {
        if(item.getProduct().skui == null || item.getProduct().skui.isEmpty()) {
            //Skui not set...
            error_log("!!!!!!!!!!!!! SKUI NOT SET FOR PRODUCT:" + item.getProduct().name + " !!!!!!!!!!!!!!!!!!!!!1");
            return null;
        }
        
        int id = new Integer(item.getProduct().skui);
        
        PreparedStatement stmt = Cnx.prepareStatement("select * from vare where vare_nr = " + id);
        ResultSet rs = stmt.executeQuery();
        if(!rs.next()) {
            error_log("!!!!!!!!!!!! did not find product in winvask with varenr: " + id);
            return null;
        }
        
        Vare vare = new Vare();
        vare.description = rs.getString("BESKRIVELSE");
        vare.tax = rs.getDouble("MOMS");
        vare.pris = rs.getDouble("pris");
        vare.varenr = id;
        vare.momskode = rs.getInt("MOMSKODE");
        return vare;
    }
    
    private Connection Cnx;
    private Connection CnxYearly;

    public WinVaskDBIntegration() throws SQLException, ClassNotFoundException {
        this.connectToDB();
        this.connectToYearlyDb();
    }

    /**
     * @param args the command line arguments
     */
    private void connectToDB() throws SQLException, ClassNotFoundException {
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        String url = "jdbc:odbc:MyODBC";
        Cnx = DriverManager.getConnection(url);
    }

    private void connectToYearlyDb() throws ClassNotFoundException, SQLException {
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        String url = "jdbc:odbc:yearlydata";
        CnxYearly = DriverManager.getConnection(url);
    }

    public void dumpTable(String table, boolean displayContent, boolean yearly, boolean vertical) throws SQLException {
        PreparedStatement st = null;
        String query = "select * from " + table;
        if (!table.isEmpty()) {
            if (yearly) {
                System.out.println("Dumping yearly db : " + table);
                st = CnxYearly.prepareStatement(query);
            } else {
                System.out.println("Dumping common db : " + table);
                st = Cnx.prepareStatement(query);
            }
        }

        ResultSet res = null;
        if (table.isEmpty()) {
            res = CnxYearly.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
        } else {
            res = st.executeQuery();
        }
        int count = res.getMetaData().getColumnCount();

        for (int i = 1; i <= count; i++) {
            System.out.print(res.getMetaData().getColumnName(i) + "\t");
        }
        System.out.println();
        if (displayContent) {
            while (res.next()) {
                for (int i = 1; i <= count; i++) {
                    if(vertical) {
                        System.out.print(res.getObject(i) + "\t");
                    } else { 
                        System.out.println(res.getMetaData().getColumnName(i) + " :" + res.getObject(i) + "\t");
                    }
                }
                System.out.println("");
                System.out.println("-----------------");
            }
        }
        System.out.println();
    }

    public Integer findCustomer(String name) throws SQLException, UnsupportedEncodingException {
        name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
        String comparename = name.replaceAll("\\s+", "").toLowerCase();
        String query = "select * from kunde";
        PreparedStatement stmt = Cnx.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String dbname = rs.getString("navn").replaceAll("\\s+", "").toLowerCase();
            if (comparename.equals(dbname)) {
                return rs.getInt("kundenr");
            }
        }
        return -1;
    }

    public Integer findNextCustomerId() throws SQLException {
        int id = -1;
        String query = "select kundenr from kunde";
        PreparedStatement stmt = Cnx.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int rowid = rs.getInt("kundenr");
            if (rowid > id) {
                id = rowid;
            }
        }
        id++;
        return id;
    }

    public Integer createCustomer(String name, String address, String postcode, String city, String phone, String email) throws SQLException {
        Integer kundenr = findNextCustomerId();
        String query = "insert into kunde (navn, gate_adr, post_nr, sted, telefon_nr, beregn_mva, kundenr, e_mail) values(?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = Cnx.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setString(2, address);
        stmt.setString(3, postcode);
        stmt.setString(4, city);
        stmt.setString(5, phone);
        stmt.setInt(6, 1);
        stmt.setInt(7, kundenr);
        stmt.setString(8, email);
        stmt.execute();
        return kundenr;
    }

    public void createInvoice(int kundenr, Order order) throws SQLException {
        //Tables changed: Bilag, UBilag
        //Inifile System Vattr miniopsjoner miniskjerm
        int wierdDate = converToWierdData(order.createdDate);
        int bilagnr = getNextBilagNumber();
        int ordrenr = getNextOrdreNumber();

        List<CartItem> items = order.cart.getItems();

        List<Vare> varer = new ArrayList();
        double mva_belop = 0;
        double price = 0;
        for (CartItem item : items) {
            Vare vare = getVareConnectedToItem(item);
            if(vare == null) {
                error_log("Failed to create invoice");
                return;
            }
            vare.count = item.getCount();
            price += (vare.pris * (vare.tax / 100))*item.getCount();
            mva_belop  += vare.pris*item.getCount();
            varer.add(vare);
        }
        double sm3 = price + mva_belop;


        PreparedStatement stmt = CnxYearly.prepareStatement("insert into Bilag (bilagnr, ordrenr, kundenr,levdato,fakturadato,dato,sm2,mva_belop,sm3)values(?,?,?,?,?,?,?,?,?)");
        stmt.setInt(1, bilagnr);
        stmt.setInt(2, ordrenr);
        stmt.setInt(3, kundenr);
        stmt.setInt(4, wierdDate);
        stmt.setInt(5, wierdDate);
        stmt.setInt(6, wierdDate);
        stmt.setDouble(7, mva_belop);
        stmt.setDouble(8, price);
        stmt.setDouble(9, sm3);
        stmt.execute();

        int lnr = 1;
        for(Vare vare : varer) {
            PreparedStatement ubilagstmt = CnxYearly.prepareStatement("insert into UBilag (BILAGNR, KUNDENR, VARENR, VARE:BESKRIVELSE, VARE:PRIS, DB_KRONER, DATO, SM1, SM2, MVA, MOMS, SM3, LEVDATO,T:DATO_FRA,T:DATO_TIL,LINE_HEIGHT, LNR, ALT_VNR, VARE:ENHET, STK_REG, MOMSKODE, ANTALL)"
                                      + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            double theprice = vare.pris;
            double mvaFactor = vare.tax;
            double mvaPrice = (vare.pris * (vare.tax/100));
            
            ubilagstmt.setInt(1, bilagnr);
            ubilagstmt.setInt(2, kundenr);
            ubilagstmt.setInt(3, vare.varenr);
            ubilagstmt.setString(4, vare.description);
            ubilagstmt.setDouble(5, theprice);
            ubilagstmt.setDouble(6, theprice*vare.count);
            ubilagstmt.setInt(7, wierdDate);
            ubilagstmt.setDouble(8, theprice*vare.count);
            ubilagstmt.setDouble(9, theprice*vare.count);
            ubilagstmt.setDouble(10, mvaPrice);
            ubilagstmt.setDouble(11, mvaFactor);
            ubilagstmt.setDouble(12, (theprice*vare.count)+mvaPrice);
            ubilagstmt.setInt(13, wierdDate);
            ubilagstmt.setInt(14, wierdDate);
            ubilagstmt.setInt(15, wierdDate);
            ubilagstmt.setInt(16, 12);
            ubilagstmt.setInt(17, lnr);
            ubilagstmt.setInt(18, vare.varenr);
            ubilagstmt.setString(19, "Stk");
            ubilagstmt.setInt(20, vare.count);
            ubilagstmt.setInt(21, vare.momskode);
            ubilagstmt.setInt(22, vare.count);
            ubilagstmt.execute();
            lnr++;
        }

    }

    private int converToWierdData(Date createdDate) {
        int base = 77805; //This is 05.01.2014, don't ask me why
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.DAY_OF_MONTH, 5);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);

        long end = createdDate.getTime();
        System.out.println(createdDate.getTime());
        int days = (int) (createdDate.getTime() - cal.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        System.out.println("Number of days: " + days + "(" + createdDate.toString() + ")");
        return base + days;
    }

    private int getNextBilagNumber() throws SQLException {
        int bilagnumber = -1;
        PreparedStatement stmt = CnxYearly.prepareStatement("select bilagnr from bilag");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int dbbilag = rs.getInt("bilagnr");
            if (bilagnumber < dbbilag) {
                bilagnumber = dbbilag;
            }
        }
        bilagnumber++;
        System.out.println(bilagnumber);
        return bilagnumber;
    }

    private int getNextOrdreNumber() throws SQLException {
        int ordrenr = -1;
        PreparedStatement stmt = CnxYearly.prepareStatement("select ordrenr from bilag");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int dbbilag = rs.getInt("ordrenr");
            if (ordrenr < dbbilag) {
                ordrenr = dbbilag;
            }
        }
        ordrenr++;
        return ordrenr;
    }
}
