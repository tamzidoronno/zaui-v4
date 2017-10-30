/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshoppaymentservice;

import java.util.Scanner;
import javax.crypto.Cipher;

/**
 *
 * @author boggi
 */
public class GetShopPaymentService {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        PasswordDatabase db = new PasswordDatabase();
        db.initialize();
        Scanner scanner = new Scanner(System.in);
        LogPrinter.print("Specify one of the following: 1 = running mode, 2: script mode (for updating db): ");
        String res = scanner.nextLine();
        if(res.equals("2")) {
            Communicator com = new Communicator(db);
            com.printMenu();
        } else {
            LogPrinter.println("Communicator started");
            PaymentProcessor processor = new PaymentProcessor(db);
            processor.run();
        }
    }
    
}
