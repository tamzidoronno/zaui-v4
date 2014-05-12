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
public class SedoxProductImporter {

    public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException {
        Gson gson = new Gson();
        String content = gson.toJson(new SedoxProductImporter().getProducts());
        File file = new File("sedoxProducts.json");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
    }
    
    public List<SedoxProduct> getProducts() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/databank?user=databank&password=flatark");
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from SedoxProduct ");

        List<SedoxProduct> products = new ArrayList<SedoxProduct>();
        int i = 0;
        int j = 0;
        while (resultSet.next()) {
            SedoxProduct product = new SedoxProduct();
            product.id = resultSet.getInt("id");
            product.filedesc = resultSet.getString("filedesc");
//            product.dateCreated = resultSet.getDate("dateCreated");
            product.userBrand = resultSet.getString("userBrand");
            product.userModel = resultSet.getString("userModel");
            product.userCharacteristic = resultSet.getString("userCharacteristic");
            product.userYear = resultSet.getString("userYear");
            product.userPower = resultSet.getString("userPower");
            product.userTool = resultSet.getString("userTool");
            product.status = resultSet.getString("status");
            product.saleAble = resultSet.getBoolean("saleAble");
            product.userId = resultSet.getInt("userId");
            product.originalChecksum = resultSet.getString("originalChecksum");
            product.gearType = resultSet.getString("gearType");
            product.useCreditAccount = resultSet.getString("useCreditAccount");
            product.comment = resultSet.getString("comment");
            product.userSeries = resultSet.getString("userSeries");
            product.userBuild = resultSet.getString("userBuild");
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
}
