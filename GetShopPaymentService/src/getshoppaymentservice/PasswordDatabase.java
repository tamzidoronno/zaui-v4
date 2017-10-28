package getshoppaymentservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.jasypt.util.text.StrongTextEncryptor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author boggi
 */
public class PasswordDatabase {
    
    private String password = "";
    private List<PasswordUser> users = new ArrayList();

    void initialize() throws FileNotFoundException {
        LogPrinter.print("Enter password: ");
        this.password = "fdsafas4342423423424f";
        Scanner scanner = new Scanner(System.in);
        this.password = scanner.nextLine();
        if(this.password.length() < 15) {
            LogPrinter.println("Password is atleast 15 char size");
            System.exit(0);
        }
        readDb();
    }

    private void readDb() throws FileNotFoundException {
        String myText = "";
        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(this.password);
        String res = readFile();
        if(!res.isEmpty()) {
            String plainText = textEncryptor.decrypt(res);
            parsePlainText(plainText);
        } else {
            parsePlainText("");
        }
    }
    
    private String readFile() throws FileNotFoundException {
        File file = new File("pwdb.txt");
        if(!file.exists()) {
            return "";
        }
        Scanner in = new Scanner(new FileReader(file));
        return in.nextLine();
    }

    private void writeDb() throws Exception {
        PrintWriter writer = new PrintWriter("pwdb.txt", "UTF-8");
        String res = "";
        for(PasswordUser user : users) {
            res += user.toString() + "\n";
        }
        
        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(this.password);
        String myEncryptedText = textEncryptor.encrypt(res);
        writer.write(myEncryptedText);
        writer.flush();
        writer.close();
        
        File file = new File("updatedb.txt");
        file.createNewFile();
    }
    
    private void parsePlainText(String plainText) {
        if(!plainText.isEmpty()) {
            String[] lines = plainText.split("\n");
            for(String line : lines) {
                PasswordUser user = new PasswordUser(line);
                users.add(user);
            }
        }
    }

    public void printUsers() {
        LogPrinter.println("storeid;---;hostname;---;wubookPassword;---;getshopUsername;---;getshopPassword");
        for(PasswordUser user : users) {
            LogPrinter.println(user);
        }
    }
    
    void addUser(PasswordUser user) throws Exception {
        users.add(user);
        writeDb();
    }

    void removeUser(String id) throws Exception {
        List<PasswordUser> newUserList = new ArrayList();
        for(PasswordUser user : users) {
            if(user.storeId.equals(id)) {
                continue;
            }
            newUserList.add(user);
        }
        users = newUserList;
        writeDb();
    }

    void checkIfNeedRefresh() throws FileNotFoundException {
        File file = new File("updatedb.txt");
        if(file.exists()) {
            LogPrinter.println("Reloading database");
            readDb();
            file.delete();
        }
    }

    List<PasswordUser> getAllUsers() {
        return users;
    }

    PasswordUser getUser(String storeId) {
        for(PasswordUser user : users) {
            if(user.storeId.equals(storeId)) {
                return user;
            }
        }
        return null;
    }
}
