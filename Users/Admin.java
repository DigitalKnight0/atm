package Users;
import DB.Transaction;
import Exceptions.UserNotFound;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Admin extends User{
    public Admin(String login, String pin, String title)
    {
        this.accNo = db.getNewAccNo();
        this.pin = pin;
        this.login = login;
        this.title = title;
        this.isAdmin = true;
    }

    public void displayMenu()
    {
        cls();
        if(this.isDisabled)
        {
            printDisabledMessage();
            return;
        }
        boolean reprintMenu = true;
        while(reprintMenu)
        {
            cls();
            System.out.println("1 -- Add User");
            System.out.println("2 -- Delete User");
            System.out.println("3 -- Edit User");
            System.out.println("4 -- Search Accounts");
            System.out.println("5 -- View Reports");
            System.out.println("Any other number to Exit");
            int input;
            try{
                input = sc.nextInt();
            } catch (InputMismatchException e)
            {
                System.out.println("You Entered an Invalid Number");
                continue;
            }
            switch(input)
            {
                case 1:
                    reprintMenu = addUser();
                    break;
                case 2:
                    reprintMenu = removeUser();
                    break;
                case 3:
                    reprintMenu = editUser();
                    break;
                case 4:
                    reprintMenu =searchAccounts();
                    break;
                case 5:
                    reprintMenu = getReports();
                    break;
                default:
                    return;
            }
        }
    }

    public boolean addUser()
    {
        cls();
        try{
            String login;
            String pin;
            String type;
            String title;
            double balance;
            sc.nextLine();
            System.out.println("Please enter the information for new account");
            System.out.println("Please Enter the Login");
            login = sc.nextLine();
            cls();
            System.out.println("Please Enter the Pin Code");
            pin = sc.nextLine();
            cls();
            System.out.println("Please Enter the Account Type(Savings / Current)");
            type = sc.nextLine();
            cls();
            if(!(type.equals("Savings") || type.equals("savings") || type.equals("Current")
            || type.equals("current"))){
                System.out.println("Type can only by (Accounts/Savings)");
                return returnToMain();
            }
            System.out.println("Please Enter the account Title");
            title = sc.nextLine();
            cls();
            System.out.println("Please Enter the Account Balance");
            balance = sc.nextDouble();
            cls();

            User newUser = new Customer(login, pin, type, title, balance);
            System.out.println("User Added!");
            System.out.println("New Account Number: " + newUser.accNo);
            db.addUser(newUser);
        } catch (InputMismatchException e)
        {
            System.out.println("You entered an invalid number");
        }
        return returnToMain();
    }

    private boolean removeUser()
    {
        cls();
        try{
            int accNo;
            System.out.println("Please enter the account no to delete");
            accNo = sc.nextInt();
            if(!db.customerExists(accNo)){
                throw new UserNotFound("No user exists with given account number");
            }
            User user = db.getUser(accNo);
            System.out.println("You wish to delete account held by " + user.title);
            System.out.println("Re enter account number to confirm");
            int input2 = sc.nextInt();
            if(input2 != accNo)
            {
                System.out.println("Action Cancelled");
                return returnToMain();
            }
            db.remove(accNo);
        } catch (InputMismatchException e) {
            System.out.println("You Entered an invalid Number");
        } catch (UserNotFound e)
        {
            System.out.println(e);
        }
        return returnToMain();
    }

    private boolean editUser()
    {
        cls();
        try{
            int accNo;
            String login;
            String pin;
            String title;
            String balance;
            String type;
            System.out.println("Please enter the account no");
            accNo = sc.nextInt();
            sc.nextLine();
            if(!db.customerExists(accNo))
            {
                throw new UserNotFound("No Account exists with given acc Number");
            }
            Customer user = (Customer)db.getUser(accNo);
            cls();
            System.out.println("Enter new account info. Leave blank for unchanged");
            System.out.println("Please Enter the Login");
            login = sc.nextLine();
            cls();
            System.out.println("Please Enter the Pin Code");
            pin = sc.nextLine();
            cls();
            System.out.println("Please Enter the Account Type(Savings / Current)");
            type = sc.nextLine();
            cls();
            if(!(type.equals("Savings") || type.equals("savings") || type.equals("Current")
            || type.equals("current") || type.equals(""))){
                System.out.println("Type can only by (Accounts/Savings)");
                return returnToMain();
            }
            System.out.println("Please Enter the account title");
            title = sc.nextLine();
            cls();
            System.out.println("Please Enter the Account Balance");
            balance = sc.nextLine();
            cls();
            updateUser(user, accNo, login, pin, type, title, balance);
            System.out.println("User Updated Successfully!");
        } catch (UserNotFound e){
            System.out.println(e);
        } catch (InputMismatchException e)
        {
            System.out.println("You entered an invalid Number");
        }
        return returnToMain();

    }

    private void updateUser(Customer user, int accNo, String login, String pin, String type,String title, String balance)
    {
        String newLogin = login.equals("") ? user.login : login;
        String newTitle = title.equals("") ? user.title : title;
        String newType = type.equals("") ? user.type : type;
        String newPin = pin.equals("") ? user.getPin() : pin;
        String newBalance = balance.equals("") ? ("" + user.getBalance()) : balance;
        User newUser = new Customer(accNo, newLogin, newPin, newType, newTitle, Double.parseDouble(newBalance));
        db.remove(accNo);
        db.addUser(newUser);
    }

    private boolean searchAccounts()
    {
        cls();
        try{
            String accNo;
            String login;
            String title;
            String balance;
            String type;
            sc.nextLine();
            System.out.println("Please enter the account no");
            accNo = sc.nextLine();
            System.out.println("Enter Login");
            login = sc.nextLine();
            System.out.println("Please Enter the account title");
            title = sc.nextLine();
            System.out.println("Please enter the account type");
            type = sc.nextLine();
            System.out.println("Please Enter the Account Balance");
            balance = sc.nextLine();
            ArrayList<User> results = db.searchAccounts(accNo, login, type, title, balance);
            String[] resultStrings = formatSearchResults(results);
            cls();
            for(String result : resultStrings)
            {
                System.out.println(result);
            }
        } catch (InputMismatchException e)
        {
            System.out.println("You entered an Invalid Number");
        }
        return returnToMain();
    }

    private String[] formatSearchResults(ArrayList<User> results)
    {
        ArrayList<String> parsed = new ArrayList<String>();
        parsed.add("Accounts ID  User ID  Title  Type  Balance  Status");
        for(User user : results)
        {
            String status = user.isDisabled ? "Disabled" : "Active";
            Customer cUser = (Customer)user;
            parsed.add((cUser.accNo + "  " + cUser.login + "  " + cUser.title + "  " + cUser.type + "  " + cUser.getBalance() + "  " + status));
        }
        String[] parsedStrings = new String[parsed.size()];
        parsedStrings = parsed.toArray(parsedStrings);
        return parsedStrings;
    }

    private boolean getReports()
    {
        cls();
        try{
            System.out.println("1 --- Search by Date");
            System.out.println("2 --- Search by Amount");
            int input = sc.nextInt();

            if(input == 1)
            {
                getByDate();
            } 
            else {
                getByAmount();
            }
        } catch (InputMismatchException e)
        {
            System.out.println("You Entered an Invalid Number");
        }
        return returnToMain();
    }

    private void getByDate()
    {
        cls();
        System.out.println("All Date formats follow dd/MM/YYYY");
        System.out.println("Please enter the starting date");
        String from = sc.nextLine();
        System.out.println("Please enter the ending date");
        String to = sc.nextLine();
        LocalDate fromDate, toDate;
        try{
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            fromDate = LocalDate.parse(from, format);
            toDate = LocalDate.parse(to, format);
            ArrayList<Transaction> results = db.searchTransactions(fromDate, toDate);
            String[] resultStrings = formatDateReport(results);
            cls();
            for(String result : resultStrings)
            {
                System.out.println(result);
            }
        } catch (DateTimeParseException e)
        {
            System.out.println("The Given Date could not be parsed");
        }
    }

    private void getByAmount()
    {
        try{
            System.out.println("Please enter the minimum amount");
            Double min = sc.nextDouble();
            System.out.println("Please enter the maximum amount");
            Double max = sc.nextDouble();
            ArrayList<Customer> results = db.searchTransactions(min, max);
            String[] resultStrings = formatBalanceReport(results);
            cls();
            for(String result : resultStrings)
            {
                System.out.println(result);
            }
        }
        catch(InputMismatchException e) {
            System.out.println("You Enetered an Invalid Number");
        }
    }

    private String[] formatDateReport(ArrayList<Transaction> transactions)
    {
        ArrayList<String> parsed = new ArrayList<String>();
        parsed.add("Transaction Type   User ID  Holders Name   Amount   Date");
        for(Transaction T : transactions)
        {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            parsed.add((T.type + "  " + T.accNo + "  " + T.title + "  " + T.amount + "  " + T.date.format(format)));
        }
        String[] parsedStrings = new String[parsed.size()];
        parsedStrings = parsed.toArray(parsedStrings);
        return parsedStrings;
    }

    private String[] formatBalanceReport(ArrayList<Customer> customers)
    {
        ArrayList<String> parsed = new ArrayList<String>();
        parsed.add("Accounts ID  User ID  Title  Type  Balance  Status");
        for(Customer customer : customers)
        {
            String status = customer.isDisabled ? "Disabled" : "Active";
            parsed.add((customer.accNo + "  " + customer.login + "  " + customer.title + "  " + customer.type + "  " + customer.getBalance() + "  " + status));
        }
        String[] parsedStrings = new String[parsed.size()];
        parsedStrings = parsed.toArray(parsedStrings);
        return parsedStrings;
    }
}
