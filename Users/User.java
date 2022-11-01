package Users;
import DB.Accounts;
import java.util.Scanner;

public abstract class User {
    final static Accounts db = new Accounts();
    final Scanner sc = new Scanner(System.in);
    public int accNo;
    public String login;
    public String title;
    public boolean isAdmin;
    protected String pin;
    protected boolean isDisabled = false;
 
    public static boolean UserExists(String login)
    {
        boolean userExist = db.userExists(login);
        return userExist;
    };

    public static User getUser(String login)
    {
        return db.getUser(login);
    }

    public boolean authenticate(String pin)
    {
        return (this.pin.equals(pin));
    }

    public void disableUser()
    {
        this.isDisabled = true;
    }

    protected void printDisabledMessage()
    {
        System.out.println("This Account has been Disabled");
        System.out.println("Please contact an Admin to restore the Account");
    }

    public static void populateSamples()
    {
        Customer c1, c2, c3;
        Admin a1, a2;
        c1 = new Customer("user1", "111", "Savings", "User 1", 5000);
        db.addUser(c1);
        c2 = new Customer("user2", "222", "Current","User 2", 50000);
        db.addUser(c2);
        c3 = new Customer("user3", "user3", "Current","User 3", 89000);
        db.addUser(c3);

        a1 = new Admin("admin1", "111", "Admin 1");
        db.addUser(a1);
        a2 = new Admin("admin2", "222", "Admin 2");
        db.addUser(a2);
    }

    protected boolean returnToMain()
    {
        System.out.println("1 --- Return to Main Menu");
        System.out.println("Press any other key to exit");
        int input = sc.nextInt();
        if(input == 1) return true;
        return false;
    }

    public final void cls()
    {
    try {
        if (System.getProperty("os.name").contains("Windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        else {
            System.out.print("\033\143");
        }
    } catch (Exception ex) {}
    }

    abstract public void displayMenu();
}
