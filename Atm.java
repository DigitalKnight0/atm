import java.util.Scanner;

import Users.*;
class Atm
{
    private User currentUser;

    public void init()
    {
        User.populateSamples();
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the Login id");
        String login = sc.nextLine();
        while(!User.UserExists(login))
        {
            System.out.println("No user exists with current id. Try again");
            login = sc.nextLine();
        }
        currentUser = User.getUser(login);
        System.out.println("Please enter the PIN to continue");
        String pin = sc.nextLine();
        int counter = 3;
        while(!currentUser.authenticate(pin))
        {
            counter--;
            System.out.println("Incorrect Pin, Try Again");
            System.out.println("Remaining Tries: " + (counter));
            pin = sc.nextLine();
            if(counter == 0)
            {
                currentUser.disableUser();
                break;
            }
        }
        currentUser.displayMenu();
        sc.close();
    }
    public static void main(String args[])
    {
        Atm atm = new Atm();
        atm.init();
    }
}