
package getshoppaymentservice;

import java.util.Scanner;

public class Communicator {
    public PasswordDatabase db;

    Communicator(PasswordDatabase db) {
        this.db = db;
    }
    
    public void printMenu() throws Exception {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            LogPrinter.println("1. Print users");
            LogPrinter.println("2. Add user");
            LogPrinter.println("3. Delete user");
            LogPrinter.println("4. Exit");
            LogPrinter.println("-----------");
            String choice = scanner.nextLine();
            switch(choice) {
                case "1":
                    db.printUsers();
                    break;
                case "2":
                    addUser();
                    break;
                case "3":
                    deleteUser();
                    break;
                case "4":
                    System.exit(0);
                    break;
            }
        }
    }

    private void addUser() throws Exception {
        PasswordUser user = new PasswordUser();
        Scanner scanner = new Scanner(System.in);
        LogPrinter.print("1/5: enter store id: ");
        user.storeId = scanner.nextLine();
        LogPrinter.print("2/5: enter hostname: ");
        user.hostname = scanner.nextLine();
        LogPrinter.print("3/5: enter getshop username: ");
        user.getshopUsername = scanner.nextLine();
        LogPrinter.print("4/5: enter getshop password: ");
        user.getshopPassword = scanner.nextLine();
        LogPrinter.print("5/5: enter wubook credit card password: ");
        user.wubookCreditCardPassword = scanner.nextLine();
        db.addUser(user);
    }

    private void deleteUser() throws Exception {
        Scanner scanner = new Scanner(System.in);
        LogPrinter.print("1/4: Enter id : ");
        String id = scanner.nextLine();
        db.removeUser(id);
    }
}
