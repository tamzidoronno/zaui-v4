/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SedoxMysqlImporter {

    public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException {
        Gson gson = new Gson();
        String content = gson.toJson(new SedoxMysqlImporter().getProducts());
        File file = new File("sedoxProducts.json");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
    }

    public SedoxMysqlImporter() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
    }
    
    
    public List<SedoxProduct> getProducts() throws ClassNotFoundException, SQLException {
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/databank?user=databank&password=flatark");
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from SedoxProduct ");

        List<SedoxProduct> products = new ArrayList<SedoxProduct>();
        int i = 0;
        int j = 0;
        while (resultSet.next()) {
            SedoxProduct product = new SedoxProduct();
            product.id = ""+resultSet.getInt("id");
            product.filedesc = resultSet.getString("filedesc");
            product.rowCreatedDate = resultSet.getTimestamp("dateCreated");
            product.brand = resultSet.getString("userBrand");
            product.model = resultSet.getString("userModel");
            product.engineSize = resultSet.getString("userCharacteristic");
            product.year = resultSet.getString("userYear");
            product.power = resultSet.getString("userPower");
            product.tool = resultSet.getString("userTool");
            product.status = resultSet.getString("status");
            product.saleAble = resultSet.getBoolean("saleAble");
            product.firstUploadedByUserId = ""+resultSet.getInt("userId");
            product.originalChecksum = resultSet.getString("originalChecksum");
            product.gearType = resultSet.getString("gearType");
            product.useCreditAccount = resultSet.getString("useCreditAccount");
            product.comment = resultSet.getString("comment");
            product.ecuType = resultSet.getString("userSeries");
            product.ecuBrand = resultSet.getString("userBuild");
            product.channel = resultSet.getString("channel");
            product.started = resultSet.getBoolean("started");

            Statement statement2 = connect.createStatement();
            ResultSet resultSetBinFiles = statement2.executeQuery("select * from SedoxBinaryFile where productId = " + product.id);

            while (resultSetBinFiles.next()) {
                SedoxBinaryFile file = new SedoxBinaryFile();
                file.additionalInformation = resultSetBinFiles.getString("additionalInformation");
                file.id = resultSetBinFiles.getInt("id");
                file.md5sum = resultSetBinFiles.getString("md5sum");
                file.fileType = resultSetBinFiles.getString("fileType");
                file.orgFilename = resultSetBinFiles.getString("orgFilename");
                file.extraInformation = resultSetBinFiles.getString("extraInformation");
                file.additionalInformation = resultSetBinFiles.getString("additionalInformation");
                file.checksumCorrected = resultSetBinFiles.getBoolean("checksumCorrected");
                product.binaryFiles.add(file);

                Statement statement3 = connect.createStatement();
                ResultSet attributesResSet = statement3.executeQuery("select * from SedoxAttributeValue where sedoxFileId = " + file.id);
                while (attributesResSet.next()) {
                    SedoxProductAttribute attribute = new SedoxProductAttribute();
                    attribute.id = attributesResSet.getInt("sedoxAttributeId");
                    attribute.value = attributesResSet.getString("value");
                    file.attribues.add(attribute);
                }
            }
            
            Statement historyStatment = connect.createStatement();
            ResultSet historyResultSet = historyStatment.executeQuery("select description, dateCreated, userId from SedoxProductHistory where productId = " + product.id);
            while (historyResultSet.next()) {
                SedoxProductHistory history = new SedoxProductHistory();
                history.userId = ""+historyResultSet.getInt("userId");
                history.dateCreated = historyResultSet.getDate("dateCreated");
                history.description = historyResultSet.getString("description");
            }
            
            if (i > 100) {
                j++;
                System.out.println("J: " + j * 100);
                i = 0;
            }
            products.add(product);
            i++;
        }
        
        return products;
    }
    
    public List<SedoxUser> getCreditAccounts() throws SQLException {
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/databank?user=databank&password=flatark");
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from CreditAccount ");
        
        List<SedoxUser> accounts = new ArrayList();
        
        int i = 0;
        int j = 0;
        while(resultSet.next()) {
            SedoxCreditAccount account = new SedoxCreditAccount();
            account.setBalance((double)resultSet.getInt("accountBalance"));
            
            SedoxUser sedoxUser = new SedoxUser();
            sedoxUser.magentoId = ""+resultSet.getInt("userId");
            sedoxUser.isNorwegian = resultSet.getInt("norwegianCustomer") == 1 ? true : false;
            sedoxUser.canUseExternalProgram = resultSet.getInt("norwegianCustomer") == 1 ? true : false;
            
            Statement statement2 = connect.createStatement();
            ResultSet resultSet2 = statement2.executeQuery("select * from CreditAccountHistory where creditAccountId = " + resultSet.getInt("id"));
            while(resultSet2.next()) {
                SedoxCreditHistory history = new SedoxCreditHistory();
                history.transactionReference = resultSet2.getInt("id");
                history.amount = Double.valueOf(resultSet2.getString("amount").replace(".", ""));
                history.description = resultSet2.getString("description");
                history.dateCreated = resultSet2.getTimestamp("dateCreated");
                account.history.add(history);
            }
        
            Statement statement3 = connect.createStatement();
            ResultSet resultSet3 = statement3.executeQuery("select * from SedoxOrder where userId = " + sedoxUser.magentoId);
            while(resultSet3.next()) {
                SedoxOrder order = new SedoxOrder();
                order.creditAmount = resultSet3.getInt("creditAmount");
                order.productId = ""+resultSet3.getInt("productId");
                sedoxUser.orders.add(order);
            }
            
            Statement statement4 = connect.createStatement();
            ResultSet resultSet4 = statement4.executeQuery("select * from CreditOrder where customerId = " + sedoxUser.magentoId);
            while(resultSet4.next()) {
                SedoxCreditOrder order = new SedoxCreditOrder();
                order.amount = resultSet4.getInt("amount");
                order.magentoOrderId = resultSet4.getInt("orderId");
                sedoxUser.creditOrders.add(order);
            }
            
            sedoxUser.creditAccount = account;
            accounts.add(sedoxUser);
            
            if (i > 100) {
                j++;
                System.out.println("K: " + j * 100);
                i = 0;
            }
            i++;
        }
        
        return accounts;
    }   
}