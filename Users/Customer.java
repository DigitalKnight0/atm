package Users;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import DB.Transaction;
import Exceptions.*;

public class Customer extends User{
    private double balance;

    public Customer(String login, String pin, String title, double balance)
    {
        this.accNo = db.getNewAccNo();
        this.login = login;
        this.pin = pin;
        this.title = title;
        this.balance = balance;
        this.isAdmin = false;
    }

    public void displayMenu()
    {
        if(this.isDisabled)
        {
            printDisabledMessage();
            return;
        }
        System.out.println("1 -- Withdraw Cash");
        System.out.println("2 -- Cash Transfer");
        System.out.println("3 -- Deposit Cash");
        System.out.println("4 -- Display Balance");
        System.out.println("0 -- Exit");

        int input = sc.nextInt();

        switch(input)
        {
            case 1:
                withdrawCash();
                break;
            case 2:
                transferCash();
                break;
            case 3:
                depositCash();
                break;
            case 4:
                displayInfo();
                break;
        }
    }

    private void withdrawCash()
    {
        System.out.println("1 -- Fast Cash");
        System.out.println("2 -- Normal Cash");

        int input = sc.nextInt();

        if(input == 1){
            fastCash();
        } else {
            normalCash();
        }
    }

    private void fastCash()
    {
        System.out.println("Please Select an Option");
        for(int i = 1; i <= 7; i++)
        {
            System.out.println(i + " " + (i * 500));
        }
        double input = sc.nextDouble() * 500;

        try{
            tryAndWithdraw(input);
            Transaction transaction = new Transaction(this.accNo, "Withdrawal", this.title, LocalDate.now(), input);
            db.addTransaction(transaction);
            System.out.println("Do you want receipt?");
            System.out.println("1 -- Yes");
            System.out.println("2 -- No");
            int input1 = sc.nextInt();
            if(input1 == 1) printReceipt(transaction);
        } catch (LowBalance e)
        {
            System.out.println(e);
        } catch (LimitExceed e)
        {
            System.out.println(e);
        }
        
    }

    private void normalCash()
    {
        System.out.println("Please Select Amount to withdraw");
        int input = sc.nextInt();

        try{
            tryAndWithdraw(input);
            Transaction transaction = new Transaction(this.accNo, "Withdrawal", this.title, LocalDate.now(), input);
            db.addTransaction(transaction);
            System.out.println("Do you want receipt?");
            System.out.println("1 -- Yes");
            System.out.println("2 -- No");
            int input1 = sc.nextInt();
            if(input1 == 1) printReceipt(transaction);
        } catch (LowBalance e)
        {
            System.out.println(e);
        } catch (LimitExceed e)
        {
            System.out.println(e);
        }
    }

    private void tryAndWithdraw(double amount) throws LowBalance, LimitExceed
    {
        if(this.balance < amount) throw new LowBalance("Your Balance is too low");
        double withdrawalsSinceYesterday = db.withdrawalsSinceYesterday(this.accNo);
        if((withdrawalsSinceYesterday + amount) > 20000)
        {
            throw new LimitExceed("You cannot withdraw more than 20000 in 24 hours");
        }
        this.balance -= amount;
    }

    private void transferCash()
    {
        int amount;
        String accNo;
        System.out.println("Please enter the amount to transfer");
        amount = sc.nextInt();
        System.out.println("Please Enter the Account Number to tranfser to");
        accNo = sc.nextLine();

        Customer account = (Customer)db.getUser(accNo);
        System.out.println("Transfer Rs." + amount + "To " + account.title);
        this.balance -= amount;
        account.depositCash(amount);
    }

    private void depositCash()
    {
        System.out.println("Please Enter Amount to Deposit");
        double amount = sc.nextDouble();
        depositCash(amount);
        Transaction transaction = new Transaction(this.accNo, "Deposit", this.title, LocalDate.now(), amount);
        db.addTransaction(transaction);
        System.out.println("Do you want receipt?");
        System.out.println("1 -- Yes");
        System.out.println("2 -- No");
        int input1 = sc.nextInt();
        if(input1 == 1) printReceipt(transaction);
    }

    public void depositCash(double amount)
    {
        this.balance += amount;
    }

    private void displayInfo()
    {
        System.out.println("Title: " + this.title);
        System.out.println("Account No: " + this.login);
        System.out.println("Balance: " + this.balance);
    }

    public double getBalance()
    {
        return this.balance;
    }

    public String getPin()
    {
        return this.pin;
    }

    private void printReceipt(Transaction t)
    {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        System.out.println("Date: " + t.date.format(format));
        System.out.println("Account Number: " + this.accNo);
        System.out.println("Type: " + t.type);
        System.out.println("Amount: " + t.amount);
        System.out.println("Balance: " + this.balance);
    }

}
