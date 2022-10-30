package Users;
import DB.Transaction;
import java.util.ArrayList;
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
        if(this.isDisabled)
        {
            printDisabledMessage();
            return;
        }
        System.out.println("1 -- Add User");
        System.out.println("2 -- Delete User");
        System.out.println("3 -- Edit User");
        System.out.println("4 -- Search Accounts");
        System.out.println("5 -- View Reports");

        int input = sc.nextInt();

        switch(input)
        {
            case 1:
                addUser();
                break;
            case 2:
                removeUser();
                break;
            case 3:
                editUser();
                break;
            case 4:
                searchAccounts();
                break;
            case 5:
                getReports();
                break;
        }
    }

    public void addUser()
    {
        String login;
        String pin;
        String title;
        double balance;
        sc.nextLine();
        System.out.println("Please enter the information for new account");
        System.out.println("Please Enter the Login");
        login = sc.nextLine();
        System.out.println("Please Enter the Pin Code");
        pin = sc.nextLine();
        System.out.println("Please Enter the account title");
        title = sc.nextLine();
        System.out.println("Please Enter the Account Balance");
        balance = sc.nextDouble();

        User newUser = new Customer(login, pin, title, balance);
        db.addUser(newUser);
    }

    private void removeUser()
    {
        int accNo;
        System.out.println("Please enter the account no to delete");
        accNo = sc.nextInt();
        db.remove(accNo);
    }

    private void editUser()
    {
        int accNo;
        String login;
        String pin;
        String title;
        String balance;
        System.out.println("Please enter the account no");
        accNo = sc.nextInt();
        Customer user = (Customer)db.getUser(accNo);
        sc.nextLine();
        System.out.println("Please Enter the Login");
        login = sc.nextLine();
        System.out.println("Please Enter the Pin Code");
        pin = sc.nextLine();
        System.out.println("Please Enter the account title");
        title = sc.nextLine();
        System.out.println("Please Enter the Account Balance");
        balance = sc.nextLine();
        updateUser(user, accNo, login, pin, title, balance);

    }

    private void updateUser(Customer user, int accNo, String login, String pin, String title, String balance)
    {
        String newLogin = login.equals("") ? user.login : login;
        String newTitle = title.equals("") ? user.title : title;
        String newPin = pin.equals("") ? user.getPin() : pin;
        String newBalance = balance.equals("") ? ("" + user.getBalance()) : balance;
        User newUser = new Customer(newLogin, newPin, newTitle, Double.parseDouble(newBalance));
        db.remove(accNo);
        db.addUser(newUser);
    }

    private void searchAccounts()
    {
        String accNo;
        String login;
        String title;
        String balance;
        sc.nextLine();
        System.out.println("Please enter the account no");
        accNo = sc.nextLine();
        System.out.println("Enter Login");
        login = sc.nextLine();
        System.out.println("Please Enter the account title");
        title = sc.nextLine();
        System.out.println("Please Enter the Account Balance");
        balance = sc.nextLine();
        ArrayList<User> results = db.searchAccounts(accNo, login, title, balance);
        String[] resultStrings = formatSearchResults(results);
        for(String result : resultStrings)
        {
            System.out.println(result);
        }
    }

    private String[] formatSearchResults(ArrayList<User> results)
    {
        ArrayList<String> parsed = new ArrayList<String>();
        parsed.add("Accounts ID  User ID  Title  Type  Balance  Status");
        for(User user : results)
        {
            Customer cUser = (Customer)user;
            parsed.add((cUser.accNo + "  " + cUser.login + "  " + cUser.title + "  " + cUser.getBalance()));
        }
        String[] parsedStrings = new String[parsed.size()];
        parsedStrings = parsed.toArray(parsedStrings);
        return parsedStrings;
    }

    private void getReports()
    {
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
    }

    private void getByDate()
    {
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
        System.out.println("Please enter the minimum amount");
        Double min = sc.nextDouble();
        System.out.println("Please enter the maximum amount");
        Double max = sc.nextDouble();
        ArrayList<Customer> results = db.searchTransactions(min, max);
        String[] resultStrings = formatBalanceReport(results);
            for(String result : resultStrings)
            {
                System.out.println(result);
            }
    }

    private String[] formatDateReport(ArrayList<Transaction> transactions)
    {
        ArrayList<String> parsed = new ArrayList<String>();
        parsed.add("Transaction Type   User ID  Holders Name   Amount   Date");
        for(Transaction T : transactions)
        {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            parsed.add((T.type + "  " + T.accNo + "  " + T.title + "  " + T.amount + T.date.format(format)));
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
            parsed.add((customer.accNo + "  " + customer.login + "  " + customer.title + "  " + customer.getBalance()));
        }
        String[] parsedStrings = new String[parsed.size()];
        parsedStrings = parsed.toArray(parsedStrings);
        return parsedStrings;
    }
}
